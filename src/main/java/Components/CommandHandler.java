package Components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;


public class CommandHandler {
    @Autowired
    public Components.Service.RespSerializer respSerializer;
    @Autowired
    public Store store;
    @Autowired
//    public RedisConfig redisConfig;
//    @Autowired
//    public ConnectionPool connectionPool;


    public String ping(String[] command) {
        return "+PONG\r\n";
    }

    public String echo(String[] command) {

        return respSerializer.serializeBulkString(command[1]);

    }
    public String set(String[] command) {
        try{
            String key = command[1];
            String value = command[2];
            int pxFlag = Arrays.stream(command).toList().indexOf("px");
            if(pxFlag != -1) {
                int delta = Integer.parseInt(command[pxFlag + 1]);
                return store.set(key, value, delta);
            }
            else{
                return store.set(key, value);
            }
        }
        catch (Exception e){
            return "-1\r\n";
        }

    }
    public String get(String[] command) {
        try{
            String key = command[1];
            return store.get(key, command[2]);
        }
        catch (Exception e){
            return "-1\r\n";
        }
    }


//    public String info(String[] command) {
//        command[0] = "info";
//        int replication = Arrays.stream(command).toList().indexOf("replication");
//        if(replication > -1) {
//            String role = "role: " + redisConfig.getRole() + "\r\n";
//            String masterReplId = "role:" + redisConfig.getMasterReplId() + "\r\n";
//            String masterReplOffset = "master_repl_offset:" + redisConfig.getMasterReplOffset() + "\r\n";
//            return respSerializer.serializeBulkString("role:" + redisConfig.getRole());
//        }
//        return  "# Server\r\n" ;
//    }


    public String replconf(String[] command, Client client) {
        return "+OK\r\n";
    }
}
