package com.woodcock.domain.posts;

import static org.assertj.core.api.Assertions.assertThat;

import com.woodcock.config.JpaConfig;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.annotation.Rollback;

@DataJpaTest(includeFilters = {
    @Filter(type = FilterType.ASSIGNABLE_TYPE,value = JpaConfig.class)})
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(value = false)
class PostsRepositoryTest {

  @Autowired
  PostsRepository postsRepository;

  @AfterEach
  void cleanup() {
    postsRepository.deleteAll();
  }

  @Test
  @DisplayName("게시글 불러오기")
  void findPosts() {
    // given
    String title = "title";
    String content = "content";

    postsRepository.save(Posts.builder()
        .title(title)
        .content(content)
        .author("woodcock@gmail.com")
        .build());
    // when
    List<Posts> postsList = postsRepository.findAll();

    // then
    Posts posts = postsList.get(0);
    assertThat(posts.getTitle()).isEqualTo(title);
    assertThat(posts.getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("BaseTimeEntity 등록")
  void baseTimeEntity_등록() {
    LocalDateTime now = LocalDateTime.of(2020, 2, 20, 0, 0, 0);
    postsRepository.save(
        Posts.builder()
            .title("title")
            .content("content")
            .build());

    // when
    List<Posts> postsList = postsRepository.findAll();

    // then
    Posts posts = postsList.get(0);

    System.out.println(">>> createDate=" + posts.getCreateDate()
        +", modifiedDate=" + posts.getModifiedDate()
    );

    assertThat(posts.getCreateDate()).isAfter(now);
    assertThat(posts.getModifiedDate()).isAfter(now);
  }
}