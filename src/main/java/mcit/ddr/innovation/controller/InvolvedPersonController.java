package mcit.ddr.innovation.controller;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.entity.InvolvedPerson;
import mcit.ddr.innovation.service.InvolvedPersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/involved-person")
@RequiredArgsConstructor
public class InvolvedPersonController {

    private final InvolvedPersonService involvedPersonService;

    @PostMapping("/{innovationId}")
    public ResponseEntity<InvolvedPerson> addInvolvedPerson(@PathVariable Long innovationId,
                                                            @RequestBody InvolvedPerson involvedPerson) {
        return ResponseEntity.ok(involvedPersonService.addInvolvedPerson(innovationId, involvedPerson));
    }

    @GetMapping("/{innovationId}")
    public ResponseEntity<List<InvolvedPerson>> getInvolvedPersonsByInnovation(@PathVariable Long innovationId) {
        return ResponseEntity.ok(involvedPersonService.getInvolvedPersonsByInnovation(innovationId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvolvedPerson(@PathVariable Long id) {
        involvedPersonService.deleteInvolvedPerson(id);
        return ResponseEntity.noContent().build();
    }
}