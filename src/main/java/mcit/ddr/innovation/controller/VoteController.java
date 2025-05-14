package mcit.ddr.innovation.controller;

import mcit.ddr.innovation.enums.VoteDecision;
import mcit.ddr.innovation.repository.CommitteeMemberRepository;
import mcit.ddr.innovation.repository.InnovationRepository;
import mcit.ddr.innovation.repository.MyUserRepository;
import mcit.ddr.innovation.repository.VoteRepository;
import mcit.ddr.innovation.service.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
public class VoteController {
    private  final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // add vote by board member
    @PostMapping("/innovation/{innovationId}/vote")
    public ResponseEntity<?> vote(@PathVariable Long innovationId, @RequestBody String decisionStr) {
        VoteDecision decision;
        try {
            decision = VoteDecision.valueOf(decisionStr.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid vote decision");
        }
        return voteService.vote(innovationId, decision);
    }

    //get the logged-in user vote for current innoation
    @GetMapping("/innovation/{innovationId}/voteState")
    public ResponseEntity<?> getVote(@PathVariable Long innovationId) {
        return voteService.getVoteForLoggedInUser(innovationId);
    }

    //get all votes for the innovation
    @GetMapping("/innovation/{innovationId}/votes")
    public ResponseEntity<?> getVotesByInnovation(@PathVariable Long innovationId) {
        return voteService.getVotesByInnovationId(innovationId);
    }

    // delete a vote







}
