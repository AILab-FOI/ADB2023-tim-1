package hr.foi_fon.api.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="reviews")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private ObjectId user_id;

    private ObjectId movie_id;

    private Integer review;

    private String description;
}
