import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.io.InputStream;
import java.util.*;
import java.net.Socket;

public class Main {
    public static void main(String[] args){

        System.out.println("Logs from your program will appear here!");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try {

            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            while(true){
                clientSocket = serverSocket.accept();
                Socket finalClientSocket = clientSocket;

            }
            clientSocket = serverSocket.accept();

        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
    public void handleClients (Socket clientSocket) {

        InputStream inputStream = clientSocket.getInputStream();
        OutputStream outputStream = clientSocket.getOutputStream();
        Scanner sc = new Scanner(inputStream);
        outputStream.write("+PONG \r\n".getBytes());
    }

}
