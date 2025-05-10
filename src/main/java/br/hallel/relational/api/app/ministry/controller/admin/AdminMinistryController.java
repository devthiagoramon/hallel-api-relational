package br.hallel.relational.api.app.ministry.controller.admin;

import br.hallel.relational.api.app.ministry.dto.MinistryRequestDTO;
import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.model.Ministry;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/ministry")
@RequiredArgsConstructor
public class AdminMinistryController {

    @Autowired
    private MinistryService service;

    @PostMapping("/create")
    public ResponseEntity<MinistryResponse> createMinistry(
            @RequestPart(name = "request") MinistryRequestDTO ministry,
            @RequestPart(name = "image") MultipartFile image) {
        return ResponseEntity.ok(this.service.createMinistry(ministry, image));
    }

    @PostMapping("/edit/{id}")
    public ResponseEntity<MinistryResponse> editMinistry(
            @PathVariable(name = "id") UUID id,
            @RequestPart(name = "request") MinistryRequestDTO ministry,
            @RequestPart(name = "image") MultipartFile image) {
        return ResponseEntity.ok(this.service.createMinistry(ministry, image));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteEvent(@PathVariable(name = "id") UUID id) {
        this.service.deleteMinistryById(id);

        return ResponseEntity.ok("Evento Deletado com sucesso!");
    }

}
