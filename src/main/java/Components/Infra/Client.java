package Components.Infra;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;


public class Client {
    public Socket socket ;
    public InputStream inputStream;
    public OutputStream outputStream;
    public int id;

    public Client(Socket socket, InputStream inputStream , OutputStream outputStream, int id) {
        this.socket = socket;
        this.id = id;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.id = this.id;
    }

    public void send(String res, byte[] data) throws IOException {
        if (res != null && !res.isEmpty()) {
            outputStream.write(res.getBytes(StandardCharsets.UTF_8));
            if (data != null && data.length > 0) {
                outputStream.write(data);
            }
        }
    }

    public void send(byte[] data) throws IOException {
            if (data != null && data.length > 0) {
                outputStream.write(data);
            }
        }
    }
}
