package hr.foi_fon.api.dtos;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MovieGenresDto {
    private String primary;
    private List<String> secondary;
}