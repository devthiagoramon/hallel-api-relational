package br.hallel.relational.api.app.payment.checkout_transparent.client;

import br.hallel.relational.api.app.event.exception.PaymentRefundException;
import br.hallel.relational.api.app.global.pdf.PdfGenerationService;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreateCardPaymentRequestDTO;
import br.hallel.relational.api.app.payment.checkout_transparent.dto.CreatePixPaymentRequestDTO;
import br.hallel.relational.api.app.payment.pix_config.repository.PixConfigRepository;
import br.hallel.relational.api.app.user.model.User;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.common.IdentificationRequest;
import com.mercadopago.client.payment.*;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.payment.PaymentPointOfInteraction;
import com.mercadopago.resources.payment.PaymentRefund;
import com.mercadopago.resources.payment.PaymentTransactionData;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Component
public class MercadoPagoClient {

    @Value("${mercadopago.access.token}")
    private String accessToken;

    @Value("${mercadopago.notification.url}")
    private String notificationUrl;

    @Autowired
    private PdfGenerationService pdfGenerationService;

    @Autowired
    private PixConfigRepository pixConfigRepository;

    @PostConstruct
    public void init() {
        log.info("Iniciando Mercado Pago");
        MercadoPagoConfig.setAccessToken(accessToken);
    }

    private String getEffectiveAccessToken() {
        return pixConfigRepository.findByAtivoTrue()
                .map(config -> config.getMercadoPagoAccessToken())
                .filter(token -> token != null && !token.isBlank())
                .orElse(this.accessToken);
    }

    public Payment createPixPayment(CreatePixPaymentRequestDTO dto, UUID generatedPaymentId) throws MPException, MPApiException {

        log.info("Criando pagamento Pix no Mercado Pago com dados: {}", dto);

        String externalReference = generatedPaymentId.toString();

        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                .email(dto.payerEmail())
                .firstName(dto.payerFirstName())
                .lastName(dto.payerLastName())
                .identification(
                        IdentificationRequest.builder()
                                .type("CPF")
                                .number(dto.payerIdentificationNumber())
                                .build())
                .build();


        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(dto.amount())
                .description(dto.description())
                .paymentMethodId("pix")
                .payer(payerRequest)
                .externalReference(externalReference)
                .notificationUrl(notificationUrl)
                .build();

        MPRequestOptions options = MPRequestOptions.builder()
                .accessToken(getEffectiveAccessToken())
                .build();
        Payment payment = client.create(createRequest, options);
        log.info("Pagamento Pix criado com sucesso no Mercado Pago");

        return payment;
    }

    public String getPaymentQRCode(long paymentId) throws MPException, MPApiException {
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);

