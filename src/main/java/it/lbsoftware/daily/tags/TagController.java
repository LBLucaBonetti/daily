package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.server.ResponseStatusException;

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
    TagDto createdTagDto = tagService.createTag(tagDto, appUserService.getUid(appUser));

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTagDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> readTag(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    return tagService
        .readTag(uuid, appUserService.getUid(appUser))
        .map(readTag -> ResponseEntity.ok(tagDtoMapper.convertToDto(readTag)))
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<PageDto<TagDto>> readTags(
      Pageable pageable, @AuthenticationPrincipal OidcUser appUser) {
    Page<TagDto> readTags;
    try {
      readTags = tagService.readTags(pageable, appUserService.getUid(appUser));
    } catch (Exception e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, e);
    }
    PageDto<TagDto> readTagDtos = new PageDto<>(readTags);

    return ResponseEntity.ok(readTagDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> updateTag(
      @PathVariable("uuid") UUID uuid,
      @Valid @RequestBody TagDto tagDto,
      @AuthenticationPrincipal OidcUser appUser) {
    Tag tag = tagDtoMapper.convertToEntity(tagDto);

    return tagService
        .updateTag(uuid, tag, appUserService.getUid(appUser))
        .<ResponseEntity<TagDto>>map(updatedTag -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
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
