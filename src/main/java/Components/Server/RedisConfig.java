package Components.Server;

import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RedisConfig {
    public String role;
    public int  port;
 public   String masterHost;
  public  int masterPort;
  private String masterReplId = null;

    public String getMasterReplId() {
        if (masterReplId == null) {
            masterReplId = UUID.randomUUID().toString().replace("-", "")+
                    UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
        return masterReplId;
    }

    public void setMasterReplId(String masterReplId) {
        this.masterReplId = masterReplId;
    }

    public long getMasterReplOffset() {
        if (masterReplOffset == Long.parseLong(null)) {
            masterReplOffset = 0L;
        }
        return masterReplOffset;
    }

    public void setMasterReplOffset(long masterReplOffset) {
        this.masterReplOffset = masterReplOffset;
    }

    private long masterReplOffset = Long.parseLong(null);


    public String getMasterHost() {
        return masterHost;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public int getMasterPort() {
        return masterPort;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }


    public RedisConfig() {
        this.role = "master";
        this.port = 6379;
    }

}
