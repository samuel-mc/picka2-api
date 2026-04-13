package com.samuel_mc.pickados_api.controllers.post;

import com.samuel_mc.pickados_api.dto.GenericResponseDTO;
import com.samuel_mc.pickados_api.dto.post.CommentRequestDTO;
import com.samuel_mc.pickados_api.dto.post.CommentResponseDTO;
import com.samuel_mc.pickados_api.dto.post.CompletePostMediaRequestDTO;
import com.samuel_mc.pickados_api.dto.post.CreatePostRequestDTO;
import com.samuel_mc.pickados_api.dto.post.PagedResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostMediaUploadResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostMetricsResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PresignPostMediaRequestDTO;
import com.samuel_mc.pickados_api.dto.post.PresignPostMediaResponseDTO;
import com.samuel_mc.pickados_api.dto.post.ReactionRequestDTO;
import com.samuel_mc.pickados_api.dto.post.SharePostRequestDTO;
import com.samuel_mc.pickados_api.dto.post.ToggleStateResponseDTO;
import com.samuel_mc.pickados_api.dto.post.UpdatePickResultRequestDTO;
import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.service.post.PostMediaStorageService;
import com.samuel_mc.pickados_api.service.post.PostService;
import com.samuel_mc.pickados_api.util.ResponseUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestController
@RequestMapping("/posts")
@Tag(name = "Posts", description = "Gestión de publicaciones de tipsters")
public class PostsController {

    private final PostService postService;
    private final PostMediaStorageService postMediaStorageService;
    private final ResponseUtils responseUtils;

    public PostsController(PostService postService, PostMediaStorageService postMediaStorageService, ResponseUtils responseUtils) {
        this.postService = postService;
        this.postMediaStorageService = postMediaStorageService;
        this.responseUtils = responseUtils;
    }

    @PostMapping("/media/presign")
    public ResponseEntity<?> presignMedia(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody PresignPostMediaRequestDTO request
    ) {
        try {
            PresignPostMediaResponseDTO dto = postMediaStorageService.presignPut(requireUserId(principal), request.getContentType());
            return ResponseEntity.ok(dto);
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(503).body(java.util.Map.of("error", ex.getMessage()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(java.util.Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/media/complete")
    public ResponseEntity<GenericResponseDTO<PostMediaUploadResponseDTO>> completeMedia(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CompletePostMediaRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(
                postMediaStorageService.completeUpload(requireUserId(principal), request.getObjectKey())
        );
    }

    @PostMapping
    public ResponseEntity<GenericResponseDTO<PostResponseDTO>> createPost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @Valid @RequestBody CreatePostRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(postService.createPost(requireUserId(principal), request));
    }

    @GetMapping("/feed")
    public ResponseEntity<GenericResponseDTO<PagedResponseDTO<PostResponseDTO>>> getFeed(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return responseUtils.generateSuccessResponse(postService.getFeed(requireUserId(principal), page, size));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<GenericResponseDTO<PostResponseDTO>> getDetail(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        return responseUtils.generateSuccessResponse(postService.getPostDetail(requireUserId(principal), postId));
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<GenericResponseDTO<CommentResponseDTO>> comment(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(postService.addComment(requireUserId(principal), postId, request));
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<GenericResponseDTO<List<CommentResponseDTO>>> getComments(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        return responseUtils.generateSuccessResponse(postService.getComments(requireUserId(principal), postId));
    }

    @PutMapping("/{postId}/reaction")
    public ResponseEntity<GenericResponseDTO<PostMetricsResponseDTO>> react(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody ReactionRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(postService.reactToPost(requireUserId(principal), postId, request));
    }

    @PutMapping("/{postId}/save")
    public ResponseEntity<GenericResponseDTO<ToggleStateResponseDTO>> toggleSave(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        return responseUtils.generateSuccessResponse(postService.toggleSavedPost(requireUserId(principal), postId));
    }

    @PostMapping("/{postId}/views")
    public ResponseEntity<GenericResponseDTO<String>> registerView(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        postService.registerView(requireUserId(principal), postId);
        return responseUtils.generateSuccessResponse("Vista registrada");
    }

    @PutMapping("/{postId}/pick-status")
    public ResponseEntity<GenericResponseDTO<PostResponseDTO>> updatePickStatus(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePickResultRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(postService.updatePickResult(requireUserId(principal), postId, request));
    }

    @PostMapping("/{postId}/share")
    public ResponseEntity<GenericResponseDTO<PostMetricsResponseDTO>> share(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId,
            @RequestBody(required = false) SharePostRequestDTO request
    ) {
        return responseUtils.generateSuccessResponse(postService.share(requireUserId(principal), postId, request));
    }

    @PutMapping("/{postId}/repost")
    public ResponseEntity<GenericResponseDTO<ToggleStateResponseDTO>> repost(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long postId
    ) {
        return responseUtils.generateSuccessResponse(postService.repost(requireUserId(principal), postId));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<GenericResponseDTO<PagedResponseDTO<PostResponseDTO>>> getPostsByUser(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return responseUtils.generateSuccessResponse(postService.getPostsByUser(requireUserId(principal), userId, page, size));
    }

    @PostMapping("/users/{userId}/follow")
    public ResponseEntity<GenericResponseDTO<ToggleStateResponseDTO>> follow(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long userId
    ) {
        return responseUtils.generateSuccessResponse(postService.followUser(requireUserId(principal), userId));
    }

    @DeleteMapping("/users/{userId}/follow")
    public ResponseEntity<GenericResponseDTO<ToggleStateResponseDTO>> unfollow(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable Long userId
    ) {
        return responseUtils.generateSuccessResponse(postService.unfollowUser(requireUserId(principal), userId));
    }

    private Long requireUserId(CustomUserDetails principal) {
        if (principal == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Usuario no autenticado");
        }
        return principal.getId();
    }
}
