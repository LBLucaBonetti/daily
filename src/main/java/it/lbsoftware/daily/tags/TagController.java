package it.lbsoftware.daily.tags;

import it.lbsoftware.daily.appusers.AppUser;
import it.lbsoftware.daily.appusers.AppUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping
    public ResponseEntity<Tag> createTag(@Valid @RequestBody Tag tag, JwtAuthenticationToken jwtAuthenticationToken) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        if (appUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        Tag createdTag = tagService.createTag(tag, appUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Tag> readTag(@PathVariable("id") Long id, JwtAuthenticationToken jwtAuthenticationToken) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        if (appUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        Optional<Tag> readTag = tagService.readTag(id, appUser);
        if (readTag.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(readTag.get());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable("id") Long id, @Valid @RequestBody Tag tag, JwtAuthenticationToken jwtAuthenticationToken) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        if (appUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        Optional<Tag> updatedTag = tagService.updateTag(id, tag, appUser);
        if (updatedTag.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Tag> deleteTag(@PathVariable("id") Long id, JwtAuthenticationToken jwtAuthenticationToken) {
        Optional<AppUser> appUserOptional = appUserService.getAppUserFromToken(jwtAuthenticationToken);
        if (appUserOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User could not be found");
        }
        AppUser appUser = appUserOptional.get();
        if (!tagService.deleteTag(id, appUser)) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }

}
