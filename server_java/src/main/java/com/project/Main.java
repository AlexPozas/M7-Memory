package com.project;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

// Tutorials: http://tootallnate.github.io/Java-WebSocket/

/*
    WebSockets server, example of messages:

    From client to server:
        - Player name           { "type": "name", "value": "Po"}
        - Change Round          { "type": "torn", "value": true, "enemyId": "001"}
        - Player move           { "type": "move", "value": 5, "enemyId": "002"}


    From server to client:
        - On find match         { "type": "id", "can": true, "me": "001", "enemy": "002", "enemyName": PP, "cards": [], "torn": 0, "waiting": 1}
        - Change Round          { "type": "torn", "value": true}
        - Player move           { "type": "move", "value": 5}

 */

public class Main {


    public static void main (String[] args) throws InterruptedException, IOException {

        int port = 8888; 
        String localIp = getLocalIPAddress();
        System.out.println("Local server IP: " + localIp);

        // Deshabilitar SSLv3 per clients Android
        java.lang.System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2");

        ChatServer server = new ChatServer(port);
        server.runServerBucle();
    }

    public static String getLocalIPAddress() throws SocketException, UnknownHostException {
        String localIp = "";
        Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface ni = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = ni.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress ia = inetAddresses.nextElement();
                if (!ia.isLinkLocalAddress() && !ia.isLoopbackAddress() && ia.isSiteLocalAddress()) {
                    System.out.println(ni.getDisplayName() + ": " + ia.getHostAddress());
                    localIp = ia.getHostAddress();
                    // Si hi ha múltiples direccions IP, es queda amb la última
                }
            }
        }

        // Si no troba cap direcció IP torna la loopback
        if (localIp.compareToIgnoreCase("") == 0) {
            localIp = InetAddress.getLocalHost().getHostAddress();
        }
        return localIp;
    }
}