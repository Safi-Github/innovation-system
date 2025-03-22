package mcit.ddr.innovation.service;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.entity.InvolvedPerson;
import mcit.ddr.innovation.enums.PersonType;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.repository.InvolvedPersonRepository;
import mcit.ddr.innovation.repository.InnovationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InvolvedPersonService {

    private final InvolvedPersonRepository involvedPersonRepository;
    private final InnovationRepository innovationRepository;

    public InvolvedPerson addInvolvedPerson(Long innovationId, InvolvedPerson involvedPerson) {
        Innovation innovation = innovationRepository.findById(innovationId)
                .orElseThrow(() -> new RuntimeException("Innovation not found"));

        involvedPerson.setInnovation(innovation);
        System.out.println("InvolvedPerson: " + involvedPerson);  // Add a log here to check
        return involvedPersonRepository.save(involvedPerson);
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


    public List<InvolvedPerson> getInvolvedPersonsByInnovation(Long innovationId) {
        return involvedPersonRepository.findByInnovationId(innovationId);
    }

    public void deleteInvolvedPerson(Long id) {
        involvedPersonRepository.deleteById(id);
    }
}