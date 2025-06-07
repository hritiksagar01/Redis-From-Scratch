package Components.Infra;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Component
public class Client {
    public Socket socket ;
    public InputStream inputStream;
    public OutputStream outputStream;
    public int id;

    public Client(Socket socket,InputStream inputStream ,OutputStream outputStream,    int id) {
        this.socket = socket;
        this.id = id;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.id = id;
    }

}
