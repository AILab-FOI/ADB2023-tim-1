package hr.foi_fon.api.services;

import hr.foi_fon.api.models.User;
import hr.foi_fon.api.repositories.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> allUsers(){
        List<User> users = userRepository.findAll();
        return users;
    }


    public ResponseEntity<Object> registerUser(Map<String, Object> payload) {
        User user = new User();
        if (userRepository.findByEmail((String) payload.get("email")) != null) {
            return new ResponseEntity<>(Map.of("error","User with that email already exists!"), HttpStatus.BAD_REQUEST);
        }
        List<ObjectId> preferenceIds = (List<ObjectId>) payload.get("preferences");
        List<ObjectId> watchlistIds = (List<ObjectId>) payload.get("watchlist");
        List<ObjectId> historyIds = (List<ObjectId>) payload.get("history");
        List<ObjectId> reviewedMoviesIds = (List<ObjectId>) payload.get("reviewed_movies");
        List<ObjectId> favoriteActorsIds = (List<ObjectId>) payload.get("favorite_actors");
        String dateOfBirthString = (String) payload.get("date_of_birth");
        LocalDate dateOfBirth = LocalDate.parse(dateOfBirthString);


        user.setFirst_name((String) payload.get("first_name"));
        user.setLast_name((String) payload.get("last_name"));
        user.setEmail((String) payload.get("email"));
        user.setUsername((String) payload.get("username"));
        user.setPassword(hashPassword ((String) payload.get("password")));
        user.setDate_of_birth(dateOfBirth);
        user.setDate_created(LocalDateTime.now());
        user.setDate_modified(LocalDateTime.now());
        user.setPreferences(preferenceIds);
        user.setWatchlist(watchlistIds);
        user.setHistory(historyIds);
        user.setReviewed_movies(reviewedMoviesIds);
        user.setFavorite_actors(favoriteActorsIds);

        userRepository.insert(user);
        return new ResponseEntity<>(Map.of("message","User added successfully"), HttpStatus.CREATED);
    }
    private boolean isValidField(Map<String, Object> payload, String field) {
        return payload.containsKey(field) && payload.get(field) != null && !payload.get(field).toString().isEmpty();
    }
    public ResponseEntity<Object> loginUser(Map<String, Object> payload) {
        if (!isValidField(payload, "email") || !isValidField(payload, "password")) {
            return new ResponseEntity<>(Map.of("error","Email and password are required"), HttpStatus.BAD_REQUEST);
        }

        String email = (String) payload.get("email");
        String password = (String) payload.get("password");

        User user = userRepository.findByEmail(email);

        if (user == null) {
            return new ResponseEntity<>(Map.of("error", "User not found"), HttpStatus.NOT_FOUND);
        }

        if (!checkPassword(password, user.getPassword())) {
            return new ResponseEntity<>(Map.of("error", "Incorrect password"), HttpStatus.UNAUTHORIZED);
        } else {


            Map<String, Object> response = Map.of("message", "Login successful");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    public static String hashPassword(String plainTextPassword) {
        BCrypt.Hasher hasher = BCrypt.withDefaults();
        int cost = 12;
        char[] hashedPasswordChars = hasher.hashToChar(cost, plainTextPassword.toCharArray());
        return new String(hashedPasswordChars);
    }
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        BCrypt.Verifyer verifyer = BCrypt.verifyer();
        BCrypt.Result result = verifyer.verify(plainTextPassword.toCharArray(), hashedPassword.toCharArray());
        return result.verified;
    }


}


