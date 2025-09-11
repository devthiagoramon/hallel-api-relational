//package br.hallel.relational.api.app.payment.checkout_pro.client;
//
//import br.hallel.relational.api.app.payment.checkout_pro.dto.CreatePreferenceRequestDTO;
//import br.hallel.relational.api.app.payment.checkout_pro.dto.CreatePreferenceResponseDTO;
//import com.mercadopago.MercadoPagoConfig;
//import com.mercadopago.client.preference.*;
//import com.mercadopago.exceptions.MPApiException;
//import com.mercadopago.exceptions.MPException;
//import com.mercadopago.resources.preference.Preference;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Slf4j
//@Component
//public class MercadoPagoClientPro {
//    //@Value("${mercadopago.access.token}")
//    private String accessToken = "";
//    //@Value("${mercadopago.notification.url}")
//    private String notificationUrl = "";
//
//    @PostConstruct
//    public void init() {
//        log.info("Iniciando Mercado Pago");
//        MercadoPagoConfig.setAccessToken(accessToken);
//    }
//
//    public CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO dto, String orderNumber) {
//        log.info("Criando preferência de pagamento no Mercado Pago com dados: {}", dto);
//        try {
//            PreferenceClient preferenceClient = new PreferenceClient();
//
//            List<PreferenceItemRequest> items = dto.items().stream()
//                    .map(item -> PreferenceItemRequest.builder()
//                            .id(item.id())
//                            .title(item.title())
//                            .stockQuantity(item.stockQuantity())
//                            .unitPrice(item.unitPrice())
//                            .build())
//                    .collect(Collectors.toList());
//
//            PreferencePayerRequest payer = PreferencePayerRequest.builder()
//                    .email(dto.payer().email())
//                    .name(dto.payer().name())
//                    .build();
//
//            PreferenceBackUrlsRequest backUrlsRequest = PreferenceBackUrlsRequest.builder()
//                    .success(dto.backUrls().success())
//                    .failure(dto.backUrls().failure())
//                    .pending(dto.backUrls().pending())
//                    .build();
//
//            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
//                    .items(items)
//                    .payer(payer)
//                    .backUrls(backUrlsRequest)
//                    .notificationUrl(notificationUrl)
//                    .externalReference(orderNumber)
//                    .autoReturn("approved")
//                    .build();
//
//            Preference preference = preferenceClient.create(preferenceRequest);
//            log.info("Preferência de pagamento criada com sucesso no Mercado Pago");
//            return new CreatePreferenceResponseDTO(
//                    preference.getId(),
//                    preference.getInitPoint()
//            );
//        } catch (MPException e) {
//            throw new RuntimeException(e);
//        } catch (MPApiException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//}
