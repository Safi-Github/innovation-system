package mcit.ddr.innovation.service;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.entity.InvolvedPerson;
import mcit.ddr.innovation.entity.Innovation;
import mcit.ddr.innovation.repository.InvolvedPersonRepository;
import mcit.ddr.innovation.repository.InnovationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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


    public List<InvolvedPerson> getInvolvedPersonsByInnovation(Long innovationId) {
        return involvedPersonRepository.findByInnovationId(innovationId);
    }

    public void deleteInvolvedPerson(Long id) {
        involvedPersonRepository.deleteById(id);
    }
}