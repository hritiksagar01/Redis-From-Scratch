package Components.Infra;

import Components.Server.ResponseDto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class Client {
    public Socket socket ;
    public InputStream inputStream;
    public OutputStream outputStream;
    private boolean transactionalContext;
    public Queue<String[]> commandQueue;
    public List<String> transactionResponse;

    public boolean isGetTransactionalContext() {
        return transactionalContext;
    }
    public boolean beginTransaction() {
        if(transactionalContext)
            return false;
        transactionalContext = true;
        transactionResponse = new ArrayList<>();
        commandQueue = new LinkedList<>();
        return transactionalContext ;
    }
    public void endTransaction() {
        commandQueue = null;
        transactionalContext = false;
    }
        public int id;

    public Client(Socket socket, InputStream inputStream , OutputStream outputStream, int id) {
        this.socket = socket;
        this.id = id;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.id = this.id;
    }

    public void send(ResponseDto res) throws IOException {
        if (res.response != null && !res.response.isEmpty()) {
            outputStream.write(res.response.getBytes(StandardCharsets.UTF_8));
            if (res.data != null) {
                outputStream.write(res.data);
            }
        }
    }

    public void send(byte[] data) throws IOException {
            if (data != null && data.length > 0) {
                outputStream.write(data);
            }
        }
    public void send(String data) throws IOException {
        if (data != null && !data.isEmpty()) {
            outputStream.write(data.getBytes());
        }
    }
    }
