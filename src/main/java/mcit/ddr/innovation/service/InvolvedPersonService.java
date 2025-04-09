package mcit.ddr.innovation.service;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.entity.InvolvedPerson;
import mcit.ddr.innovation.enums.PersonType;
import mcit.ddr.innovation.exception.ResourceNotFoundException;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.repository.InvolvedPersonRepository;
import mcit.ddr.innovation.repository.InnovationRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class InvolvedPersonService {

    private final InvolvedPersonRepository involvedPersonRepository;
    private final InnovationRepository innovationRepository;

    //create invovled person
    public InvolvedPerson addInvolvedPerson(Long innovationId, InvolvedPerson submittedData) {
                // Find the innovation by ID
        Innovation innovation = innovationRepository.findById(innovationId)
            .orElseThrow(() -> new ResourceNotFoundException("Innovation not found"));

        // Check if the involved person already exists based on their NID
        Optional<InvolvedPerson> existingPersonOpt = involvedPersonRepository.findByNid(submittedData.getNid());

        InvolvedPerson involvedPerson;
        if (existingPersonOpt.isPresent()) {
            involvedPerson = existingPersonOpt.get();
        } else {
            // Create a new InvolvedPerson if not found
            involvedPerson = new InvolvedPerson();
            involvedPerson = involvedPersonRepository.save(submittedData);
        }

        // Add the involved person to the innovation
        innovation.getInvolvedPersons().add(involvedPerson);
        innovationRepository.save(innovation);  // Update the innovation with the new involved person

        return involvedPerson;
        
    }

    // update service
    public InvolvedPerson updateInvolvedPerson(Long id, Map<String, Object> updates) {
        InvolvedPerson involvedPerson = involvedPersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("InvolvedPerson not found"));

        updates.forEach((key, value) -> {
            try {
                Field field = InvolvedPerson.class.getDeclaredField(key);
                field.setAccessible(true);

                // Handle ENUM conversion (PersonType)
                if (field.getType().isEnum() && value instanceof String) {
                    if (field.getType().equals(PersonType.class)) {
                        value = PersonType.valueOf((String) value); // Convert String to Enum
                    }
                }

                // Handle Innovation separately
                if ("innovation".equals(key) && value instanceof Number) {
                    Long innovationId = ((Number) value).longValue();
                    Innovation innovation = innovationRepository.findById(innovationId)
                            .orElseThrow(() -> new RuntimeException("Innovation not found"));
                    value = innovation;
                }

                field.set(involvedPerson, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException("Invalid field: " + key, e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid enum value for field: " + key, e);
            }
        });

        return involvedPersonRepository.save(involvedPerson);
    }


    public List<InvolvedPerson> searchInvolvedPersons() {
        return involvedPersonRepository.findAll(); // Assuming you want all involved persons
    }

    public Optional<InvolvedPerson> getInvolvedPersonByNID(String nid) {
        return involvedPersonRepository.findByNid(nid); 
    }

    public Optional<InvolvedPerson> getInvolvedPersonById(Long id) {
        return involvedPersonRepository.findById(id);
    }

    public void deleteInvolvedPerson(Long id) {
        involvedPersonRepository.deleteById(id);
    }
}