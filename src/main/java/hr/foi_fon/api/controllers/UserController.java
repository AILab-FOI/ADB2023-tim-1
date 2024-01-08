package hr.foi_fon.api.controllers;

import hr.foi_fon.api.models.User;
import hr.foi_fon.api.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;



    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return new ResponseEntity<List<User>>(userService.allUsers(), HttpStatus.OK);
    }

    @CrossOrigin
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody Map<String, Object> payload) {
        try {
            return userService.registerUser(payload);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }


    }
    @CrossOrigin
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody Map<String, Object> payload) {
        try {
            System.out.println("Received payload:");
            payload.forEach((key, value) -> System.out.println(key + ": " + value));

            ResponseEntity<Object> loginResult = userService.loginUser(payload);
            return new ResponseEntity<>(loginResult.getBody(), loginResult.getHeaders(), loginResult.getStatusCode());
        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
