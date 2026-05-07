package com.samuel_mc.pickados_api.controllers.post;

import com.samuel_mc.pickados_api.entity.CustomUserDetails;
import com.samuel_mc.pickados_api.entity.FollowEntity;
import com.samuel_mc.pickados_api.entity.PostEntity;
import com.samuel_mc.pickados_api.entity.UserEntity;
import com.samuel_mc.pickados_api.entity.enums.PostType;
import com.samuel_mc.pickados_api.entity.enums.PostVisibility;
import com.samuel_mc.pickados_api.repository.UserRepository;
import com.samuel_mc.pickados_api.repository.post.FollowRepository;
import com.samuel_mc.pickados_api.repository.post.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PostsVisibilityIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired UserRepository userRepository;
    @Autowired PostRepository postRepository;
    @Autowired FollowRepository followRepository;

    private UserEntity author;
    private UserEntity deletedAuthor;
    private UserEntity viewer;

    private PostEntity publicPost;
    private PostEntity followersOnlyPost;
    private PostEntity privatePost;
    private PostEntity deletedAuthorPost;

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        postRepository.deleteAll();
        userRepository.deleteAll();

        author = userRepository.save(buildUser("author", false));
        deletedAuthor = userRepository.save(buildUser("deleted_author", true));
        viewer = userRepository.save(buildUser("viewer", false));

        publicPost = postRepository.save(buildPost(author, PostVisibility.PUBLIC));
        followersOnlyPost = postRepository.save(buildPost(author, PostVisibility.FOLLOWERS_ONLY));
        privatePost = postRepository.save(buildPost(author, PostVisibility.PRIVATE));
        deletedAuthorPost = postRepository.save(buildPost(deletedAuthor, PostVisibility.PUBLIC));
    }

    @Test
    void getPostDetail_filtersDeletedAuthors_andEnforcesVisibility() throws Exception {
        mockMvc.perform(get("/posts/{postId}", publicPost.getId())
                        .with(authenticated(viewer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/{postId}", followersOnlyPost.getId())
                        .with(authenticated(viewer)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/posts/{postId}", privatePost.getId())
                        .with(authenticated(viewer)))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/posts/{postId}", deletedAuthorPost.getId())
                        .with(authenticated(viewer)))
                .andExpect(status().isNotFound());

        followRepository.save(buildFollow(viewer, author));

        mockMvc.perform(get("/posts/{postId}", followersOnlyPost.getId())
                        .with(authenticated(viewer)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/{postId}", privatePost.getId())
                        .with(authenticated(author)))
                .andExpect(status().isOk());
    }

    @Test
    void getPublicDetail_allowsOnlyPublicPostsFromNonDeletedAuthors() throws Exception {
        mockMvc.perform(get("/posts/public/{postId}", publicPost.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/posts/public/{postId}", followersOnlyPost.getId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/posts/public/{postId}", privatePost.getId()))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/posts/public/{postId}", deletedAuthorPost.getId()))
                .andExpect(status().isNotFound());
    }

    private static RequestPostProcessor authenticated(UserEntity user) {
        return SecurityMockMvcRequestPostProcessors.user(new CustomUserDetails(user));
    }

    private static UserEntity buildUser(String username, boolean deleted) {
        UserEntity user = new UserEntity();
        user.setName(username);
        user.setLastname("test");
        user.setUsername(username);
        user.setEmail(username + "@example.com");
        user.setPassword("password");
        user.setActive(true);
        user.setDeleted(deleted);
        return user;
    }

    private static PostEntity buildPost(UserEntity author, PostVisibility visibility) {
        PostEntity post = new PostEntity();
        post.setAuthor(author);
        post.setType(PostType.ANALYSIS);
        post.setContent("test content");
        post.setVisibility(visibility);
        return post;
    }

    private static FollowEntity buildFollow(UserEntity follower, UserEntity followed) {
        FollowEntity entity = new FollowEntity();
        entity.setFollower(follower);
        entity.setFollowed(followed);
        return entity;
    }
}