        return payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
    }

    public byte[] generatePDFReceiptPayment(long paymentId, User user) throws MPException, MPApiException, IOException{
        PaymentClient client = new PaymentClient();
        Payment paymentDetails = client.get(paymentId);
        return pdfGenerationService.generatePdfFromPayment(paymentDetails, user);
    }

    public String generateBase64ReceiptPayment(long paymentId, User user) throws MPException, MPApiException, IOException {
        PaymentClient client = new PaymentClient();
        Payment paymentDetails = client.get(paymentId);
        int WIDTH = 400;
        int HEIGHT = 500;
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();


        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);


        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);


        g2d.setColor(Color.BLACK);


        int y = 40; // Posição vertical inicial

        // Título
        g2d.setFont(new Font("SansSerif", Font.BOLD, 20));
        g2d.drawString("Comprovante de Pagamento", 55, y);
        y += 40;

        // Linha separadora
        g2d.drawLine(20, y - 20, WIDTH - 20, y - 20);

        // Detalhes do pagamento
        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        DecimalFormat currencyFormatter = new DecimalFormat("'R$' #,##0.00");

        g2d.drawString("ID da Transação:", 20, y);
        g2d.drawString(paymentDetails.getId().toString(), 180, y);
        y += 20;

        g2d.drawString("Data:", 20, y);
        g2d.drawString(paymentDetails.getDateApproved().format(formatter), 180, y);
        y += 20;

        g2d.drawString("Valor:", 20, y);
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));
        g2d.drawString(currencyFormatter.format(paymentDetails.getTransactionAmount()), 180, y);
        y += 20;

        g2d.setFont(new Font("Monospaced", Font.PLAIN, 12));
        g2d.drawString("Status:", 20, y);
        g2d.drawString(paymentDetails.getStatus().toUpperCase(), 180, y);
        y += 30;

        // Detalhes do Pagador
        g2d.drawString("Pagador:", 20, y);
        g2d.drawString(user.getName(), 180, y);
        y += 20;

        g2d.drawString("Email:", 20, y);
        g2d.drawString(user.getEmail(), 180, y);
        y += 20;

        // Adicione aqui um logo se desejar!

        // Rodapé
        g2d.setFont(new Font("SansSerif", Font.ITALIC, 10));
        g2d.drawString("Comprovante gerado por Hallel.", 130, HEIGHT - 20);

        // 4. Finaliza o "desenho"
        g2d.dispose();

        // 5. Converte a imagem gerada para um array de bytes (PNG)
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageBytes = baos.toByteArray();

        // 6. Codifica o array de bytes em uma string Base64 e retorna
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public Payment getPaymentStatus(long paymentId) throws MPException, MPApiException {
        log.info("Buscando status do pagamento Pix com ID: {}", paymentId);
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(paymentId);
        log.info("Status do pagamento Pix obtido com sucesso: {}", payment.getStatus());
        return payment;
    }

    public String getPixReceiptUrl(long paymentId) throws MPException, MPApiException {
        log.info("Buscando comprovante do pagamento Pix com ID: {}", paymentId);

        Payment payment = getPaymentStatus(paymentId);

        if (payment != null && "approved".equals(payment.getStatus())) {
            PaymentPointOfInteraction pointOfInteraction = payment.getPointOfInteraction();
            if (pointOfInteraction != null) {
                PaymentTransactionData transactionData = pointOfInteraction.getTransactionData();
                if (transactionData != null) {
                    String ticketUrl = transactionData.getTicketUrl();
                    if (ticketUrl != null) {
                        return ticketUrl;
                    }
                }
            }
        }
        log.warn("Comprovante não encontrado ou pagamento não aprovado para o ID: {}", paymentId);
        return null;
    }

    public boolean requestRefund(Long paymentId, Double amount) throws PaymentRefundException {
        log.info("Attempting to refund paymentId: {} for amount R${}", paymentId, amount);

        PaymentRefundClient client = new PaymentRefundClient();

        try {
            PaymentRefund refund = client.refund(paymentId);

            if (refund.getId() != null) {
                log.info("Refund successfully processed for paymentId: {}. Refund ID: {}", paymentId, refund.getId());
                return true;
            } else {
                log.error("Refund failed for paymentId: {}. No refund ID returned.", paymentId);
                throw new PaymentRefundException("Mercado Pago API did not return a refund ID.");
            }
        } catch (MPApiException e) {
            log.error("Mercado Pago API error while refunding paymentId {}. Status: {}, Content: {}",
                    paymentId, e.getApiResponse().getStatusCode(), e.getApiResponse().getContent());
            throw new PaymentRefundException("Mercado Pago API Error: " + e.getApiResponse().getContent());
        } catch (MPException e) {
            log.error("Mercado Pago SDK error while refunding paymentId {}. Message: {}", paymentId, e.getMessage());
            throw new PaymentRefundException("Mercado Pago SDK Error: " + e.getMessage());
            // Retorne false para indicar que o reembolso não foi bem-sucedido
        }
    }

    public Payment createFoodPixPayment(BigDecimal amount, String description, UUID transactionId) throws MPException, MPApiException {
        log.info("Criando pagamento Pix para venda de alimentos. Valor: {}", amount);

        PaymentPayerRequest payerRequest = PaymentPayerRequest.builder().email("pagador_generico@email.com").build();

        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(amount)
                .description(description)
                .paymentMethodId("pix")
                .payer(payerRequest)
                .externalReference(transactionId.toString())
                .notificationUrl(notificationUrl)
                .build();

        PaymentClient client = new PaymentClient();
        MPRequestOptions options = MPRequestOptions.builder()
                .accessToken(getEffectiveAccessToken())
                .build();
        Payment payment = client.create(createRequest, options);

        log.info("Pagamento Pix para alimentos criado com sucesso. ID: {}", payment.getId());

        return payment;
    }

    public Payment createCardPayment(CreateCardPaymentRequestDTO dto, UUID userId) throws MPException, MPApiException {
        log.info("Criando pagamento com Cartão no Mercado Pago. Valor: {}", dto.amount());

        String externalReference = userId.toString();
        PaymentClient client = new PaymentClient();

        PaymentPayerRequest payerRequest = PaymentPayerRequest.builder()
                .email(dto.payerEmail())
                .firstName(dto.payerFirstName())
                .lastName(dto.payerLastName())
                .identification(
                        IdentificationRequest.builder()
                                .type("CPF")
                                .number(dto.payerIdentificationNumber())
                                .build())
                .build();

        PaymentCreateRequest createRequest = PaymentCreateRequest.builder()
                .transactionAmount(dto.amount())
                .description(dto.description())
                .token(dto.token()) // O token gerado pelo frontend
                .installments(dto.installments()) // O número de parcelas
                .paymentMethodId(dto.paymentMethodId()) // O ID do método (visa, master, etc.)
                .payer(payerRequest)
                .externalReference(externalReference)
                .notificationUrl(notificationUrl)
                .build();

        // 3. Criação do Pagamento
        MPRequestOptions options = MPRequestOptions.builder()
                .accessToken(getEffectiveAccessToken())
                .build();
        Payment payment = client.create(createRequest, options);
        log.info("Pagamento com Cartão criado com sucesso no Mercado Pago. Status: {}", payment.getStatus());

        return payment;
    }



    public String getPaymentPixCode(Long mercadoPagoPaymentId) throws MPException, MPApiException {
        PaymentClient client = new PaymentClient();
        Payment payment = client.get(mercadoPagoPaymentId);

        return payment.getPointOfInteraction().getTransactionData().getQrCodeBase64();
    }
}
