package br.hallel.relational.api.app.ministry.controller.user_public;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse;
import br.hallel.relational.api.app.ministry.service.FunctionMinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/user/ministry/function-ministry")
@RestController
@RequiredArgsConstructor
@Tag(name = "User function ministry", description = "User part for function ministry information")
public class UserFunctionMinistryController {

    private final FunctionMinistryService functionMinistryService;

    @GetMapping("/list/{ministry-id}")
    @Operation(
            summary = "List functions of ministry",
            description = "Route to list all the functions of the ministry"
    )
    public ResponseEntity<List<FunctionMinistryResponse>> listAllFunctionInOneMinistry(@PathVariable("ministry-id") UUID ministryId) {
        return ResponseEntity.ok().body(this.functionMinistryService.getAllFunctionsInMinistry(ministryId));
    }
}
