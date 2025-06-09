package Components.Server;

import Components.Service.CommandHandler;
import Components.Service.RespSerializer;
import Infra.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
public class SlaveTcpServer {
    @Autowired
    private RespSerializer respSerializer;
    @Autowired
    private CommandHandler commandHandler;
    @Autowired
    RedisConfig redisConfig;

    public void startServer() {

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = redisConfig.getPort();

        try {
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            CompletableFuture<Void> slaveConnectionFuture = CompletableFuture.runAsync(this::initiateSlavery);
            slaveConnectionFuture.thenRun(() -> {
                System.out.println("Replication Compleleted");
            });
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

    private void initiateSlavery() {
        try(Socket master = new Socket(redisConfig.getMasterHost(), redisConfig.getMasterPort())){
            InputStream inputStream = master.getInputStream();
            OutputStream outputStream = master.getOutputStream();
            byte[] inputBuffer = new byte[1024];

            //part 1 of the handshake
            byte[] data = "*1\r\n$4\r\nPING\r\n".getBytes();
            outputStream.write(data);
            int bytesRead = inputStream.read(inputBuffer,0,inputBuffer.length);
            String response = new String(inputBuffer,0,bytesRead, StandardCharsets.UTF_8);


            //part 2 of the handshake
            int lenListeningPort = (redisConfig.getPort()+"").length();
            int listeningPort = redisConfig.getPort();
            String replconf = "*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n$" +
                    (lenListeningPort+"") + "\r\n" + (listeningPort+"") +
                    "\r\n";
            data = replconf.getBytes();
            outputStream.write(data);
            bytesRead = inputStream.read(inputBuffer,0,inputBuffer.length);
            response = new String(inputBuffer,0,bytesRead, StandardCharsets.UTF_8);



        }
        catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    void handleClients(Client client) throws IOException {

        while (client.socket.isConnected()) {
            byte[] buffer = new byte[client.socket.getReceiveBufferSize()];
            int bytesRead = client.inputStream.read(buffer);
            if (bytesRead > 0) {
                byte[] validBuffer = Arrays.copyOfRange(buffer, 0, bytesRead);
                List<String[]> commands = respSerializer.deseralize(validBuffer);

                for (String[] command : commands) {
                    handleCommand(command, client);
                }

            }
        }
//        Scanner sc = new Scanner(client.inputStream);
//        while (sc.hasNextLine()) {
//            String nextLine = sc.nextLine();
//            if (nextLine.contains("PING")) {
//                client.outputStream.write("+PONG \r\n".getBytes());
//            }
//            if (sc.nextLine().contains("ECHO")) {
//                String respHeader = sc.nextLine();
//                String respBody = sc.nextLine();
//                String response = respHeader + "\r\n" + respBody + "\r\n";
//                client.outputStream.write(response.getBytes());
//            }
        // }
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
                res = "READONLY You cant write to a slave replica. \r\n";
                break;
            case "REPLCONF":
                res = commandHandler.replconf(command, client);
                break;
            case "GET":
                res = commandHandler.get(command);
                break;
                case "INFO":
                    res = commandHandler.info(command);
                    break;

        }
        if (res != null && !res.isEmpty()) {
            client.outputStream.write(res.getBytes(StandardCharsets.UTF_8));
            client.outputStream.flush();
        }
    }
    }

