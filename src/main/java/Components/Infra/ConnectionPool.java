package Components.Infra;

import Components.Server.SlaveTcpServer;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class ConnectionPool {
    private Set<Client> clients;
    private Set<Slave> slaves;
    public ConnectionPool(Set<Client> clients, Set<Slave> slaves) {
        this.clients = new HashSet<>();
        this.slaves = new HashSet<>();
    }
    public Set<Client> getClients() {
        return clients;
    }

    public Set<Slave> getSlaves() {
        return slaves;
    }
    public void addClient(Client client) {
        if(client != null){
            clients.add(client);
        }
    }
    public void addSlave(Slave slave) {
        if(slave != null){
            slaves.add(slave);
        }
    }
    public void removeClient(Client client) {
        if(client != null){
            clients.remove(client);
        }
    }
    public void removeSlave(Slave slave) {
        if(slave != null){
            slaves.remove(slave);
        }
    }
    public void clearClients() {
        clients.clear();
    }
    public void clearSlaves() {
        slaves.clear();
    }
    public void clear() {
        clearClients();
        clearSlaves();
    }
    public boolean isEmpty() {
        return clients.isEmpty() && slaves.isEmpty();
    }
    public int size() {
        return clients.size() + slaves.size();
    }
    public boolean containsClient(Client client) {
        return clients.contains(client);
    }
    public boolean removeSlave(Client client) {
        Slave slavetoRemove = null;
        for (Slave s : slaves) {
            if (s.connection.equals(client)) {
                slavetoRemove = s;
                break;
            }
        }
        return slaves.remove(slavetoRemove);
    }


}
