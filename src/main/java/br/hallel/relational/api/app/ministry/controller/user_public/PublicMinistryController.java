package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.MinistryResponse;
import br.hallel.relational.api.app.ministry.service.MinistryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/public/ministry")
@RequiredArgsConstructor
public class PublicMinistryController {

    @Autowired
    private MinistryService service;

    @GetMapping("/list-all")
    public ResponseEntity<List<MinistryResponse>> litAllMinistries(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size

    ) {
        return ResponseEntity.ok(this.service.listAllMinistries(page, size));
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<MinistryResponse> getMinistryById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(this.service.getMinistryById(id));
    }
}
