package mcit.ddr.innovation.service;

import mcit.ddr.innovation.service.CommitteeFileStorageService;
import mcit.ddr.innovation.dto.CommitteeDTO;
import mcit.ddr.innovation.entity.Committee;
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
import java.util.List;

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
    //     committee.setDescribtion(dto.getDescribtion());
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
        // Add default values and set user information
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        MyUser currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

        Committee committee = new Committee();
        committee.setName(dto.getName());
        committee.setDescribtion(dto.getDescribtion());
        committee.setCreatedDate(LocalDate.now());  
        committee.setCreatedBy(currentUser);
        committee.setIsClosed(false);

        // Handle file upload if a file is provided
        if (attachmentFile != null && !attachmentFile.isEmpty()) {
            String filePath = committeeFileStorageService.saveFile(attachmentFile);
            committee.setAttachment(filePath);  // Save the file path to the committee entity
        }

        // Save the committee to the database
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
    
        if (dto.getDescribtion() != null) {
            committee.setDescribtion(dto.getDescribtion());
        }
    
        if (file != null && !file.isEmpty()) {
            String filePath = committeeFileStorageService.saveFile(file);
            committee.setAttachment(filePath);  // Adjust field name accordingly
        }
    
        return committeeRepository.save(committee);
    }
    

    @Transactional
    public void deleteCommittee(Long id) {
        if (!committeeRepository.existsById(id)) {
            throw new RuntimeException("Committee not found");
        }
        committeeRepository.deleteById(id);
    }

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
}