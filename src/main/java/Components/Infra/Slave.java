package Components.Infra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class Slave {
   public Client connection;
    public List<String> capabilities;
    public Client getConnection() {
        return connection;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public Slave(Client client) {
        this.connection = client;
        this.capabilities = new ArrayList<>();
    }

    public void send(byte[] bytes) throws IOException {
        if (bytes != null && bytes.length > 0) {
           this.connection.outputStream.write(bytes);
        }
    }
}
