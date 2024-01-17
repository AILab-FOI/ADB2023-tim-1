package hr.foi_fon.api.dtos;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import hr.foi_fon.api.models.GenreWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieDto {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String title;

    private LocalDate date_release;


    private MovieGenresDto genres;


    private String director;

    @JsonSerialize(using = ToStringSerializer.class)
    private List<String> actors;

    private String description;

    private Integer duration;

}
