package dto;

import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.DefaultValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DragonCaveDTO {
    @DefaultValue(value="-1")
    private long id;
    @Positive
    private float numberOfTreasures; //Значение поля должно быть больше 0

    // ------------ добавленные

    @DefaultValue(value="-1")
    private long ownerId;
    @DefaultValue(value="-1")
    private long updatedBy;
    @DefaultValue(value="false")
    private boolean allowEditing;
}
