package org.example.trade.adapter.broker.xtp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("xtp")
@Configuration
@ConfigurationProperties(prefix = "xtp")
public class NodeConfig {

    private String serverIp;

    private int serverPort;

    private String logFolder;

    private short clientId;

    private String username;

    public String username() {
        return username;
    }

    public String serverIp() {
        return serverIp;
    }

    public int serverPort() {
        return serverPort;
    }

    public String logFolder() {
        return logFolder;
    }

    public short clientId() {
        return clientId;
    }

    @Override
    public String toString() {
        return "XTPLoginRequest{" +
            "xtpTradeServerIp='" + serverIp + '\'' +
            ", xtpServerPort=" + serverPort +
            ", logFolder='" + logFolder + '\'' +
            ", clientId=" + clientId +
            '}';
    }

    public String nodeId() {
        return serverIp + ':' + serverPort + '/' + clientId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public void setLogFolder(String logFolder) {
        this.logFolder = logFolder;
    }

    public void setClientId(short clientId) {
        this.clientId = clientId;
    }

}
