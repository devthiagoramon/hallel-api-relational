package br.hallel.relational.api.app.association.service;

import br.hallel.relational.api.app.association.dto.AssociatePayDetails;
import br.hallel.relational.api.app.association.dto.AssociateResponse;
import br.hallel.relational.api.app.association.dto.AssociationPaymentResponse;
import br.hallel.relational.api.app.association.dto.CreateAssociateRequestDTO;
import br.hallel.relational.api.app.association.exception.AssociateException;
import br.hallel.relational.api.app.association.exception.AssociateNotFoundException;
import br.hallel.relational.api.app.association.exception.UserAlreadyAssociatedException;
import br.hallel.relational.api.app.association.model.Associate;
import br.hallel.relational.api.app.association.model.AssociationPayment;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.association.model.PaymentMethod;
import br.hallel.relational.api.app.association.repository.AssociatePaymentRepository;
import br.hallel.relational.api.app.association.repository.AssociateRepository;
import br.hallel.relational.api.app.event.dto.PaymentStatusDTO;
import br.hallel.relational.api.app.event.exception.UserValidationException;
import br.hallel.relational.api.app.event.model.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.payment.checkout_transparent.client.MercadoPagoClient;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import br.hallel.relational.api.app.user.exceptions.UserNotFoundException;
import br.hallel.relational.api.app.user.model.User;
import br.hallel.relational.api.app.user.repository.UserRepository;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssociateService {
    private final UserRepository userRepository;
    private final AssociateRepository associateRepository;
    private final MercadoPagoClient mercadoPagoClient;
    private final SimpMessagingTemplate template;
    private final AssociatePaymentRepository associatePaymentRepository;
    private final EntityManager entityManager;
    private static final Locale PT_BR = new Locale("pt", "BR");

    @Transactional
    public AssociateResponse createAssociation(CreateAssociateRequestDTO dto, UUID userId) {
        boolean alreadyAssociate = associateRepository.existsAssociateByUser_Id(userId);
        if (alreadyAssociate) {
            log.warn("Associate already associated with user id {}", userId);
            throw new UserAlreadyAssociatedException("associate.already.exists");
        }

        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        if (user.getCpf() == null || user.getCpf().isEmpty()) {
            throw new UserValidationException("User CPF is required to make the payment.");
        }

        Associate associate = new Associate();
        associate.setUser(user);
        associate.setAssociateSince(LocalDateTime.now());
        associate.setStatus(AssociatePaymentStatus.PENDENTE);
        associate.setValueAssociation(dto.valueAssociation());

        this.entityManager.persist(associate);

        // 3. Force a sincronização, para que a próxima operação (payment)
        // tenha certeza de que o associate existe no banco de dados
        this.entityManager.flush();

        Associate associateSaved = associate;
        if (dto.paymentMethod() == PaymentMethod.PIX) {
            AssociationPayment paymentTransaction = createInitialPaymentTransaction(associateSaved, dto);
            processPixPayment(user, paymentTransaction, dto);
        }

        return AssociateResponse.toResponse(associateSaved);
    }

    public AssociatePayDetails payAnAssociation(UUID userId) {
        User user = this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        Associate associate = this.associateRepository.findByUser_Id(userId).orElseThrow(
                () -> new AssociateNotFoundException("associate.not.found")
        );

        AssociationPayment lastPendingPayment = associatePaymentRepository
                .findTopByAssociateAndStatusOrderByPaidDateDesc(associate, AssociatePaymentStatus.PENDENTE)
                .orElseThrow(() -> new AssociateException("No pending PIX transaction found for this associate."));

        if (lastPendingPayment.getPixTxid() == null || lastPendingPayment.getMercadoPagoPaymentId() == null) {
            throw new AssociateException("Pending payment details are incomplete.");
        }

        String qrCodeBase64 = null;
        try {
            qrCodeBase64 = mercadoPagoClient.getPaymentQRCode(lastPendingPayment.getMercadoPagoPaymentId());

            template.convertAndSend("/topic/payments/" + user.getId(),
                    new PaymentStatusDTO(qrCodeBase64, lastPendingPayment.getPixTxid(),
                            StatusPaymentEventParticipation.PENDENTE));
        } catch (Exception e) {
            log.error("Failed to retrieve Mercado Pago QR Code for payment ID {}",
                    lastPendingPayment.getMercadoPagoPaymentId(), e);
            throw new RuntimeException("Failed to retrieve payment details.", e);
        }

        return new AssociatePayDetails(
                qrCodeBase64,
                lastPendingPayment.getPixTxid(),
                lastPendingPayment.getValuePaid().doubleValue(),
                lastPendingPayment.getStatus()
        );
    }

    public List<AssociationPaymentResponse> listAllPayments(UUID userId) {
        this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        Associate associate = this.associateRepository.findByUser_Id(userId).orElseThrow(
                () -> new AssociateNotFoundException("associate.not.found")
        );

        List<AssociationPayment> byAssociate = this.associatePaymentRepository.findByAssociate(associate);
        List<AssociationPaymentResponse> response =
                byAssociate.stream().map(AssociationPaymentResponse::toResponse).toList();
        return response;
    }

    public AssociateResponse getAssociateInfoByUserId(UUID userId) {
        this.userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("user.not.found", userId.toString())
        );

        Associate associate = this.associateRepository.findByUser_Id(userId).orElseThrow(
                () -> new AssociateNotFoundException("associate.not.found")
        );

        return AssociateResponse.toResponse(associate);
    }

    private void processPixPayment(User user, AssociationPayment paymentTransaction,
                                   CreateAssociateRequestDTO dto) {
        String fullName = user.getName();
        String firstName = "";
        String lastName = "";

        if (fullName != null && !fullName.isEmpty()) {
            String[] names = fullName.split(" ");
            if (names.length > 0) {
                firstName = names[0];
            }
            if (names.length > 1) {
                lastName = String.join(" ", java.util.Arrays.copyOfRange(names, 1, names.length));
            }
        }
        CreatePixPaymentRequestDTO paymentRequestDTO = new CreatePixPaymentRequestDTO(
                BigDecimal.valueOf(dto.valueAssociation()),
                buildDescription(),
                user.getEmail(),
                firstName,
                lastName,
                user.getCpf()
        );

        try {
            Payment mpPayment = mercadoPagoClient.createPixPayment(paymentRequestDTO, user.getId());

            if (mpPayment != null && mpPayment.getPointOfInteraction() != null &&
                    mpPayment.getPointOfInteraction().getTransactionData() != null) {

                String qrCodeTxid = mpPayment.getPointOfInteraction().getTransactionData().getQrCode();
                String qrCodeBase64 = mpPayment.getPointOfInteraction().getTransactionData().getQrCodeBase64();


                paymentTransaction.setPixTxid(qrCodeTxid);
                paymentTransaction.setMercadoPagoPaymentId(mpPayment.getId());
                paymentTransaction.setStatus(AssociatePaymentStatus.PENDENTE); // Mantém pendente até o webhook

                associatePaymentRepository.save(paymentTransaction); // Salva as informações do Mercado Pago

                template.convertAndSend("/topic/payments/" + user.getId(),
                        new PaymentStatusDTO(qrCodeBase64, qrCodeTxid,
                                StatusPaymentEventParticipation.PENDENTE));

                log.info("Pagamento Pix criado com sucesso para o usuário ID {}. TXID: {}", user.getId(), qrCodeTxid);

            } else {
                log.error(
                        "Resposta do Mercado Pago incompleta, dados de transação ou de interação nulos para user ID {}.",
                        user.getId());
                throw new RuntimeException("Erro ao processar a resposta do Mercado Pago. Dados incompletos.");
            }

        } catch (MPException | MPApiException e) {
            String errorMessage = "Erro na integração com o Mercado Pago (PIX) para user ID " + user.getId();
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }


    private String buildDescription() {
        String currentMonthName = Month.of(LocalDateTime.now().getMonthValue())
                .getDisplayName(java.time.format.TextStyle.FULL, PT_BR);

        return String.format("Ingressando à Associação da comunidade católica Hallel - %s", currentMonthName);
    }

    private AssociationPayment createInitialPaymentTransaction(Associate associate, CreateAssociateRequestDTO dto) {
        AssociationPayment payment = new AssociationPayment();
        payment.setAssociate(associate);
        payment.setValuePaid(BigDecimal.valueOf(dto.valueAssociation()));
        payment.setPaymentMethod(dto.paymentMethod());
        payment.setMonthsCovered(1);
        payment.setStatus(AssociatePaymentStatus.PENDENTE);

        return associatePaymentRepository.save(payment);
    }

    public Boolean verifyIfUserIsAssociated(UUID userId) {
        Optional<Associate> associateOptional = this.associateRepository.findByUser_Id(userId);
        return associateOptional.isPresent();
    }
}