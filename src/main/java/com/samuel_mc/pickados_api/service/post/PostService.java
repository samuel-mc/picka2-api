package com.samuel_mc.pickados_api.service.post;

import com.samuel_mc.pickados_api.config.R2Properties;
import com.samuel_mc.pickados_api.dto.post.CommentRequestDTO;
import com.samuel_mc.pickados_api.dto.post.CommentResponseDTO;
import com.samuel_mc.pickados_api.dto.post.CreatePostRequestDTO;
import com.samuel_mc.pickados_api.dto.post.PagedResponseDTO;
import com.samuel_mc.pickados_api.dto.post.ParleySelectionRequestDTO;
import com.samuel_mc.pickados_api.dto.post.ParleySelectionResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostAuthorResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostMetricsResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostParleyRequestDTO;
import com.samuel_mc.pickados_api.dto.post.PostParleyResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostPickRequestDTO;
import com.samuel_mc.pickados_api.dto.post.PostPickResponseDTO;
import com.samuel_mc.pickados_api.dto.post.PostResponseDTO;
import com.samuel_mc.pickados_api.dto.post.ReactionRequestDTO;
import com.samuel_mc.pickados_api.dto.post.SharePostRequestDTO;
import com.samuel_mc.pickados_api.dto.post.ToggleStateResponseDTO;
import com.samuel_mc.pickados_api.dto.post.UpdatePickResultRequestDTO;
import com.samuel_mc.pickados_api.entity.CompetitionEntity;
import com.samuel_mc.pickados_api.entity.CommentEntity;
import com.samuel_mc.pickados_api.entity.CommentLikeEntity;
import com.samuel_mc.pickados_api.entity.FollowEntity;
import com.samuel_mc.pickados_api.entity.PostEntity;
import com.samuel_mc.pickados_api.entity.PostMediaEntity;
import com.samuel_mc.pickados_api.entity.PostParleyEntity;
import com.samuel_mc.pickados_api.entity.PostPickEntity;
import com.samuel_mc.pickados_api.entity.PostRepostEntity;
import com.samuel_mc.pickados_api.entity.PostShareEntity;
import com.samuel_mc.pickados_api.entity.PostTagEntity;
import com.samuel_mc.pickados_api.entity.PostViewEntity;
import com.samuel_mc.pickados_api.entity.ParleySelectionEntity;
import com.samuel_mc.pickados_api.entity.ReactionEntity;
import com.samuel_mc.pickados_api.entity.SavedPostEntity;
import com.samuel_mc.pickados_api.entity.SportEntity;
import com.samuel_mc.pickados_api.entity.SportsbookEntity;
import com.samuel_mc.pickados_api.entity.TipsterProfileEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.entity.enums.PostType;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import com.samuel_mc.pickados_api.entity.enums.ReactionType;
import com.samuel_mc.pickados_api.entity.enums.ResultStatus;
import com.samuel_mc.pickados_api.repository.TipsterProfileRepository;
import com.samuel_mc.pickados_api.repository.CompetitionRepository;
import com.samuel_mc.pickados_api.repository.SportRepository;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.repository.post.CommentRepository;
import com.samuel_mc.pickados_api.repository.post.CommentLikeRepository;
import com.samuel_mc.pickados_api.repository.post.FollowRepository;
import com.samuel_mc.pickados_api.repository.post.PostPickRepository;
import com.samuel_mc.pickados_api.repository.post.PostRepository;
import com.samuel_mc.pickados_api.repository.post.PostRepostRepository;
import com.samuel_mc.pickados_api.repository.post.PostShareRepository;
import com.samuel_mc.pickados_api.repository.post.PostViewRepository;
import com.samuel_mc.pickados_api.repository.post.ReactionRepository;
import com.samuel_mc.pickados_api.repository.post.SavedPostRepository;
import com.samuel_mc.pickados_api.repository.post.SportsbookRepository;
import com.samuel_mc.pickados_api.repository.projection.PostTimelineProjection;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class PostService {

    private static final String ROLE_TIPSTER = "TIPSTER";

    private final PostRepository postRepository;
    private final PostPickRepository postPickRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ReactionRepository reactionRepository;
    private final SavedPostRepository savedPostRepository;
    private final PostViewRepository postViewRepository;
    private final PostShareRepository postShareRepository;
    private final PostRepostRepository postRepostRepository;
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final SportsbookRepository sportsbookRepository;
    private final TipsterProfileRepository tipsterProfileRepository;
    private final SportRepository sportRepository;
    private final CompetitionRepository competitionRepository;
    private final SportsbookService sportsbookService;
    private final PostMediaStorageService postMediaStorageService;
    private final R2Properties r2Properties;

    public PostService(
            PostRepository postRepository,
            PostPickRepository postPickRepository,
            CommentRepository commentRepository,
            CommentLikeRepository commentLikeRepository,
            ReactionRepository reactionRepository,
            SavedPostRepository savedPostRepository,
            PostViewRepository postViewRepository,
            PostShareRepository postShareRepository,
            PostRepostRepository postRepostRepository,
            FollowRepository followRepository,
            UserRepository userRepository,
            SportsbookRepository sportsbookRepository,
            TipsterProfileRepository tipsterProfileRepository,
            SportRepository sportRepository,
            CompetitionRepository competitionRepository,
            SportsbookService sportsbookService,
            PostMediaStorageService postMediaStorageService,
            R2Properties r2Properties
    ) {
        this.postRepository = postRepository;
        this.postPickRepository = postPickRepository;
        this.commentRepository = commentRepository;
        this.commentLikeRepository = commentLikeRepository;
        this.reactionRepository = reactionRepository;
        this.savedPostRepository = savedPostRepository;
        this.postViewRepository = postViewRepository;
        this.postShareRepository = postShareRepository;
        this.postRepostRepository = postRepostRepository;
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.sportsbookRepository = sportsbookRepository;
        this.tipsterProfileRepository = tipsterProfileRepository;
        this.sportRepository = sportRepository;
        this.competitionRepository = competitionRepository;
        this.sportsbookService = sportsbookService;
        this.postMediaStorageService = postMediaStorageService;
        this.r2Properties = r2Properties;
    }

    @Transactional
    public PostResponseDTO createPost(Long currentUserId, CreatePostRequestDTO request) {
        UserEntity author = getUser(currentUserId);
        ensureTipster(author);
        validatePostRequest(request);

        PostEntity post = new PostEntity();
        post.setAuthor(author);
        post.setType(request.getType());
        post.setContent(request.getContent().trim());
        post.setVisibility(request.getVisibility());

        if (request.getImageKey() != null && !request.getImageKey().isBlank()) {
            if (!postMediaStorageService.isKeyOwnedByUser(currentUserId, request.getImageKey().trim())) {
                throw new ResponseStatusException(BAD_REQUEST, "La imagen del post no pertenece al usuario autenticado");
            }
            PostMediaEntity media = new PostMediaEntity();
            media.setPost(post);
            media.setUrl(request.getImageKey().trim());
            post.getMedia().add(media);
        }

        normalizeTags(request.getTags()).forEach(tagValue -> {
            PostTagEntity tag = new PostTagEntity();
            tag.setPost(post);
            tag.setTag(tagValue);
            post.getTags().add(tag);
        });

        if (request.getType() == PostType.PICK_SIMPLE) {
            post.setSimplePick(buildSimplePick(post, request.getSimplePick()));
        }

        if (request.getType() == PostType.PARLEY) {
            post.setParleyDetails(buildParleyDetails(post, request.getParley()));
            post.setParleySelections(buildParleySelections(post, request.getParley()));
        }

        PostEntity saved = postRepository.save(post);
        return mapPost(saved, currentUserId);
    }

    @Transactional(readOnly = true)
    public PagedResponseDTO<PostResponseDTO> getFeed(Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        return mapTimelinePage(postRepository.findFeedTimeline(currentUserId, pageable), currentUserId);
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getPostDetail(Long currentUserId, Long postId) {
        return mapPost(getVisiblePost(postId, currentUserId), currentUserId);
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getPublicPostDetail(Long postId) {
        PostEntity post = postRepository.findPublicById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post no encontrado"));
        return mapPost(post, null);
    }

    @Transactional(readOnly = true)
    public PagedResponseDTO<PostResponseDTO> getPostsByUser(Long currentUserId, Long authorId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        return mapTimelinePage(postRepository.findTimelineByAuthorVisibleToUser(authorId, currentUserId, pageable), currentUserId);
    }

    @Transactional(readOnly = true)
    public PagedResponseDTO<PostResponseDTO> getSavedPosts(Long currentUserId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50));
        return mapPage(postRepository.findSavedByUserVisibleToUser(currentUserId, pageable), currentUserId);
    }

    @Transactional
    public CommentResponseDTO addComment(Long currentUserId, Long postId, CommentRequestDTO request) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        UserEntity author = getUser(currentUserId);

        CommentEntity entity = new CommentEntity();
        entity.setPost(post);
        entity.setAuthor(author);
        entity.setContent(request.getContent().trim());
        return mapComment(commentRepository.save(entity), currentUserId);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDTO> getComments(Long currentUserId, Long postId) {
        getVisiblePost(postId, currentUserId);
        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(comment -> mapComment(comment, currentUserId))
                .toList();
    }

    @Transactional
    public CommentResponseDTO toggleCommentLike(Long currentUserId, Long postId, Long commentId) {
        getVisiblePost(postId, currentUserId);
        CommentEntity comment = commentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Comentario no encontrado"));

        commentLikeRepository.findByCommentIdAndUserId(commentId, currentUserId)
                .ifPresentOrElse(
                        commentLikeRepository::delete,
                        () -> {
                            CommentLikeEntity like = new CommentLikeEntity();
                            like.setComment(comment);
                            like.setUser(getUser(currentUserId));
                            commentLikeRepository.save(like);
                        }
                );

        return mapComment(comment, currentUserId);
    }

    @Transactional
    public PostMetricsResponseDTO reactToPost(Long currentUserId, Long postId, ReactionRequestDTO request) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        ReactionEntity existing = reactionRepository.findByPostIdAndUserId(postId, currentUserId).orElse(null);

        if (existing != null && existing.getType() == request.getType()) {
            reactionRepository.delete(existing);
        } else {
            if (existing == null) {
                existing = new ReactionEntity();
                existing.setPost(post);
                existing.setUser(getUser(currentUserId));
            }
            existing.setType(request.getType());
            reactionRepository.save(existing);
        }
        return buildMetrics(postId, currentUserId);
    }

    @Transactional
    public ToggleStateResponseDTO toggleSavedPost(Long currentUserId, Long postId) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        return savedPostRepository.findByPostIdAndUserId(postId, currentUserId)
                .map(existing -> {
                    savedPostRepository.delete(existing);
                    return new ToggleStateResponseDTO(false);
                })
                .orElseGet(() -> {
                    SavedPostEntity saved = new SavedPostEntity();
                    saved.setPost(post);
                    saved.setUser(getUser(currentUserId));
                    savedPostRepository.save(saved);
                    return new ToggleStateResponseDTO(true);
                });
    }

    @Transactional
    public void registerView(Long currentUserId, Long postId) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        PostViewEntity view = postViewRepository.findByPostIdAndUserId(postId, currentUserId).orElseGet(() -> {
            PostViewEntity entity = new PostViewEntity();
            entity.setPost(post);
            entity.setUser(getUser(currentUserId));
            return entity;
        });
        view.setViewedAt(LocalDateTime.now());
        postViewRepository.save(view);
    }

    @Transactional
    public PostResponseDTO updatePickResult(Long currentUserId, Long postId, UpdatePickResultRequestDTO request) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        if (post.getAuthor().getId() != currentUserId) {
            throw new ResponseStatusException(BAD_REQUEST, "Solo el autor puede actualizar el estado del pick");
        }
        ensureTipster(post.getAuthor());

        if (post.getType() == PostType.PICK_SIMPLE) {
            PostPickEntity pick = postPickRepository.findByPostId(postId)
                    .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Pick no encontrado"));
            pick.setResultStatus(request.getResultStatus());
            postPickRepository.save(pick);
        } else if (post.getType() == PostType.PARLEY) {
            if (post.getParleyDetails() == null) {
                throw new ResponseStatusException(NOT_FOUND, "Parley no encontrado");
            }
            post.getParleyDetails().setResultStatus(request.getResultStatus());
            postRepository.save(post);
        } else {
            throw new ResponseStatusException(BAD_REQUEST, "Los posts de análisis no tienen estado de pick");
        }
        return mapPost(postRepository.findById(postId).orElseThrow(), currentUserId);
    }

    @Transactional
    public ToggleStateResponseDTO followUser(Long currentUserId, Long targetUserId) {
        if (currentUserId.equals(targetUserId)) {
            throw new ResponseStatusException(BAD_REQUEST, "No puedes seguirte a ti mismo");
        }

        UserEntity follower = getUser(currentUserId);
        UserEntity followed = getUser(targetUserId);
        return followRepository.findByFollowerIdAndFollowedId(currentUserId, targetUserId)
                .map(existing -> new ToggleStateResponseDTO(true))
                .orElseGet(() -> {
                    FollowEntity follow = new FollowEntity();
                    follow.setFollower(follower);
                    follow.setFollowed(followed);
                    followRepository.save(follow);
                    return new ToggleStateResponseDTO(true);
                });
    }

    @Transactional
    public ToggleStateResponseDTO unfollowUser(Long currentUserId, Long targetUserId) {
        return followRepository.findByFollowerIdAndFollowedId(currentUserId, targetUserId)
                .map(existing -> {
                    followRepository.delete(existing);
                    return new ToggleStateResponseDTO(false);
                })
                .orElse(new ToggleStateResponseDTO(false));
    }

    private void ensureTipster(UserEntity user) {
        if (user.getRole() == null || !ROLE_TIPSTER.equalsIgnoreCase(user.getRole().getName())) {
            throw new ResponseStatusException(BAD_REQUEST, "Solo los tipsters pueden publicar picks");
        }
    }

    @Transactional
    public ToggleStateResponseDTO repost(Long currentUserId, Long postId) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        return postRepostRepository.findByPostIdAndUserId(postId, currentUserId)
                .map(existing -> {
                    postRepostRepository.delete(existing);
                    return new ToggleStateResponseDTO(false);
                })
                .orElseGet(() -> {
                    PostRepostEntity entity = new PostRepostEntity();
                    entity.setPost(post);
                    entity.setUser(getUser(currentUserId));
                    postRepostRepository.save(entity);
                    return new ToggleStateResponseDTO(true);
                });
    }

    @Transactional
    public PostMetricsResponseDTO share(Long currentUserId, Long postId, SharePostRequestDTO request) {
        PostEntity post = getVisiblePost(postId, currentUserId);
        PostShareEntity share = new PostShareEntity();
        share.setPost(post);
        share.setUser(getUser(currentUserId));
        share.setChannel(request != null && request.getChannel() != null && !request.getChannel().isBlank()
                ? request.getChannel().trim()
                : "APP");
        postShareRepository.save(share);
        return buildMetrics(postId, currentUserId);
    }

    private void validatePostRequest(CreatePostRequestDTO request) {
        if (request.getType() == PostType.ANALYSIS) {
            if (request.getSimplePick() != null) {
                throw new ResponseStatusException(BAD_REQUEST, "Los posts ANALYSIS no deben incluir un pick simple");
            }
            if (request.getParley() != null) {
                throw new ResponseStatusException(BAD_REQUEST, "Los posts ANALYSIS no deben incluir resumen de parley");
            }
            return;
        }

        if (request.getType() == PostType.PICK_SIMPLE) {
            if (request.getSimplePick() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Un PICK_SIMPLE debe incluir datos del pick");
            }
            validateStakeStep(request.getSimplePick().getStake());
            if (request.getParley() != null) {
                throw new ResponseStatusException(BAD_REQUEST, "Un PICK_SIMPLE no debe incluir resumen de parley");
            }
            return;
        }

        if (request.getType() == PostType.PARLEY) {
            if (request.getSimplePick() != null) {
                throw new ResponseStatusException(BAD_REQUEST, "Un PARLEY no puede incluir un pick simple");
            }
            if (request.getParley() == null) {
                throw new ResponseStatusException(BAD_REQUEST, "Un PARLEY debe incluir selecciones y stake");
            }
            if (request.getParley().getSelections() == null || request.getParley().getSelections().isEmpty()) {
                throw new ResponseStatusException(BAD_REQUEST, "Un PARLEY debe incluir al menos una selección");
            }
            validateStakeStep(request.getParley().getStake());
        }
    }

    private PostPickEntity buildSimplePick(PostEntity post, PostPickRequestDTO request) {
        SportEntity sport = getSport(request.getSportId());
        CompetitionEntity competition = getCompetition(request.getLeagueId());
        validateCompetitionBelongsToSport(competition, sport);

        PostPickEntity pick = new PostPickEntity();
        pick.setPost(post);
        pick.setSport(sport);
        pick.setCompetition(competition);
        pick.setLegacySport(sport.getName());
        pick.setLegacyLeague(competition.getName());
        pick.setStake(request.getStake());
        pick.setEventDate(request.getEventDate());
        pick.setResultStatus(request.getResultStatus() != null ? request.getResultStatus() : ResultStatus.PENDING);
        if (request.getSportsbookId() != null) {
            SportsbookEntity sportsbook = sportsbookRepository.findById(request.getSportsbookId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La casa de apuesta no existe"));
            pick.setSportsbook(sportsbook);
        }
        return pick;
    }

    private PostParleyEntity buildParleyDetails(PostEntity post, PostParleyRequestDTO request) {
        ParleySelectionRequestDTO firstSelection = request.getSelections().get(0);
        SportEntity sport = getSport(firstSelection.getSportId());
        CompetitionEntity competition = getCompetition(firstSelection.getLeagueId());
        validateCompetitionBelongsToSport(competition, sport);

        PostParleyEntity parley = new PostParleyEntity();
        parley.setPost(post);
        parley.setSport(sport);
        parley.setCompetition(competition);
        parley.setLegacySport(sport.getName());
        parley.setLegacyLeague(competition.getName());
        parley.setStake(request.getStake());
        if (request.getSportsbookId() != null) {
            SportsbookEntity sportsbook = sportsbookRepository.findById(request.getSportsbookId())
                    .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La casa de apuesta no existe"));
            parley.setSportsbook(sportsbook);
        }
        parley.setEventDate(request.getEventDate());
        parley.setResultStatus(request.getResultStatus() != null ? request.getResultStatus() : ResultStatus.PENDING);
        return parley;
    }

    private List<ParleySelectionEntity> buildParleySelections(PostEntity post, PostParleyRequestDTO request) {
        return request.getSelections().stream()
                .map(selectionRequest -> buildParleySelection(post, selectionRequest))
                .toList();
    }

    private ParleySelectionEntity buildParleySelection(PostEntity post, ParleySelectionRequestDTO request) {
        SportEntity sport = getSport(request.getSportId());
        CompetitionEntity competition = getCompetition(request.getLeagueId());
        validateCompetitionBelongsToSport(competition, sport);

        ParleySelectionEntity selection = new ParleySelectionEntity();
        selection.setPost(post);
        selection.setSport(sport);
        selection.setCompetition(competition);
        return selection;
    }

    private Set<String> normalizeTags(List<String> tags) {
        Set<String> normalized = new LinkedHashSet<>();
        if (tags == null) {
            return normalized;
        }
        for (String tag : tags) {
            if (tag == null) {
                continue;
            }
            String clean = tag.trim();
            if (!clean.isEmpty()) {
                normalized.add(clean);
            }
        }
        return normalized;
    }

    private PostEntity getVisiblePost(Long postId, Long currentUserId) {
        return postRepository.findVisibleById(postId, currentUserId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post no encontrado"));
    }

    private UserEntity getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado"));
    }

    private PagedResponseDTO<PostResponseDTO> mapPage(Page<PostEntity> page, Long currentUserId) {
        PagedResponseDTO<PostResponseDTO> dto = new PagedResponseDTO<>();
        dto.setItems(page.getContent().stream().map(post -> mapPost(post, currentUserId)).toList());
        dto.setPage(page.getNumber());
        dto.setSize(page.getSize());
        dto.setTotalElements(page.getTotalElements());
        dto.setTotalPages(page.getTotalPages());
        dto.setHasNext(page.hasNext());
        return dto;
    }

    private PagedResponseDTO<PostResponseDTO> mapTimelinePage(Page<PostTimelineProjection> timelinePage, Long currentUserId) {
        List<PostTimelineProjection> entries = timelinePage.getContent();
        if (entries.isEmpty()) {
            return mapPage(new PageImpl<>(List.of(), timelinePage.getPageable(), timelinePage.getTotalElements()), currentUserId);
        }

        Map<Long, PostEntity> postsById = new HashMap<>();
        for (PostEntity post : postRepository.findAllById(entries.stream().map(PostTimelineProjection::getPostId).distinct().toList())) {
            postsById.put(post.getId(), post);
        }

        Map<Long, UserEntity> repostUsersById = new HashMap<>();
        List<Long> repostUserIds = entries.stream()
                .map(PostTimelineProjection::getRepostUserId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        if (!repostUserIds.isEmpty()) {
            for (UserEntity user : userRepository.findAllById(repostUserIds)) {
                repostUsersById.put(user.getId(), user);
            }
        }

        List<PostResponseDTO> items = entries.stream()
                .map(entry -> {
                    PostEntity post = postsById.get(entry.getPostId());
                    if (post == null) {
                        return null;
                    }
                    UserEntity repostUser = entry.getRepostUserId() != null ? repostUsersById.get(entry.getRepostUserId()) : null;
                    return mapPost(post, currentUserId, entry.getRepostId(), repostUser, entry.getRepostCreatedAt());
                })
                .filter(item -> item != null)
                .toList();

        PagedResponseDTO<PostResponseDTO> dto = new PagedResponseDTO<>();
        dto.setItems(items);
        dto.setPage(timelinePage.getNumber());
        dto.setSize(timelinePage.getSize());
        dto.setTotalElements(timelinePage.getTotalElements());
        dto.setTotalPages(timelinePage.getTotalPages());
        dto.setHasNext(timelinePage.hasNext());
        return dto;
    }

    private PostResponseDTO mapPost(PostEntity entity, Long currentUserId) {
        return mapPost(entity, currentUserId, null, null, null);
    }

    private PostResponseDTO mapPost(
            PostEntity entity,
            Long currentUserId,
            Long repostId,
            UserEntity repostUser,
            LocalDateTime repostedAt
    ) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setContent(entity.getContent());
        dto.setVisibility(entity.getVisibility());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setAuthor(mapAuthor(entity.getAuthor()));
        dto.setTimelineEntryId(repostId != null ? "repost-" + repostId : "post-" + entity.getId());
        dto.setRepostEntry(repostId != null);
        dto.setRepostedAt(repostedAt);
        dto.setRepostedBy(repostUser != null ? mapAuthor(repostUser) : null);
        dto.setMediaUrls(entity.getMedia().stream()
                .map(PostMediaEntity::getUrl)
                .map(postMediaStorageService::resolvePublicUrl)
                .toList());
        dto.setTags(entity.getTags().stream().map(PostTagEntity::getTag).toList());
        dto.setSimplePick(entity.getSimplePick() != null ? mapSimplePick(entity.getSimplePick()) : null);
        dto.setParley(entity.getParleyDetails() != null ? mapParley(entity.getParleyDetails()) : null);
        dto.setParleySelections(entity.getParleySelections().stream()
                .map(this::mapParleySelection)
                .toList());
        dto.setParleyEventDate(entity.getParleyDetails() != null ? entity.getParleyDetails().getEventDate() : null);
        dto.setMetrics(buildMetrics(entity.getId(), currentUserId));
        return dto;
    }

    private PostPickResponseDTO mapSimplePick(PostPickEntity entity) {
        PostPickResponseDTO dto = new PostPickResponseDTO();
        dto.setId(entity.getId());
        dto.setSportId(entity.getSport().getId());
        dto.setSport(entity.getSport().getName());
        dto.setLeagueId(entity.getCompetition().getId());
        dto.setLeague(entity.getCompetition().getName());
        dto.setStake(entity.getStake());
        dto.setSportsbook(entity.getSportsbook() != null ? sportsbookService.map(entity.getSportsbook()) : null);
        dto.setEventDate(entity.getEventDate());
        dto.setResultStatus(entity.getResultStatus());
        return dto;
    }

    private PostParleyResponseDTO mapParley(PostParleyEntity entity) {
        PostParleyResponseDTO dto = new PostParleyResponseDTO();
        dto.setId(entity.getId());
        dto.setStake(entity.getStake());
        dto.setSportsbook(entity.getSportsbook() != null ? sportsbookService.map(entity.getSportsbook()) : null);
        dto.setEventDate(entity.getEventDate());
        dto.setResultStatus(entity.getResultStatus());
        return dto;
    }

    private ParleySelectionResponseDTO mapParleySelection(ParleySelectionEntity entity) {
        ParleySelectionResponseDTO dto = new ParleySelectionResponseDTO();
        dto.setId(entity.getId());
        dto.setSportId(entity.getSport().getId());
        dto.setSport(entity.getSport().getName());
        dto.setLeagueId(entity.getCompetition().getId());
        dto.setLeague(entity.getCompetition().getName());
        return dto;
    }

    private CommentResponseDTO mapComment(CommentEntity entity, Long currentUserId) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setAuthor(mapAuthor(entity.getAuthor()));
        dto.setLikesCount(commentLikeRepository.countByCommentId(entity.getId()));
        dto.setLikedByCurrentUser(
                currentUserId != null
                        && commentLikeRepository.findByCommentIdAndUserId(entity.getId(), currentUserId).isPresent()
        );
        return dto;
    }

    private PostAuthorResponseDTO mapAuthor(UserEntity user) {
        PostAuthorResponseDTO dto = new PostAuthorResponseDTO();
        dto.setId(user.getId());
        dto.setName((user.getName() + " " + user.getLastname()).trim());
        dto.setUsername(user.getUsername());

        TipsterProfileEntity tipsterProfile = tipsterProfileRepository.findByUser(user).orElse(null);
        String avatarKey = tipsterProfile != null && tipsterProfile.getAvatarUrl() != null && !tipsterProfile.getAvatarUrl().isBlank()
                ? tipsterProfile.getAvatarUrl()
                : user.getProfilePhotoKey();
        dto.setAvatarUrl(resolvePublicUrl(avatarKey));
        boolean validated = tipsterProfile != null && Boolean.TRUE.equals(tipsterProfile.getValidated());
        dto.setValidatedTipster(validated);
        dto.setBadge(validated ? "Verified Tipster" : null);
        return dto;
    }

    private PostMetricsResponseDTO buildMetrics(Long postId, Long currentUserId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Post no encontrado"));
        PostMetricsResponseDTO dto = new PostMetricsResponseDTO();
        dto.setCommentsCount(commentRepository.countByPostId(postId));
        dto.setLikesCount(reactionRepository.countByPostIdAndType(postId, ReactionType.LIKE));
        dto.setDislikesCount(reactionRepository.countByPostIdAndType(postId, ReactionType.DISLIKE));
        dto.setSavesCount(savedPostRepository.countByPostId(postId));
        dto.setViewsCount(postViewRepository.countByPostId(postId));
        dto.setSharesCount(postShareRepository.countByPostIdAndUserIdNot(postId, post.getAuthor().getId()));
        dto.setRepostsCount(postRepostRepository.countByPostId(postId));
        if (currentUserId == null) {
            dto.setCurrentUserReaction(null);
            dto.setSavedByCurrentUser(false);
            dto.setRepostedByCurrentUser(false);
            return dto;
        }
        dto.setCurrentUserReaction(reactionRepository.findByPostIdAndUserId(postId, currentUserId).map(ReactionEntity::getType).orElse(null));
        dto.setSavedByCurrentUser(savedPostRepository.findByPostIdAndUserId(postId, currentUserId).isPresent());
        dto.setRepostedByCurrentUser(postRepostRepository.findByPostIdAndUserId(postId, currentUserId).isPresent());
        return dto;
    }

    private SportEntity getSport(Long sportId) {
        return sportRepository.findById(sportId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "El deporte seleccionado no existe"));
    }

    private CompetitionEntity getCompetition(Long competitionId) {
        return competitionRepository.findById(competitionId)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "La liga seleccionada no existe"));
    }

    private void validateCompetitionBelongsToSport(CompetitionEntity competition, SportEntity sport) {
        if (!competition.getSport().getId().equals(sport.getId())) {
            throw new ResponseStatusException(BAD_REQUEST, "La liga no pertenece al deporte seleccionado");
        }
    }

    private void validateStakeStep(Integer stake) {
        if (stake == null || stake % 10 != 0) {
            throw new ResponseStatusException(BAD_REQUEST, "El stake debe ir de 10 en 10");
        }
    }

    private String resolvePublicUrl(String storedValue) {
        if (storedValue == null || storedValue.isBlank()) {
            return null;
        }
        if (storedValue.startsWith("http://") || storedValue.startsWith("https://")) {
            return storedValue;
        }
        String base = r2Properties.getPublicBaseUrl();
        if (base == null || base.isBlank()) {
            return storedValue;
        }
        return base.replaceAll("/$", "") + "/" + storedValue;
    }
}
