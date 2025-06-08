import Components.TcpServer;
import org.springframework.boot.autoconfigure.web.reactive.HttpHandlerAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    public static void main(String[] args) throws IOException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        TcpServer app = context.getBean(TcpServer.class);
        int port =6379;
        for(int i = 0; i < args.length; i++) {
           if(args[i].equals("--port")) {
                port = Integer.parseInt(args[i + 1]);
               i++;

            }
        }
        app.startServer(port);

    }
}




