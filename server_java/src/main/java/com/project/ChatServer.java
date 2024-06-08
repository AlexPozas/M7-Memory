package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

public class ChatServer extends WebSocketServer {
    
    List<String> clientsId = new ArrayList<>();
    List<String> clientsName = new ArrayList<>();

    static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    public ChatServer (int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onStart() {
        // Quan el servidor s'inicia
        String host = getAddress().getAddress().getHostAddress();
        int port = getAddress().getPort();
        System.out.println("WebSockets server running at: ws://" + host + ":" + port);
        System.out.println("Type 'exit' to stop and exit server.");
        setConnectionLostTimeout(0);
        setConnectionLostTimeout(100);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        // Quan un client es connecta
        System.out.println("Client connected");
        String clientId = getConnectionId(conn);
        clientsId.add(clientId);
    }

    public void createGame() {
        //Quan son 2 ja poden començar la partida
        if (clientsId.size() == 2 && clientsName.size() == 2) {
            //Lista de cartas
            List<Integer> cards = randomCards(8);

            //Mandar missatge al primer client
            JSONObject playersId = new JSONObject("{}");
            playersId.put("type", "id");
            playersId.put("can", true);
            playersId.put("me", clientsId.get(0));
            playersId.put("enemy", clientsId.get(1));
            playersId.put("enemyName", clientsName.get(1));
            playersId.put("cards", cards);
            playersId.put("torn", 0);
            playersId.put("waiting", 1);

            WebSocket ws = getClientById(clientsId.get(0));
            ws.send(playersId.toString());

            //Mandar missatge al segon client
            JSONObject playersId2 = new JSONObject("{}");
            playersId2.put("type", "id");
            playersId2.put("can", false);
            playersId2.put("me", clientsId.get(1));
            playersId2.put("enemy", clientsId.get(0));
            playersId2.put("enemyName", clientsName.get(0));
            playersId2.put("cards", cards);
            playersId2.put("torn", 1);
            playersId2.put("waiting", 0);

            WebSocket ws2 = getClientById(clientsId.get(1));
            ws2.send(playersId2.toString());
            clientsId.clear();
            clientsName.clear();
        }
    }

    //Generar les cartes
    public static List<Integer> randomCards(int numCards){
        List<Integer> numList = new ArrayList<Integer>();
        List<Integer> result = new ArrayList<Integer>();
        Random r = new Random();
        //Numeros
        for (int i = 0; i<numCards; i++) {
            for (int v = 0; v<2; v++) {
                numList.add(i);
            }
        }
        //RandomizarLista
        int mida = numList.size()-1;
        for (int i = 0; i <= mida; i++) {
            int num = r.nextInt(numList.size());
            result.add(numList.get(num));
            numList.remove(num);
        }
        return result;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        // Quan un client es desconnecta
        String clientId = getConnectionId(conn);

        // Informem a tothom que el client s'ha desconnectat
        JSONObject objCln = new JSONObject("{}");
        objCln.put("type", "disconnected");
        objCln.put("from", "server");
        objCln.put("id", clientId);
        broadcast(objCln.toString());

        // Mostrem per pantalla (servidor) la desconnexió
        System.out.println("Client disconnected '" + clientId + "'");
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // Quan arriba un missatge
        String clientId = getConnectionId(conn);
        try {
            JSONObject objRequest = new JSONObject(message);
            String type = objRequest.getString("type");

            if (type.equals("name")) {
                clientsName.add(objRequest.getString("value"));
                createGame();

            } else if (type.equals("torn")){
                WebSocket wb = getClientById(objRequest.getString("enemyId"));
                JSONObject ms = new JSONObject("{}");
                ms.put("type", "torn");
                ms.put("value", objRequest.getBoolean("value"));
                wb.send(ms.toString());

            } else if (type.equals("move")) {
                WebSocket wb = getClientById(objRequest.getString("enemyId"));
                JSONObject ms = new JSONObject(message);
                ms.put("type", "move");
                ms.put("value", objRequest.getInt("value"));
                wb.send(message);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Quan hi ha un error
        ex.printStackTrace();
    }

    public void runServerBucle () {
        boolean running = true;
        try {
            System.out.println("Starting server");
            start();
            while (running) {
                String line;
                line = in.readLine();
                if (line.equals("exit")) {
                    running = false;
                }
            } 
            System.out.println("Stopping server");
            stop(1000);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }  
    }

    public void sendList (WebSocket conn) {
        JSONObject objResponse = new JSONObject("{}");
        objResponse.put("type", "list");
        objResponse.put("from", "server");
        objResponse.put("list", getClients());
        conn.send(objResponse.toString()); 
    }

    public String getConnectionId (WebSocket connection) {
        String name = connection.toString();
        return name.replaceAll("org.java_websocket.WebSocketImpl@", "").substring(0, 3);
    }

    public String[] getClients () {
        int length = getConnections().size();
        String[] clients = new String[length];
        int cnt = 0;

        for (WebSocket ws : getConnections()) {
            clients[cnt] = getConnectionId(ws);               
            cnt++;
        }
        return clients;
    }

    public WebSocket getClientById (String clientId) {
        for (WebSocket ws : getConnections()) {
            String wsId = getConnectionId(ws);
            if (clientId.compareTo(wsId) == 0) {
                return ws;
            }               
        }
        
        return null;
    }
}