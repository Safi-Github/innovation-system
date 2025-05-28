package mcit.ddr.innovation.controller;

import jakarta.validation.constraints.NotNull;
import mcit.ddr.innovation.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Text;

import java.security.Principal;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //add comment
    @PostMapping("/innovation/{innovationId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long innovationId,
            @RequestBody @NotNull String commentText) {
            return commentService.addComment(innovationId, commentText);
    }

    //list the comments of the specific Board Member Related to an innovation
    @GetMapping("/innovation/{innovationId}/comments/user/{userId}")
    public ResponseEntity<?> getUserCommentsOnInnovation(
            @PathVariable Long innovationId,
            @PathVariable Long userId) {
        return commentService.getUserCommentsForInnovation(innovationId, userId);
    }
    
    //list all the comments related to an innovation
    @GetMapping("/innovation/{innovationId}/comments")
    public ResponseEntity<?> getUserCommentsOnInnovation(@PathVariable Long innovationId) {
        return commentService.getAllMemberCommentsForInnovation(innovationId);
    }
}
