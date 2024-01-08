package hr.foi_fon.api.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;


@Document(collection="users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

        @Id
        @JsonSerialize(using = ToStringSerializer.class)
        private ObjectId id;

        private String first_name;

        private String last_name;

        private String email;

        private String username;

        private String password;

        private LocalDate date_of_birth;

        private LocalDateTime date_created;

        private LocalDateTime date_modified;

        private List<ObjectId> preferences;

        private List<ObjectId> history;

        private List<ObjectId> watchlist;

        private List<ObjectId> reviewed_movies;

        private List<ObjectId> favorite_actors;





}

