package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
    private final TagDtoMapper tagDtoMapper;

    @PostMapping
    public ResponseEntity<TagDto> createTag(@Valid @RequestBody TagDto tagDto) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if(appUserOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        Tag tag = tagDtoMapper.convertToEntity(tagDto);
        Tag createdTag = tagService.createTag(tag, appUser);
        TagDto createdTagDto = tagDtoMapper.convertToDto(createdTag);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTagDto);
    }

    @GetMapping(value = "/{uuid}")
    public ResponseEntity<TagDto> readTag(@PathVariable("uuid") UUID uuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if(appUserOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        Optional<Tag> readTag = tagService.readTag(uuid, appUser);
        if (readTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        TagDto readTagDto = tagDtoMapper.convertToDto(readTag.get());

        return ResponseEntity.ok(readTagDto);
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> readTags() {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if(appUserOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        List<Tag> readTags = tagService.readTags(appUser);
        List<TagDto> readTagDtos = readTags.stream().map(tagDtoMapper::convertToDto).collect(Collectors.toList());

        return ResponseEntity.ok(readTagDtos);
    }

    @PutMapping(value = "/{uuid}")
    public ResponseEntity<TagDto> updateTag(@PathVariable("uuid") UUID uuid, @Valid @RequestBody TagDto tagDto) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if(appUserOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        Tag tag = tagDtoMapper.convertToEntity(tagDto);
        Optional<Tag> updatedTag = tagService.updateTag(uuid, tag, appUser);
        if (updatedTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{uuid}")
    public ResponseEntity<TagDto> deleteTag(@PathVariable("uuid") UUID uuid) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken();
        if(appUserOptional.isEmpty()){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        if (!tagService.deleteTag(uuid, appUser)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

}
