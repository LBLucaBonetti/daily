package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUserService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
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
  private final AppUserService appUserService;

  @PostMapping
  public ResponseEntity<TagDto> createTag(
      @Valid @RequestBody TagDto tagDto, @AuthenticationPrincipal OidcUser appUser) {
    Tag tag = tagDtoMapper.convertToEntity(tagDto);
    Tag createdTag = tagService.createTag(tag, appUserService.getUid(appUser));
    TagDto createdTagDto = tagDtoMapper.convertToDto(createdTag);

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTagDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> readTag(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    Optional<Tag> readTag = tagService.readTag(uuid, appUserService.getUid(appUser));
    if (readTag.isEmpty()) {
      return ResponseEntity.notFound().build();
    }
    TagDto readTagDto = tagDtoMapper.convertToDto(readTag.get());

    return ResponseEntity.ok(readTagDto);
  }

  @GetMapping
  public ResponseEntity<List<TagDto>> readTags(@AuthenticationPrincipal OidcUser appUser) {
    List<Tag> readTags = tagService.readTags(appUserService.getUid(appUser));
    List<TagDto> readTagDtos = tagDtoMapper.convertToDto(readTags);

    return ResponseEntity.ok(readTagDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> updateTag(
      @PathVariable("uuid") UUID uuid,
      @Valid @RequestBody TagDto tagDto,
      @AuthenticationPrincipal OidcUser appUser) {
    Tag tag = tagDtoMapper.convertToEntity(tagDto);
    Optional<Tag> updatedTag = tagService.updateTag(uuid, tag, appUserService.getUid(appUser));
    if (updatedTag.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> deleteTag(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    if (!Boolean.TRUE.equals(tagService.deleteTag(uuid, appUserService.getUid(appUser)))) {
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.noContent().build();
  }
}
