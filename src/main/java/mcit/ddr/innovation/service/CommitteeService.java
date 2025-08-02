package mcit.ddr.innovation.service;

import mcit.ddr.innovation.enums.InnovStatus;
import mcit.ddr.innovation.service.CommitteeFileStorageService;
import mcit.ddr.innovation.dto.CommitteeDTO;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.repository.CommitteeRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.*;

@Service
public class CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final MyUserRepository userRepository;
    private final CommitteeFileStorageService committeeFileStorageService;

    public CommitteeService(CommitteeRepository committeeRepository, MyUserRepository userRepository) {
        this.committeeRepository = committeeRepository;
        this.userRepository = userRepository;
        this.committeeFileStorageService = new CommitteeFileStorageService();
    }

    // public Committee createCommittee(CommitteeDTO dto) {
    //     Committee committee = new Committee();
    //     committee.setName(dto.getName());
    //     committee.setDescription(dto.getDescription());
    //     committee.setCreatedDate(LocalDate.now()); 
    //     committee.setIsClosed(false); 

    //     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    //     String username = auth.getName();

    //     MyUser creator = userRepository.findByUsername(username)
    //             .orElseThrow(() -> new RuntimeException("User not found: " + username));
    //     committee.setCreatedBy(creator);

    //     return committeeRepository.save(committee);
    // }

    public Committee createCommittee(Committee dto, MultipartFile attachmentFile) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String identifier = auth.getName(); // could be username or email

        Optional<MyUser> userOpt = userRepository.findByUsername(identifier);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findByEmail(identifier);
        }
        MyUser currentUser = userOpt.orElseThrow(() -> new RuntimeException("User not found: " + identifier));

        Committee committee = new Committee();
        committee.setName(dto.getName());
        committee.setDescription(dto.getDescription());
        committee.setCreatedDate(LocalDate.now());
        committee.setCreatedBy(currentUser);
        committee.setIsClosed(false);

        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String filePath = committeeFileStorageService.saveFile(attachmentFile);
            committee.setAttachment(filePath);
        }

        return committeeRepository.save(committee);
    }


    // public Committee updateCommitteeName(Long id, String newName) {
    //     Committee committee = committeeRepository.findById(id)
    //             .orElseThrow(() -> new RuntimeException("Committee not found"));

    //     // Check if the new name is already used by another committee
    //     committeeRepository.findByName(newName).ifPresent(existing -> {
    //         if (!existing.getId().equals(id)) {
    //             throw new RuntimeException("A committee with this name already exists.");
    //         }
    //     });

    //     committee.setName(newName);
    //     return committeeRepository.save(committee);
    // }

    public Committee partialUpdateCommittee(Long id, CommitteeDTO dto, MultipartFile file) {
        Committee committee = committeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Committee not found"));
    
        if (dto.getName() != null) {
            // Check if new name exists on another committee
            committeeRepository.findByName(dto.getName()).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new RuntimeException("A committee with this name already exists.");
                }
            });
            committee.setName(dto.getName());
        }
    
        if (dto.getDescription() != null) {
            committee.setDescription(dto.getDescription());
        }
    
        if (file != null && !file.isEmpty()) {
            String filePath = committeeFileStorageService.saveFile(file);
            committee.setAttachment(filePath);  // Adjust field name accordingly
        }
    
        return committeeRepository.save(committee);
    }
    

//    @Transactional
//    public void deleteCommittee(Long id) {
//        if (!committeeRepository.existsById(id)) {
//            throw new RuntimeException("Committee not found");
//        }
//        committeeRepository.deleteById(id);
//    }

    public Committee setCommitteeClosed(Long id, boolean isClosed) {
        Committee committee = committeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Committee not found"));
        committee.setIsClosed(isClosed);
        return committeeRepository.save(committee);
    }

    public List<Committee> getAllCommittees() {
        return committeeRepository.findAll();
    }

    public Committee getCommitteeById(Long id) {
        return committeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Committee not found with id: " + id));
    }

    public List<Innovation> getInnovationsByCommittee(Long committeeId) {
        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new RuntimeException("Committee not found"));
        return committee.getInnovations();
    }

    public Map<String, Map<String, Long>> countStatusesByCommitteeName() {
        List<Committee> allCommittees = committeeRepository.findAll();
        Map<String, Map<String, Long>> committeeStatusCounts = new LinkedHashMap<>();

        for (Committee committee : allCommittees) {
            Map<String, Long> statusCounts = new LinkedHashMap<>();

            long totalAssigned = 0;

            for (Innovation innovation : committee.getInnovations()) {
                InnovStatus status = innovation.getStatus();

                // Skip unwanted statuses
                if (status == InnovStatus.DRAFT || status == InnovStatus.SUBMITTED) {
                    continue;
                }

                statusCounts.put(status.name(), statusCounts.getOrDefault(status.name(), 0L) + 1);
                totalAssigned++;
            }

            // Ensure all other statuses are present with 0 if not already present
            for (InnovStatus status : InnovStatus.values()) {
                if (status != InnovStatus.DRAFT && status != InnovStatus.SUBMITTED) {
                    statusCounts.putIfAbsent(status.name(), 0L);
                }
            }

            statusCounts.put("TotalAssigned", totalAssigned);
            committeeStatusCounts.put(committee.getName(), statusCounts);
        }

        return committeeStatusCounts;
    }


}