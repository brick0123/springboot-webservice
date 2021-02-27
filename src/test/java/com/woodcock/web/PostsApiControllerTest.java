package com.woodcock.web;

import static org.assertj.core.api.Assertions.assertThat;

import com.woodcock.domain.posts.Posts;
import com.woodcock.domain.posts.PostsRepository;
import com.woodcock.web.dto.PostsSaveRequestDto;
import com.woodcock.web.dto.PostsUpdateRequestDto;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PostsApiControllerTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private PostsRepository postsRepository;

  @AfterEach
  void teatDown() {
    postsRepository.deleteAll();
  }

  @Test
  @DisplayName("Posts 정상 등록")
  void Posts_등록() {
    // given
    String title = "title";
    String content = "content";
    String author = "author";

    PostsSaveRequestDto requestDto = PostsSaveRequestDto.builder()
        .title(title)
        .content(content)
        .author(author)
        .build();

    String url = "http://localhost:" + port + "/api/v1/posts";

    // when
    ResponseEntity<Long> responseEntity = restTemplate
        .postForEntity(url, requestDto, Long.class);

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isGreaterThan(0L);

    List<Posts> postsList = postsRepository.findAll();

    assertThat(postsList.get(0).getTitle()).isEqualTo(title);
    assertThat(postsList.get(0).getContent()).isEqualTo(content);
  }

  @Test
  @DisplayName("posts 수정")
  void Posts_수정() {
    // given
    Posts savePosts = postsRepository.save(Posts.builder()
        .title("title")
        .content("content")
        .author("author")
        .build());

    Long updateId = savePosts.getId();
    String expectedTitle = "title2";
    String expectedContent = "content2";

    PostsUpdateRequestDto requestDto = PostsUpdateRequestDto.builder()
        .title(expectedTitle)
        .content(expectedContent)
        .build();

    String url = "http://localhost:" + port + "/api/v1/posts/" + updateId;

    HttpEntity<PostsUpdateRequestDto> requestEntity =
        new HttpEntity<>(requestDto);

    // when
    ResponseEntity<Long> responseEntity = restTemplate
        .exchange(url, HttpMethod.PUT, requestEntity, Long.class);

    // then
    assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(responseEntity.getBody()).isGreaterThan(0L);

    List<Posts> postsList = postsRepository.findAll();
    assertThat(postsList.get(0).getTitle()).isEqualTo(expectedTitle);
    assertThat(postsList.get(0).getContent()).isEqualTo(expectedContent);

  }
}