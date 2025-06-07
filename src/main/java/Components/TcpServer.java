package Components;

import Components.RespSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

@Component
public class TcpServer {
    private RespSerializer respSerializer;
    @Autowired
    private CommandHandler commandHandler;

    public void startServer() {
        int port = 6379;
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            int id = 0;

            while (true) {
                clientSocket = serverSocket.accept();
                id++;
                Socket finalClientSocket = clientSocket;
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                Client client = new Client(finalClientSocket, inputStream, outputStream, id);
                CompletableFuture.runAsync(() -> {
                    try {
                        handleClients(client);
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

    private void handleClients(Client client) throws IOException {

        while (client.socket.isConnected()) {
            byte[] buffer = new byte[client.socket.getReceiveBufferSize()];
            int bytesRead = client.inputStream.read(buffer);
            if (bytesRead > 0) {
                List<String[]> commands = respSerializer.deseralize(buffer);
                for (String[] command : commands) {
                    handleCommand(command, client);
                }

            }
        }
        Scanner sc = new Scanner(client.inputStream);
        while (sc.hasNextLine()) {
            String nextLine = sc.nextLine();
            if (nextLine.contains("PING")) {
                client.outputStream.write("+PONG \r\n".getBytes());
            }
            if (sc.nextLine().contains("ECHO")) {
                String respHeader = sc.nextLine();
                String respBody = sc.nextLine();
                String response = respHeader + "\r\n" + respBody + "\r\n";
                client.outputStream.write(response.getBytes());
            }
        }
    }

    private void handleCommand(String[] command, Client client) throws IOException {
        String res = "";
        switch (command[0]) {
            case "PING":
                res = commandHandler.ping(command);
                break;
            case "ECHO":
                res = commandHandler.echo(command);
                break;
            case "SET":
                res = commandHandler.set(command);
                break;
            case "REPLCONF":
                res = commandHandler.replconf(command, client);
                break;
            case "GET":
                res = commandHandler.get(command);
                break;
        }

                if (res != null && res.equals(" ")) {
                    client.outputStream.write(res.getBytes());
                }
        }
    }

