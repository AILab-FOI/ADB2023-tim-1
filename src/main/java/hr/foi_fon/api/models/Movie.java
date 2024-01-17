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
import java.util.List;

@Document(collection="movies")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String title;

    private LocalDate date_release;

    @Field("genres")
    private GenreWrapper genres;

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId director;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<ObjectId> actors;

    private String description;

    private Integer duration;

    private String url;
}
