package example;

import dev.lightdream.databasemanager.dto.DatabaseEntry;
import jakarta.persistence.*;
import org.hibernate.annotations.Type;

@SuppressWarnings("ALL")
@Entity
@Table(name = "example_table",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"})
        }
)
public class ExampleDatabaseItem extends DatabaseEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 11)
    public Integer id;
    @Column(name = "data_1")
    public String data1;
    @Column(name = "data_2")
    public String data2;

    public ExampleDatabaseItem(String data1, String data2) {
        super(ExampleMain.instance);
        this.data1 = data1;
        this.data2 = data2;
    }

    public ExampleDatabaseItem() {
        super(ExampleMain.instance);
    }

    @Override
    public Object getID() {
        return id;
    }
}
