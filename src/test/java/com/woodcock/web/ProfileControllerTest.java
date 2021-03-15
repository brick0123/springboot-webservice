package com.woodcock.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.env.MockEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProfileControllerTest {

  @LocalServerPort
  int port;

  @Autowired
  TestRestTemplate restTemplate;

  @Test
  @DisplayName("profile은 인증 없이 호출된다")
  void profile_invoke() {
    String expected = "default";

    ResponseEntity<String> response = restTemplate.getForEntity("/profile", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expected);
  }

  @Test
  void real_profile이_조회된다() {
    // given
    String expectedProfile = "real";
    MockEnvironment env = new MockEnvironment();
    env.addActiveProfile(expectedProfile);
    env.addActiveProfile("oauth");
    env.addActiveProfile("real-db");

    ProfileController controller = new ProfileController(env);

    // when
    String profile = controller.profile();

    // then
    assertThat(profile).isEqualTo(expectedProfile);
  }

  @Test
  @DisplayName("realProfile이 없으면 첫번째가 조회된다")
  void real_profile이_없으면_첫번째가_조회() {
    // given
    String expectedProfile = "oauth";
    MockEnvironment env = new MockEnvironment();

    ProfileController controller = new ProfileController(env);
    env.addActiveProfile(expectedProfile);
    env.addActiveProfile("real-db");

    // when
    String profile = controller.profile();

    // then
    assertThat(profile).isEqualTo(expectedProfile);
  }

    @Test
    @DisplayName("active_profile이 없으면 default가 조회된다")
    void default_조회된다 () {
    // given
    String expectedProfile = "default";
    MockEnvironment env = new MockEnvironment();

    ProfileController controller = new ProfileController(env);

    // when
    String profile = controller.profile();

    // then
    assertThat(profile).isEqualTo(expectedProfile);
  }

  }