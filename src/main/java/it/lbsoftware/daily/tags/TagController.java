package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
// TODO Change @CrossOrigin to match the deployed frontend URL
@CrossOrigin(origins = "http://localhost:8080")
@RequestMapping(value = "/tags")
class TagController {

    private final AppUserService appUserService;
    private final TagService tagService;

    @PostMapping
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag, JwtAuthenticationToken jwtAuthenticationToken) {
        AppUser appUser;
        try {
            appUser = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        Tag createdTag;
        try {
            createdTag = tagService.createTag(tag, appUser);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tag could not be created");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Tag> readTag(@PathVariable("id") Long id, JwtAuthenticationToken jwtAuthenticationToken) {
        AppUser appUser;
        try {
            appUser = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        Tag readTag;
        try {
            readTag = tagService.readTag(id, appUser);
        } catch (IllegalStateException ise) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not access the tag");
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag could not be read");
        }

        return ResponseEntity.ok(readTag);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable("id") Long id, @Valid @RequestBody Tag tag, JwtAuthenticationToken jwtAuthenticationToken) {
        AppUser appUser;
        try {
            appUser = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        try {
            tagService.updateTag(id, tag, appUser);
        } catch (IllegalStateException ise) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not access the tag");
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag could not be found");
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Tag> deleteTag(@PathVariable("id") Long id, JwtAuthenticationToken jwtAuthenticationToken) {
        AppUser appUser;
        try {
            appUser = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        } catch (EntityNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }

        try {
            tagService.deleteTag(id, appUser);
        } catch (IllegalStateException ise) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not access the tag");
        } catch (EntityNotFoundException enfe) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tag could not be found");
        }

        return ResponseEntity.noContent().build();
    }

}
