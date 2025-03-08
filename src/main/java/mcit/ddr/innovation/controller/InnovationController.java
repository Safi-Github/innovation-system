package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.dto.InnovationDTO;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.repository.InnovationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/innovation")
public class InnovationController {

    @Autowired
    private InnovationRepository innovationRepository;

    // ✅ 1. API to Add Innovation Record (Using DTO)
    @PostMapping("/add")
    public ResponseEntity<InnovationDTO> addInnovation(@RequestBody InnovationDTO innovationDTO) {
        Innovation innovation = new Innovation();
        innovation.setTittle(innovationDTO.getTittle());
        innovation.setInnovStatus(InnovStatus.DRAFT);

        Innovation savedInnovation = innovationRepository.save(innovation);
        
        // Convert Entity to DTO before returning
        InnovationDTO responseDTO = new InnovationDTO(
                savedInnovation.getId(), 
                savedInnovation.getTittle(), 
                savedInnovation.getInnovStatus()
        );

        return ResponseEntity.ok(responseDTO);
    }

    // ✅ 2. API to Update Innovation Status (Using DTO)
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateInnovationStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        Optional<Innovation> optionalInnovation = innovationRepository.findById(id);

        if (optionalInnovation.isPresent()) {
            Innovation innovation = optionalInnovation.get();
            try {
                // Extract and validate status from the payload
                String statusValue = payload.get("status");

                if (statusValue == null || statusValue.isEmpty()) {
                    return ResponseEntity.badRequest().body("New Status Param is required");
                }

                innovation.setInnovStatus(InnovStatus.valueOf(statusValue.toUpperCase()));
                innovationRepository.save(innovation);

                // Convert Entity to DTO before returning
                InnovationDTO responseDTO = new InnovationDTO(
                        innovation.getId(),
                        innovation.getTittle(),
                        innovation.getInnovStatus()
                );

                return ResponseEntity.ok(responseDTO);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body("Invalid status value: " + payload.get("status"));
            }
        } else {
            return ResponseEntity.badRequest().body("Innovation record not found!");
        }
    }

}
