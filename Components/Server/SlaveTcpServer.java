package Components.Server;

import Components.Service.CommandHandler;
import Components.Service.RespSerializer;
import Components.Infra.Client;
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
public class SlaveTcpServer {
    @Autowired
private RespSerializer respSerializer;
    @Autowired
    private CommandHandler commandHandler;
    @Autowired
    private RedisConfig redisConfig;
    public void startServer() {

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = redisConfig.getPort();

        try {

            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);

            CompletableFuture slaveConnectionFuture = CompletableFuture.runAsync(this::initiateSlavery);
            slaveConnectionFuture.thenRun(()->System.out.println("Replication compmleleted"));

            int id =0;
            while (true) {
                clientSocket = serverSocket.accept();
                id++;
                Socket finalClientSocket = clientSocket;
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                Client client = new Client(finalClientSocket ,inputStream, outputStream, id);
                CompletableFuture.runAsync(() -> {
                    try {
                        handleClient(client);
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
            } catch (IOException e) {
                System.out.println("IOException: " + e.getMessage());
            }
        }
    }

    private void initiateSlavery() {
        try(Socket master = new Socket( redisConfig.getMasterHost() , redisConfig.getPort())) {
            InputStream inputStream = master.getInputStream();
            OutputStream outputStream = master.getOutputStream();
            byte[] inputBuffer = new byte[1024];
            byte[] data = "*1\r\n$4\r\nPING\r\n".getBytes();
            outputStream.write(data);
            int bytesRead = inputStream.read(inputBuffer , 0 , inputBuffer.length);
            String response = new String(inputBuffer, 0, bytesRead , StandardCharsets.UTF_8);

            int lenListeninPort = (redisConfig.getPort()+"").length();
            int listeningPort = redisConfig.getPort();
            String replconf = "*3\r\n$8\r\nREPLCONF\r\n$14\r\nlistening-port\r\n$" +
                    lenListeninPort + "\r\n" + listeningPort + "\r\n";
            data = replconf.getBytes(StandardCharsets.UTF_8);
            outputStream.write(data);
            bytesRead = inputStream.read(inputBuffer , 0 , inputBuffer.length);
            response = new String(inputBuffer, 0, bytesRead , StandardCharsets.UTF_8);

            String psync = "*3\r\n$5\r\nPSYNC\r\n$1\r\n?\r\n$2\r\n-1\r\n" + redisConfig.getMasterReplId() + "\r\n";
            data = replconf.getBytes();
            outputStream.write(data);
            bytesRead = inputStream.read(inputBuffer , 0 , inputBuffer.length);
            response = new String(inputBuffer, 0, bytesRead , StandardCharsets.UTF_8);


            }
         catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());


        }
    }

    public static String encodingRespString(String s) {
        String resp = "$";
        resp += s.length();
        resp += "\r\n";
        resp += s;
        resp += "\r\n";
        return resp;
    }

    private  void handleClient(Client client) throws IOException {

        while(client.socket.isConnected()){
            byte[] buffer = new byte[client.socket.getReceiveBufferSize()];
            int bytesRead = client.inputStream.read(buffer);
            if (bytesRead >0) {
                List<String[]> commands = respSerializer.deseralize(buffer);
                for(String[] command : commands){
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

    private  void handleCommand(String[] command, Client client) throws IOException {
        String res = "" ;
        switch (command[0]) {
            case "PING":
                res = commandHandler.ping(command);
                break;
            case "ECHO":
                res = commandHandler.echo(command);
                break;
            case "SET":
                res = "-READONLY You cant write against a replica\r\n";
                break;
                case "REPLCONF":
                    res = commandHandler.replconf(command , client);
                    break;
                case "GET":
                    res = commandHandler.get(command);
           if(res != null &&res.equals("")){
               client.outputStream.write(res.getBytes());
           }
        }
    }
}

