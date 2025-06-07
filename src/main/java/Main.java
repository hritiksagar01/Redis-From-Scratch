import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 6379;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            while (true) {
                clientSocket = serverSocket.accept();
                Socket finalClientSocket = clientSocket;
                CompletableFuture.runAsync(() -> {
                    try {
                        handleClients(finalClientSocket);
                    } catch (IOException e) {
                        System.out.println("IOException: " + e.getMessage());
                    }
                });
            }


        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }
        public static void handleClients(Socket clientSocket) throws IOException {

            InputStream inputStream = clientSocket.getInputStream();
            OutputStream outputStream = clientSocket.getOutputStream();
            Scanner sc = new Scanner(inputStream, "UTF-8");
            System.out.println("+++++++++++++++++++++++++++++++++========================");
            while(sc.hasNextLine()) {
                String nextLine = sc.nextLine();
                if(nextLine.contains("PING")){
                    outputStream.write("+PONG\r\n".getBytes(StandardCharsets.UTF_8));
                }
            }
            System.out.println("===========================================");
        }

    }

