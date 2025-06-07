
//import Components.Server.RedisConfig;
//import Components.Server.SlaveTcpServer;
//import Config.AppConfig;
//import Components.Server.MasterTcpServer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    public static void main(String[] args) {
        System.out.println("Logs from your program will appear here!");
        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        int port = 6379;
        try{
            serverSocket = new ServerSocket(port);
            serverSocket.setReuseAddress(true);
            clientSocket = serverSocket.accept();
            OutputStream outputStream = clientSocket.getOutputStream();
             outputStream.write("+PONG\r\n".getBytes());
        } catch (Exception e) {
            System.out.println("Error creating server socket: " + e.getMessage());
            return;
        }
//        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);

//        MasterTcpServer master = context.getBean(MasterTcpServer.class);
//        SlaveTcpServer slave = context.getBean(SlaveTcpServer.class);
//
//        RedisConfig redisConfig = context.getBean(RedisConfig.class);
//        int port= 6379;
//        redisConfig.setPort(port);;
//
//        redisConfig.setRole("master");
//        for(int i =0 ; i< args.length; i++){
//           switch (args[i]){
//               case "--port":
//                   port = Integer.parseInt(args[i+1]);
//                   redisConfig.setPort(port);
//                   break;
//                   case "--replicaof":
//                       redisConfig.setRole("slave");
//                       String masterHost = args[i+1].split(" ")[0];
//                          int masterPort = Integer.parseInt(args[i+1].split(" ")[1]);
//                          redisConfig.setMasterHost(masterHost);
//                            redisConfig.setMasterPort(masterPort);
//                          break;
//           }
//        }
//        redisConfig.setPort(port);
//        redisConfig.setRole("master");
//        if(redisConfig.getRole().equals("slave")) {
//            slave.startServer();
//        } else {
//            master.startServer(6379);
//        }
//        master.startServer(6379);
    }
}



