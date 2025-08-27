//package br.hallel.relational.api.app.payment.checkout_pro.service;
//
//import br.hallel.relational.api.app.payment.checkout_pro.client.MercadoPagoClientPro;
//import br.hallel.relational.api.app.payment.checkout_pro.dto.CreatePreferenceRequestDTO;
//import br.hallel.relational.api.app.payment.checkout_pro.dto.CreatePreferenceResponseDTO;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//
//@Service
//@Slf4j
//public class CreatePaymentPreferenceService {
//    private final MercadoPagoClientPro mercadoPagoClientPro;
//
//    public CreatePaymentPreferenceService(MercadoPagoClientPro mercadoPagoClientPro) {
//        this.mercadoPagoClientPro = mercadoPagoClientPro;
//    }
//
//    public CreatePreferenceResponseDTO createPreference(CreatePreferenceRequestDTO dto) {
//        log.info("Creating payment preference with request: {}", dto);
//
//        String orderNumber = String.valueOf(Math.random());
//        try{
//            return mercadoPagoClientPro.createPreference(dto,orderNumber);
//        }catch (Exception e) {
//            log.error(e.getMessage());
//        }
//
//        return null;
//    }
//}
