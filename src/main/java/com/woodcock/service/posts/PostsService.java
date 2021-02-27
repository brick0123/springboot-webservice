package com.woodcock.service.posts;

import com.woodcock.domain.posts.Posts;
import com.woodcock.domain.posts.PostsRepository;
import com.woodcock.web.dto.PostsListResponseDto;
import com.woodcock.web.dto.PostsResponseDto;
import com.woodcock.web.dto.PostsSaveRequestDto;
import com.woodcock.web.dto.PostsUpdateRequestDto;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostsService {

  private final PostsRepository postsRepository;

  @Transactional
  public Long save(PostsSaveRequestDto requestDto) {
    return postsRepository.save(requestDto.toEntity()).getId();
  }

  @Transactional
  public Long update(Long id, PostsUpdateRequestDto requestDto) {
    Posts posts = postsRepository.findById(id)
        .orElseThrow(() ->
            new EntityNotFoundException("해당 게시글이 없습니다. id=" + id));

    posts.update(requestDto.getTitle(), requestDto.getContent());

    return id;
  }

  public PostsResponseDto findById(Long id) {
    Posts posts = postsRepository.findById(id)
        .orElseThrow(() ->
            new EntityNotFoundException("해당 게시글이 없습니다. id=" + id));

    return new PostsResponseDto(posts);
  }

  @Transactional(readOnly = true)
  public List<PostsListResponseDto> findAllByDesc() {
    return postsRepository.findAllByDesc().stream()
        .map(PostsListResponseDto::new)
        .collect(Collectors.toList());
  }

  @Transactional
  public void delete(Long id) {
    Posts posts = postsRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id =" + id));

    postsRepository.delete(posts);
  }
}
