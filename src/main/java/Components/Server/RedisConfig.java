package Components.Server;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

@Configuration
@Getter
@Setter
public class RedisConfig {
    private String role;
    private int port;
    private String masterHost;
    private int masterPort;
    private String masterReplId;

    public String getMasterReplId() {
      if(masterReplId == null) {
         masterReplId = UUID.randomUUID().toString().replace("-", "")+
                 UUID.randomUUID().toString().replace("-", "").substring(0, 8);
      }
    }

    public void setMasterReplId(String masterReplId) {
        this.masterReplId = masterReplId;
    }

    public Long getMasterReplOffset() {
        if(masterReplOffset == null) {
            masterReplOffset = 0L;
        }
        return masterReplOffset;
    }

    public void setMasterReplOffset(Long masterReplOffset) {
        this.masterReplOffset = masterReplOffset;
    }

    private Long masterReplOffset;

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



 /*   public RedisConfig (int port) {
        this.port = port;
        this.role = "master";
    }
    public RedisConfig (int port, String role) {
        this.port = port;
        this.role =role;
    }*/

}
