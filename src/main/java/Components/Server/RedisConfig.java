package Components.Server;

import org.springframework.stereotype.Component;

@Component
public class RedisConfig {
    public String role;

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

    public int  port;
    public RedisConfig() {
        this.role = "master";
        this.port = 6379;
    }

}
