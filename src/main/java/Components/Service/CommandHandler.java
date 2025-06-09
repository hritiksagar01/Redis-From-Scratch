package Components.Service;

import Components.Infra.ConnectionPool;
import Components.Infra.Slave;
import Components.Repository.Store;
import Components.Server.RedisConfig;
import Components.Infra.Client;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CommandHandler {
    @Autowired
    public RespSerializer respSerializer;
    @Autowired
    public Store store;
    @Autowired
    public RedisConfig redisConfig;
    @Autowired
    public ConnectionPool connectionPool;


    public String ping(String[] command) {
        return "+PONG\r\n";
    }

    public String echo(String[] command) {
        return respSerializer.serializeBulkString(command[1]);
    }

    public String set(String[] command) {
        try {
            int pxFlag = Arrays.stream(command).toList().indexOf("px");
            if (pxFlag != -1) {
                String key = command[1];
                String value = command[2];
                int delta = Integer.parseInt(command[pxFlag + 1]);
                return store.set(key, value, delta);

            } else {
                String key = command[1];
                String value = command[2];
                return store.set(key, value);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "-1\r\n";
        }
    }

    public String get(String[] command) {
        try {
            String key = command[1];
            return store.get(key);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return "-1\r\n";
        }
    }


    public String info(String[] command){

        int replication = Arrays.stream(command).toList().indexOf("replication");
        if(replication > -1){
            String role = "role:"+redisConfig.getRole();
            String masterReplId = "master_replid:"+redisConfig.getMasterReplId();
            String masterReplOffset = "master_repl_offset:"+redisConfig.getMasterReplOffset();

            String []info = new String[]{role, masterReplId, masterReplOffset};

            String replicationData = String.join("\r\n", info);

            return respSerializer.serializeBulkString(replicationData);
        }
        return "";
    }




    public String replconf(String[] command, Client client) {

        switch (command[1]){
            case "listening-port":
               connectionPool.removeClient(client);
                Slave s = new Slave(client);
                connectionPool.addSlave(s);
                return "+OK\r\n";
            case "capa":
                Slave slave = null;
                for(Slave sd : connectionPool.getSlaves()){
                    if(sd.connection.equals(client)){
                        slave = sd;
                        break;
                    }
                }
                for(int i =0 ; i < command.length; i++){
                    if(command[i].equals("capa")){
                        slave.capabilities.add(command[i + 1]);
                    }
                }
                return "+OK\r\n";

            case "getack":
                // Handle getack command if needed
                break;
            default:
                return "-ERR unknown REPLCONF option\r\n";
        }
        connectionPool.removeClient(client);
        return "+OK\r\n";
    }

    public String psync(String[] command) {
        String replicationIdMaster  = command[1];
        String relicationOffsetMaster = command[2];

        if (replicationIdMaster.equals("?") && relicationOffsetMaster.equals("-1")) {
            // Full synchronization
            String replicationId = redisConfig.getMasterReplId();
            long replicationOffset = redisConfig.getMasterReplOffset();
            String res = "+FULLRESYNC " + replicationId + " " + replicationOffset + "\r\n";;
        return  res;
        }
        else {
            return "-NOMASTERLINK No link to master\r\n";
        }
    }
}
