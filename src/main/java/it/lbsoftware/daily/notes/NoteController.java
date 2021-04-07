package it.lbsoftware.daily.notes;

//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import org.springframework.web.bind.annotation.CrossOrigin;
//import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

//import java.util.Arrays;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;

@RestController
// TODO Change @CrossOrigin to match the deployed frontend URL
@CrossOrigin(origins = "http://localhost:8080")
public class NoteController {

//    @GetMapping("/api/messages")
//    @PreAuthorize("hasAuthority('SCOPE_email')")
//    public Map<String, Object> messages(JwtAuthenticationToken jwtAuthenticationToken) {
//        System.out.println("--- CONTROLLER ---");
//        System.out.println("Sub: " + jwtAuthenticationToken.getName());
//        System.out.println("Uid: " + jwtAuthenticationToken.getTokenAttributes().get("uid"));
//
//        Map<String, Object> result = new HashMap<>();
//        result.put("messages", Arrays.asList(
//                new Message("I am a robot."),
//                new Message("Hello, world!")
//        ));
//
//        return result;
//    }
//
//    class Message {
//
//        public Date date = new Date();
//        public String text;
//
//        Message(String text) {
//            this.text = text;
//        }
//
//    }

}
