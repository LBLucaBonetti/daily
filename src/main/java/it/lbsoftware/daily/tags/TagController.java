package it.lbsoftware.daily.tags;

import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/tags")
class TagController {

  private final TagService tagService;
  private final TagDtoMapper tagDtoMapper;

  @PostMapping
  public ResponseEntity<TagDto> createTag(@Valid @RequestBody TagDto tagDto, Principal appUser) {
    Tag tag = tagDtoMapper.convertToEntity(tagDto);
    Tag createdTag = tagService.createTag(tag, appUser.getName());
    TagDto createdTagDto = tagDtoMapper.convertToDto(createdTag);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTagDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> readTag(@PathVariable("uuid") UUID uuid, Principal appUser) {
    Optional<Tag> readTag = tagService.readTag(uuid, appUser.getName());
    if (readTag.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    TagDto readTagDto = tagDtoMapper.convertToDto(readTag.get());

    return ResponseEntity.ok(readTagDto);
  }

  @GetMapping
  public ResponseEntity<List<TagDto>> readTags(Principal appUser) {
    List<Tag> readTags = tagService.readTags(appUser.getName());
    List<TagDto> readTagDtos =
        readTags.stream()
            .map(tagDtoMapper::convertToDto)
            .collect(Collectors.toCollection(LinkedList::new));

    return ResponseEntity.ok(readTagDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> updateTag(
      @PathVariable("uuid") UUID uuid, @Valid @RequestBody TagDto tagDto, Principal appUser) {
    Tag tag = tagDtoMapper.convertToEntity(tagDto);
    Optional<Tag> updatedTag = tagService.updateTag(uuid, tag, appUser.getName());
    if (updatedTag.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> deleteTag(@PathVariable("uuid") UUID uuid, Principal appUser) {
    if (!Boolean.TRUE.equals(tagService.deleteTag(uuid, appUser.getName()))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }
}
