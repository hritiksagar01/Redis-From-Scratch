package Components.Infra;

import java.util.ArrayList;
import java.util.List;

public class Slave {
    public Client connection;
    public List<String> capabilities;

    public Slave(Client connection, List<String> capabilites) {
        this.connection = connection;
        this.capabilities = new ArrayList<>();
    }
}
