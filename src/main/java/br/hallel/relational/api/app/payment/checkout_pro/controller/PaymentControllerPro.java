//package br.hallel.relational.api.app.payment.checkout_pro.controller;
//
//import br.hallel.relational.api.app.payment.checkout_pro.dto.CreatePreferenceRequestDTO;
//import br.hallel.relational.api.app.payment.checkout_pro.dto.CreatePreferenceResponseDTO;
//import br.hallel.relational.api.app.payment.checkout_pro.service.CreatePaymentPreferenceService;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/public/checkout-pro/payments")
//@RequiredArgsConstructor
//@Slf4j
//public class PaymentControllerPro {
//
//    @Autowired
//    private CreatePaymentPreferenceService service;
//
//    @PostMapping()
//    public ResponseEntity<CreatePreferenceResponseDTO> createPreference(@Valid @RequestBody CreatePreferenceRequestDTO requestDTO) {
//        log.info("Creating payment preference...");
//        CreatePreferenceResponseDTO response = this.service.createPreference(requestDTO);
//
//        return ResponseEntity.ok(response);
//    }
//}
