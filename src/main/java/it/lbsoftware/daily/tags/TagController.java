package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import it.lbsoftware.daily.entities.DtoEntityMappingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<TagDto> readTag(@PathVariable("uuid") UUID uuid) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Optional<Tag> readTag = tagService.readTag(uuid, appUser);
        if (readTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TagDto readTagDto = tagConverter.convertToDto(readTag.get(), TagDto.class);

        return ResponseEntity.ok(readTagDto);
    }

    @GetMapping()
    public ResponseEntity<List<TagDto>> readTags() {
        AppUser appUser = appUserService.getAppUserFromToken();
        List<Tag> readTags = tagService.readTags(appUser);
        List<TagDto> readTagDtos = readTags.stream().map(readTag -> tagConverter.convertToDto(readTag, TagDto.class)).collect(Collectors.toList());

        return ResponseEntity.ok(readTagDtos);
    }

    @PutMapping(value = "/{uuid}")
    public ResponseEntity<TagDto> updateTag(@PathVariable("uuid") UUID uuid, @Valid @RequestBody TagDto tagDto) {
        AppUser appUser = appUserService.getAppUserFromToken();
        Tag tag = tagConverter.convertToEntity(tagDto, Tag.class);
        Optional<Tag> updatedTag = tagService.updateTag(uuid, tag, appUser);
        if (updatedTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<TagDto> deleteTag(@PathVariable("uuid") UUID uuid) {
        AppUser appUser = appUserService.getAppUserFromToken();
        if (!tagService.deleteTag(uuid, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
