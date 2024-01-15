package hr.foi_fon.api.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenreWrapper {

    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId primary;

    @JsonSerialize(contentUsing = ToStringSerializer.class)
    private List<ObjectId> secondary;
}
