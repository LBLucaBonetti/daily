package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.entities.DtoEntityMappingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
// TODO Change @CrossOrigin to match the deployed frontend URL
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value = "/tags")
class TagController {

    private final AppUserService appUserService;
    private final TagService tagService;
    private final DtoEntityMappingConverter<TagDto, Tag> tagConverter;

    @PostMapping
    public ResponseEntity<TagDto> createTag(@Valid @RequestBody TagDto tagDto) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Tag tag = tagConverter.convertToEntity(tagDto, Tag.class);
        Tag createdTag = tagService.createTag(tag, appUser);
        TagDto createdTagDto = tagConverter.convertToDto(createdTag, TagDto.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTagDto);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TagDto> readTag(@PathVariable("id") Long id) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Optional<Tag> readTag = tagService.readTag(id, appUser);
        if (readTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TagDto readTagDto = tagConverter.convertToDto(readTag.get(), TagDto.class);

        return ResponseEntity.ok(readTagDto);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<TagDto> updateTag(@PathVariable("id") Long id, @Valid @RequestBody TagDto tagDto) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Tag tag = tagConverter.convertToEntity(tagDto, Tag.class);
        Optional<Tag> updatedTag = tagService.updateTag(id, tag, appUser);
        if (updatedTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<TagDto> deleteTag(@PathVariable("id") Long id) {
        AppUser appUser = appUserService.getAppUserFromToken();
        if (!tagService.deleteTag(id, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
