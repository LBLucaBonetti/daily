package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exception.DailyBadRequestException;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
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

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/tags")
@CommonsLog
class TagController {

  private final TagService tagService;
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
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<PageDto<TagDto>> readTags(
      Pageable pageable, @AuthenticationPrincipal OidcUser appUser) {
    Page<TagDto> readTags;
    try {
      readTags = tagService.readTags(pageable, appUserService.getUid(appUser));
    } catch (Exception e) {
      log.error(e);
      throw new DailyBadRequestException(null);
    }
    PageDto<TagDto> readTagDtos = new PageDto<>(readTags);

    return ResponseEntity.ok(readTagDtos);
  }

  @PutMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> updateTag(
      @PathVariable("uuid") UUID uuid,
      @Valid @RequestBody TagDto tagDto,
      @AuthenticationPrincipal OidcUser appUser) {
    return tagService
        .updateTag(uuid, tagDto, appUserService.getUid(appUser))
        .<ResponseEntity<TagDto>>map(updatedTag -> ResponseEntity.noContent().build())
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> deleteTag(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal OidcUser appUser) {
    tagService.deleteTag(uuid, appUserService.getUid(appUser));

    return ResponseEntity.noContent().build();
  }
}
