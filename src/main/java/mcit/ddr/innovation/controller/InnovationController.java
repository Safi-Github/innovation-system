package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.dto.InnovationDTO;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.entity.Review;
import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.ReviewRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.exception.ResourceNotFoundException;

import mcit.ddr.innovation.service.InnovationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import mcit.ddr.innovation.service.SecurityUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.catalina.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/innovation")
public class InnovationController {
    private static final Logger logger = LoggerFactory.getLogger(InnovationController.class);

    @Autowired
    private InnovationRepository innovationRepository;
    private ReviewRepository reviewRepository;
    private MyUserRepository myUserRepository;
    private final InnovationService innovationService;

    public InnovationController(InnovationRepository innovationRepository,ReviewRepository reviewRepository,MyUserRepository myUserRepository, InnovationService innovationService) {
        this.innovationRepository = innovationRepository;
        this.reviewRepository = reviewRepository;
        this.myUserRepository = myUserRepository;
        this.innovationService = innovationService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_INNOVATOR')")
    public ResponseEntity<Innovation> createInnovation(@Valid @RequestBody Innovation innovation) {
        Innovation createdInnovation = innovationService.createInnovation(innovation);
        return ResponseEntity.ok(createdInnovation);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/assign/{id}")
    public ResponseEntity<Innovation> assignInnovation(@PathVariable Long id,@RequestBody Map<String, String> payload ) {
        Long assignerId = Long.parseLong(payload.get("assignerId"));
        Long boardMemberId = Long.parseLong(payload.get("boardMemberId"));
        String statusValue = payload.get("status");
        String comment = payload.get("comment");

        Innovation updatedInnovation = innovationService.assignInnovation(id, assignerId, boardMemberId, statusValue, comment);
        return ResponseEntity.ok(updatedInnovation);
    }

    //innovation status changing api
    @PutMapping("/status/{id}")
    public ResponseEntity<?> updateInnovationStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        InnovationDTO responseDTO = innovationService.updateInnovationStatus(id, payload);
            return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<Innovation>> getAllInnovations() {
        return ResponseEntity.ok(innovationService.getAllInnovations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Innovation> getInnovationById(@PathVariable Long id) {
        return innovationService.getInnovationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}
