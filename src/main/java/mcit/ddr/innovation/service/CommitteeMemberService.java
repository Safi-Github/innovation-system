package mcit.ddr.innovation.service;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.CommitteeMemberDTO;
import mcit.ddr.innovation.dto.UserInCommitteesDTO;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.CommitteeMember;
import mcit.ddr.innovation.entity.MyUser;
import mcit.ddr.innovation.repository.CommitteeMemberRepository;
import mcit.ddr.innovation.repository.CommitteeRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommitteeMemberService {

    private final CommitteeMemberRepository committeeMemberRepository;
    private final CommitteeRepository committeeRepository;
    private final MyUserRepository userRepository;

    public CommitteeMember addMemberToCommittee(CommitteeMemberDTO dto) {
        Committee committee = committeeRepository.findById(dto.getCommitteeId())
                .orElseThrow(() -> new RuntimeException("Committee not found"));

        MyUser user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check for existing membership (to avoid duplicate entry)
        boolean exists = committeeMemberRepository.existsByUserAndCommittee(user, committee);
        if (exists) {
            throw new RuntimeException("This user is already a member of the committee");
        }

        // Check if the user is being set as the head and if another member is already head
        if (dto.getIsHead()) {
            long headCount = committeeMemberRepository.countByCommitteeAndIsHeadTrue(committee);
            if (headCount > 0) {
                throw new RuntimeException("This committee already has a head member");
            }
        }

        CommitteeMember member = new CommitteeMember();
        member.setUser(user);
        member.setCommittee(committee);
        member.setIsHead(dto.getIsHead());

        return committeeMemberRepository.save(member);
    }

//    public List<Committee> getAllCommitteesByUserId(Long userId) {
//        return committeeMemberRepository.findByUserId(userId)
//                .stream()
//                .map(cm -> cm.getCommittee())
//                .collect(Collectors.toList());
//    }

    public List<UserInCommitteesDTO> getCommitteeSummariesByUserId(Long userId) {
        return committeeMemberRepository.findCommitteeSummariesByUserId(userId);
    }

    public List<CommitteeMember> getMembersByCommitteeId(Long committeeId) {
        Committee committee = committeeRepository.findById(committeeId)
                .orElseThrow(() -> new RuntimeException("Committee not found"));
        return committeeMemberRepository.findByCommittee(committee);
    }


    public void assignHead(CommitteeMemberDTO dto) {
        // Step 1: Unset any previous head in the committee
        List<CommitteeMember> members = committeeMemberRepository.findByCommitteeId(dto.getCommitteeId());
        for (CommitteeMember member : members) {
            member.setIsHead(false);
        }

        // Step 2: Set the requested member as head
        CommitteeMember newHead = committeeMemberRepository
                .findByCommitteeIdAndUserId(dto.getCommitteeId(), dto.getUserId())
                .orElseThrow(() -> new RuntimeException("Committee member not found"));

        newHead.setIsHead(true);
        committeeMemberRepository.saveAll(members); // Save all members with head = false
        committeeMemberRepository.save(newHead);    // Save the new head
    }

    public void removeMember(Long id) {
        committeeMemberRepository.deleteById(id);
    }

//    public CommitteeMember updateHeadStatus(Long id, boolean isHead) {
//        CommitteeMember member = committeeMemberRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Member not found"));
//
//        if (isHead) {
//            long headCount = committeeMemberRepository.countByCommitteeAndIsHeadTrue(member.getCommittee());
//            if (headCount > 0 && !member.isHead()) {
//                throw new RuntimeException("This committee already has a head member");
//            }
//        }
//
//        member.setIsHead(isHead);
//        return committeeMemberRepository.save(member);
//    }

    @Transactional
    public void removeUserFromCommittee(Long userId, Long committeeId) {
        boolean exists = committeeMemberRepository.existsByUserIdAndCommitteeId(userId, committeeId);
        if (!exists) {
            throw new RuntimeException("User is not a member of this committee");
        }
        committeeMemberRepository.deleteByUserIdAndCommitteeId(userId, committeeId);
    }





}