package objects;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.Audited;

import java.util.Objects;

@Entity
@Table(name = "location")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Audited
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    @SequenceGenerator(name = "location_seq", sequenceName = "location_sequence", allocationSize = 50)
    private long id;

    @Column(name = "x")
    private int x;

    @NotNull(message = "Поле y не должно быть пустым")
    @Column(name = "y")
    private Integer y; //Поле не может быть null

    @Column(name = "z")
    private int z;

    // ------------ добавленные

    @Column(name = "owner_id")
    private long ownerId;

    @Column(name = "updated_by")
    private long updatedBy;

    @Column(name = "allow_editing")
    private boolean allowEditing;

    // ------------

    public Location(int x, Integer y, int z, long ownerId, boolean allowEditing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.ownerId = ownerId;
        this.allowEditing = allowEditing;
    }

    public Location(int x, Integer y, int z, boolean allowEditing) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.allowEditing = allowEditing;
    }

    public String toJson() {
        try (Jsonb jsonb = JsonbBuilder.create()) {
            return jsonb.toJson(this);
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @Override
    public String toString() {
        return (id + ". x: " + x + ", y: " + y + ", z: " + z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return x == location.x &&
                z == location.z &&
                ownerId == location.ownerId &&
                Objects.equals(y, location.y);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z, ownerId);
    }

    public boolean isValid() {
        Location l = this;

        return (
                !Objects.isNull(l.y)
        );
    }
}
