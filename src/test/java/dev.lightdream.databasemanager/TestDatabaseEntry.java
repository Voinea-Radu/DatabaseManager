package dev.lightdream.databasemanager;

import com.google.gson.Gson;
import dev.lightdream.databasemanager.dto.DatabaseEntry;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("JpaDataSourceORMInspection")
@Entity
@Table(name = "test_table",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"id"})
        }
)
public class TestDatabaseEntry extends DatabaseEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, unique = true, length = 11)
    public Integer id;
    @Column(name = "data1")
    public String data1;
    @Column(name = "data2")
    public String data2;
    @Column(name = "uuid")
    public UUID uuid;
    @Column(name = "string_list")
    public String stringList;
    @Column(name = "uuid_list")
    public String uuidList;
    @Column(name = "integer")
    public Integer integer;

    public TestDatabaseEntry(String data1, String data2, UUID uuid, List<String> stringList, List<UUID> uuidList, Integer integer) {
        super(TestDatabaseMain.instance);
        this.data1 = data1;
        this.data2 = data2;
        this.uuid = uuid;
        this.stringList = new Gson().toJson(stringList);
        this.uuidList = new Gson().toJson(uuidList);
        this.integer = integer;
    }

    public TestDatabaseEntry(){
        super(TestDatabaseMain.instance);
    }

    @Override
    public Object getID() {
        return id;
    }
}
