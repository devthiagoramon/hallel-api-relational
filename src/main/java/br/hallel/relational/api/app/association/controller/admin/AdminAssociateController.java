package br.hallel.relational.api.app.association.controller.admin;


import br.hallel.relational.api.app.association.dto.AssociateResponse;
import br.hallel.relational.api.app.association.dto.AssociateWithUserResponse;
import br.hallel.relational.api.app.association.dto.CreateAssociateRequestDTO;
import br.hallel.relational.api.app.association.exception.AssociateException;
import br.hallel.relational.api.app.association.model.AssociatePaymentStatus;
import br.hallel.relational.api.app.association.service.AssociateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/association")
@RequiredArgsConstructor
@Tag(name = "Associate admin routes",
        description = "Admin routes for associates")
public class AdminAssociateController {

    private final AssociateService associateService;

    @PostMapping("/{userId}")
    @Operation(summary = "Associate a user to associate system", description = "Handles when admin wants to associate a new user in system")
    public ResponseEntity<AssociateResponse> associateNewUser(@PathVariable("userId") UUID userId, @RequestBody
    CreateAssociateRequestDTO dto) {
        return ResponseEntity.ok(this.associateService.createAssociation(dto, userId));
    }

    @DeleteMapping("/{associationId}")
    @Operation(summary = "Removes some associate from system", description = "Handles when admin wants to remove some associate form association system")
    public ResponseEntity<Boolean> removeUserFromAssociation(@PathVariable("associationId") UUID associationId) {
        return ResponseEntity.ok(this.associateService.removeAssociation(associationId));
    }

    @GetMapping("/all")
    @Operation(summary = "Listing all associated of system", description = "Handles listing the associated in system as admin")
    public ResponseEntity<Page<AssociateWithUserResponse>> listAllAssociateWithUserResponse(
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        return ResponseEntity.ok(
                this.associateService.listAllAssociateWithUserResponseService(PageRequest.of(page, size)));
    }

    @GetMapping("/all/by-status")
    @Operation(summary = "Listing all associated of system with status filtered", description = "Handles listing the associated in system with status filter")
    public ResponseEntity<Page<AssociateWithUserResponse>> listAllAssociateWithUserResponseFiltered(
            @RequestParam(name = "filter", defaultValue = "undefined") String filter,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "page", defaultValue = "0") int page) {
        AssociatePaymentStatus status = null;
        if (!filter.equalsIgnoreCase("undefined")) {
            status = AssociatePaymentStatus.valueOf(filter.toUpperCase());
        } else {
            throw new AssociateException("Can't filter the associated by this status");
        }
        return ResponseEntity.ok(this.associateService.listAllASsociateWithUserResponseFilteredByPaymentStatus(status,
                PageRequest.of(page, size)));
    }


}
