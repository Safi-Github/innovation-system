package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.dto.CommitteeDTO;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.service.CommitteeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/committee")
public class CommitteeController {

    private final CommitteeService committeeService;

    public CommitteeController(CommitteeService committeeService) {
        this.committeeService = committeeService;
    }


    @PostMapping("/add")
    public ResponseEntity<Committee> createCommittee(@RequestBody CommitteeDTO dto) {
        Committee saved = committeeService.createCommittee(dto);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}/name")
    public ResponseEntity<Committee> updateCommitteeName(@PathVariable Long id, @RequestParam String name) {
        Committee updated = committeeService.updateCommitteeName(id, name);
        return ResponseEntity.ok(updated);
    }
    //open/close a committee
    @PutMapping("/{id}/status")
    public ResponseEntity<Committee> toggleCommitteeStatus(@PathVariable Long id, @RequestParam boolean isClosed) {
        Committee updated = committeeService.setCommitteeClosed(id, isClosed);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCommittee(@PathVariable Long id) {
        committeeService.deleteCommittee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<Committee> getAllCommittees() {
        return committeeService.getAllCommittees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Committee> getCommitteeById(@PathVariable Long id) {
        return ResponseEntity.ok(committeeService.getCommitteeById(id));
    }

    @GetMapping("/{id}/innovations")
    public ResponseEntity<List<Innovation>> getCommitteeInnovations(@PathVariable Long id) {
        List<Innovation> innovations = committeeService.getInnovationsByCommittee(id);
        return ResponseEntity.ok(innovations);
    }






}
