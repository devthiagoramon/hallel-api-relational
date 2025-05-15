package br.hallel.relational.api.app.ministry.controller.coordinator;

import br.hallel.relational.api.app.ministry.dto.FunctionMinistryDTO;
import br.hallel.relational.api.app.ministry.dto.FunctionMinistryResponse;
import br.hallel.relational.api.app.ministry.service.FunctionMinistryMemberService;
import br.hallel.relational.api.app.ministry.service.FunctionMinistryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/coordinator/ministry/function-ministry")
@Tag(name = "Function Ministry - Coordinator", description = "Coordinator part for function ministry managment")
@RequiredArgsConstructor
public class CoordinatorFunctionMinistryController {

    private final FunctionMinistryService functionMinistryService;


    @PostMapping("/add/{ministry-id}")
    @Operation(
            summary = "Create a function ministry",
            description = "Create a new function ministry that serves for one ministry just passing ministry-id and the information of function"
    )
    public ResponseEntity<FunctionMinistryResponse> createFunctionMinistry(@PathVariable("ministry-id") UUID ministryId, @RequestBody FunctionMinistryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(functionMinistryService.addNewFunctionIntoMinistry(ministryId, dto));
    }

    @PutMapping("/edit/{function-ministry-id}")
    @Operation(
            summary = "Edit a function ministry",
            description = "Edit a function ministry that serves for one ministry just passing function id and the information of function that you wanna change"
    )
    public ResponseEntity<FunctionMinistryResponse> editFunctionMinistry(@PathVariable("function-ministry-id") UUID functionMinistryId, @RequestBody FunctionMinistryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(functionMinistryService.editFunctionMinistryById(functionMinistryId, dto));
    }

    @DeleteMapping("/delete/{function-ministry-id}")
    @Operation(
            summary = "Delete a function ministry",
            description = "Delete a function ministry that serves for one ministry"
    )
    public ResponseEntity<?> deleteFunctionMinistry(@PathVariable("function-ministry-id") UUID functionMinistryId) {
        functionMinistryService.deleteFunctionMinistryById(functionMinistryId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
