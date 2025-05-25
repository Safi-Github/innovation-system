package mcit.ddr.innovation.controller;

import lombok.RequiredArgsConstructor;
import mcit.ddr.innovation.dto.CommitteeMemberDTO;
import mcit.ddr.innovation.dto.UserInCommitteesDTO;
import mcit.ddr.innovation.entity.Committee;
import mcit.ddr.innovation.entity.CommitteeMember;
import mcit.ddr.innovation.service.CommitteeMemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/committee-members")
@RequiredArgsConstructor
public class CommitteeMemberController {

    private final CommitteeMemberService committeeMemberService;

    @PostMapping
    public ResponseEntity<CommitteeMember> addMemberToCommittee(@RequestBody CommitteeMemberDTO dto) {
        CommitteeMember member = committeeMemberService.addMemberToCommittee(dto);
        return ResponseEntity.ok(member);
    }

    // List of all committees a member is a part of it
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserInCommitteesDTO>> getCommitteeSummariesByUser(@PathVariable Long userId) {
        List<UserInCommitteesDTO> summaries = committeeMemberService.getCommitteeSummariesByUserId(userId);
        return ResponseEntity.ok(summaries);
    }
    //count the committees a user is part of
    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Long> countCommitteesByUser(@PathVariable Long userId) {
        Long count = committeeMemberService.countCommitteesByUserId(userId);
        return ResponseEntity.ok(count);
    }


    // Get All Members of a Committee
    @GetMapping("/committee/{committeeId}")
    public ResponseEntity<List<CommitteeMember>> getMembersByCommittee(@PathVariable Long committeeId) {
        List<CommitteeMember> members = committeeMemberService.getMembersByCommitteeId(committeeId);
        return ResponseEntity.ok(members);
    }

    //change the member head from a committee
    @PutMapping("/change-head")
    public ResponseEntity<String> assignCommitteeHead(@RequestBody CommitteeMemberDTO dto) {
        committeeMemberService.assignHead(dto);
        return ResponseEntity.ok("Head member successfully Changed.");
    }


    // Remove a Member from a Committee
//    @DeleteMapping("user/{id}")
//    public ResponseEntity<Void> removeMember(@PathVariable Long id) {
//        committeeMemberService.removeMember(id);
//        return ResponseEntity.noContent().build();
//    }


    // Update Member Role (e.g., change head status)
//    @PutMapping("user/{id}/head")
//    public ResponseEntity<CommitteeMember> updateHeadStatus(
//            @PathVariable Long id,
//            @RequestParam boolean isHead) {
//        CommitteeMember updated = committeeMemberService.updateHeadStatus(id, isHead);
//        return ResponseEntity.ok(updated);
//    }

    // delete user form a committee
    @DeleteMapping("/user/{userId}/committee/{committeeId}")
    public ResponseEntity<Void> removeUserFromCommittee(
            @PathVariable Long userId,
            @PathVariable Long committeeId) {
        committeeMemberService.removeUserFromCommittee(userId, committeeId);
        return ResponseEntity.noContent().build();
    }
}