package br.hallel.relational.api.app.association.controller.user;

import br.hallel.relational.api.app.association.dto.AssociatePayDetails;
import br.hallel.relational.api.app.association.dto.AssociateResponse;
import br.hallel.relational.api.app.association.dto.AssociationPaymentResponse;
import br.hallel.relational.api.app.association.dto.CreateAssociateRequestDTO;
import br.hallel.relational.api.app.association.service.AssociateService;
import br.hallel.relational.api.app.security.utils.JwtTokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/user/association")
@RequiredArgsConstructor
@Tag(name = "Associate user routes",
        description = "User routes for associate usage")
public class UserAssociateController {
    private final AssociateService associateService;
    private final JwtTokenProvider jwtTokenProvider;


    @PostMapping("/join")
    public ResponseEntity<AssociateResponse> createAssociate(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody CreateAssociateRequestDTO dto
    ) {
        return ResponseEntity.ok(this.associateService.createAssociation(dto, jwtTokenProvider.getUserId(authorizationHeader)));
    }

    @GetMapping("/pay")
    public ResponseEntity<AssociatePayDetails> payAssociation(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return ResponseEntity.ok
                (this.associateService.payAnAssociation(jwtTokenProvider.getUserId(authorizationHeader)));
    }

    @GetMapping("/list-all/payments")
    public ResponseEntity<List<AssociationPaymentResponse>> listAllPayments(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        return ResponseEntity.ok
                (this.associateService.listAllPayments(jwtTokenProvider.getUserId(authorizationHeader)));
    }

    @GetMapping("/get/user-info")
    public ResponseEntity<AssociateResponse> getAssociateInfo(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.associateService.getAssociateInfoByUserId(userId));
    }

    @GetMapping("/verify-if-is-associate")
    @Operation(summary = "Verify if user is associated in system", description = "Handles verifing if user is associated")
    public ResponseEntity<Boolean> verifyIfIsAssociate(@RequestHeader("Authorization") String authorizationHeader){
        UUID userId = jwtTokenProvider.getUserId(authorizationHeader);
        return ResponseEntity.ok(this.associateService.verifyIfUserIsAssociated(userId));
    }

}
