package administration.server.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Client {

    private String id;
    private String address;
    private int port;

    public Client() {}

    public Client(String id, String address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "{ Id: " + id + ", Address: " + address + ", Port: " + port + " }";
    }
}