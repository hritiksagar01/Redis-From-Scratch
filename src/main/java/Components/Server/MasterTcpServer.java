package Components.Server;

import Components.Infra.ConnectionPool;
import Components.Infra.Slave;
import Components.Service.RespSerializer;
import Components.Service.CommandHandler;
import Components.Infra.Client;
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
public class MasterTcpServer {
    @Autowired
    private RespSerializer respSerializer;
    @Autowired
    private CommandHandler commandHandler;
    @Autowired
    RedisConfig redisConfig;
    @Autowired
    ConnectionPool connectionPool;

    public void startServer() {

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = redisConfig.getPort();

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

    void handleClients(Client client) throws IOException {
        connectionPool.addClient(client);
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
        connectionPool.removeClient(client);
        connectionPool.removeSlave(client);
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
        byte[] data = null;
        switch (command[0]) {
            case "PING":
                res = commandHandler.ping(command);
                break;
            case "WAIT":
                if(connectionPool.bytesSentToSlaves == 0){
                    res = respSerializer.respInteger(connectionPool.slavesThatAreCaughtUp);
                    break;
                }
            case "ECHO":
                res = commandHandler.echo(command);
                break;
            case "SET":
                res = commandHandler.set(command);
                CompletableFuture.runAsync(()->propogate(command));
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
            case "RIYA":
                res = "HI RIYA";
                break;
            case "PSYNC":
             ResponseDto   resDto = commandHandler.psync(command);
               res = resDto.response;
                data = resDto.data;
                break;
        }
        client.send(res ,data);


    }

    private void propogate(String[] command) {
        String commandRespString = respSerializer.respArray(command);
      try {
          for(Slave slave : connectionPool.getSlaves()){
              slave.send(commandRespString.getBytes());
          }
      }
      catch (IOException e){
          throw new RuntimeException(e);
      }
    }
}

