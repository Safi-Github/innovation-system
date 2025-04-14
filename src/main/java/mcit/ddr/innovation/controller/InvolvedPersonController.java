package mcit.ddr.innovation.controller;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.InnovationSearchCriteriaDTO;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.InvolvedPerson;
import mcit.ddr.innovation.service.InvolvedPersonService;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/involved-person")
@RequiredArgsConstructor
public class InvolvedPersonController {

    private final InvolvedPersonService involvedPersonService;

    //create invovled person api
    @PostMapping("/{innovationId}")
    public ResponseEntity<InvolvedPerson> addInvolvedPerson(@PathVariable Long innovationId,
                                                            @RequestBody InvolvedPerson involvedPerson) {
        InvolvedPerson addedInvolvedPerson = involvedPersonService.addInvolvedPerson(innovationId, involvedPerson);
        // Return a ResponseEntity with the added InvolvedPerson
        return ResponseEntity.ok(addedInvolvedPerson);
    }

    // update & partial updated of involved person
    @PatchMapping("/{id}")
    public ResponseEntity<InvolvedPerson> updateInvolvedPerson(@PathVariable Long id,
                                                               @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(involvedPersonService.updateInvolvedPerson(id, updates));
    }

    @GetMapping
    public ResponseEntity<List<InvolvedPerson>> searchInvolvedPersons() {
        List<InvolvedPerson> result = involvedPersonService.searchInvolvedPersons();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-nid/{nid}")
    public ResponseEntity<Optional<InvolvedPerson>> getInvolvedPersonByNID(@PathVariable String nid) {
        return ResponseEntity.ok(involvedPersonService.getInvolvedPersonByNID(nid));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<InvolvedPerson>> getInvolvedPersonsById(@PathVariable Long id) {
        return ResponseEntity.ok(involvedPersonService.getInvolvedPersonById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInvolvedPerson(@PathVariable Long id) {
        involvedPersonService.deleteInvolvedPerson(id);
        return ResponseEntity.noContent().build();
    }


}