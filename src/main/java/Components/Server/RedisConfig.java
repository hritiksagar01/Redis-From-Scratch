package Components.Server;

import org.springframework.stereotype.Component;

@Component
public class RedisConfig {
    public String role;
    public int  port;
 public   String masterHost;
  public  int masterPort;

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
