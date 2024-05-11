package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.bases.PageDto;
import it.lbsoftware.daily.exceptions.DailyBadRequestException;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
      @Valid @RequestBody TagDto tagDto, @AuthenticationPrincipal Object principal) {
    log.info("POST request to /api/tags; parameters: %s".formatted(tagDto.toString()));
    TagDto createdTagDto = tagService.createTag(tagDto, appUserService.getAppUser(principal));

    return ResponseEntity.status(HttpStatus.CREATED).body(createdTagDto);
  }

  @GetMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> readTag(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/tags/%s".formatted(uuid.toString()));
    return tagService
        .readTag(uuid, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @GetMapping
  public ResponseEntity<PageDto<TagDto>> readTags(
      Pageable pageable, @AuthenticationPrincipal Object principal) {
    log.info("GET request to /api/tags with paging");
    Page<TagDto> readTags;
    try {
      readTags = tagService.readTags(pageable, appUserService.getAppUser(principal));
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
      @AuthenticationPrincipal Object principal) {
    log.info(
        "PUT request to /api/tags/%s; parameters: %s"
            .formatted(uuid.toString(), tagDto.toString()));
    return tagService
        .updateTag(uuid, tagDto, appUserService.getAppUser(principal))
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @DeleteMapping(value = "/{uuid}")
  public ResponseEntity<TagDto> deleteTag(
      @PathVariable("uuid") UUID uuid, @AuthenticationPrincipal Object principal) {
    log.info("DELETE request to /api/tags/%s".formatted(uuid.toString()));
    tagService.deleteTag(uuid, appUserService.getAppUser(principal));

    return ResponseEntity.noContent().build();
  }
}
