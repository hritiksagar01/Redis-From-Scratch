package Components.Server;

import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RedisConfig {
    @Getter
    private String role;
    @Getter
    private int port;
    @Getter
    private String masterHost;
    @Getter
    private int masterPort;
    private String masterReplId = null;
    @Getter
    private long masterReplOffset = 0L;

    public RedisConfig() {
        this.role = "master";
        this.port = 6379;
    }

    public String getMasterReplId() {
        if (masterReplId == null) {
            masterReplId = UUID.randomUUID().toString().replace("-", "") +
                    UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        }
        return masterReplId;
    }

    public void setMasterReplId(String masterReplId) {
        this.masterReplId = masterReplId;
    }

    public void setMasterReplOffset(long masterReplOffset) {
        this.masterReplOffset = masterReplOffset;
    }

    public void setMasterHost(String masterHost) {
        this.masterHost = masterHost;
    }

    public void setMasterPort(int masterPort) {
        this.masterPort = masterPort;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
