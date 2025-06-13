package Components.Infra;

import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ConnectionPool {
    public int bytesSentToSlaves=0;
    public int slavesThatAreCaughtUp =0;
    private Set<Client> clients;
    private Set<Slave> slaves;

    public void slaveAck(int ackResponse){
        if(this.bytesSentToSlaves == ackResponse){
            slavesThatAreCaughtUp++;
        }
        else{
            slavesThatAreCaughtUp = 0;
        }
    }
    public ConnectionPool() {
       clients = new HashSet<>();
        slaves = new HashSet<>();
    }
    public Set<Client> getClients() {
        return clients;
    }
    public Set<Slave> getSlaves() {
        return slaves;
    }
    public void addClient(Client client) {
        if (client != null){
            clients.add(client);
        }
    }
    public void addSlave(Slave slave) {
        if (slave != null){
            slaves.add(slave);
        }
    }
    public boolean removeClient(Client client) {
      return   clients.remove(client);
    }
    public boolean removeSlave(Slave slave) {
       return slaves.remove(slave);
    }
    public boolean removeSlave(Client client){
        Slave slaveToRemove = null;
        for (Slave s : slaves) {
            if (s.connection.equals(client)) {
                slaveToRemove = s;
                break;
            }
        }
          return slaves.remove(slaveToRemove);
    }

}

