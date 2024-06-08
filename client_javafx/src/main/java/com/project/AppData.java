package com.project;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.scene.paint.Color;

public class AppData {
    Controller0 c0;
    private static final AppData INSTANCE = new AppData();
    private WebSocketClient wSClient;
    private String ip = "localhost";
    private String port = "8888";
    public ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;
    private String name = "Default";
    // Dades partida
    private boolean myTurn; 
    private String enemyId;
    private String enemyName;
    private ArrayList<Integer> cards;
    private int enemyPoints;
    private int userPoints;

    
    public enum ConnectionStatus {
        DISCONNECTED, DISCONNECTING, CONNECTING, CONNECTED
    }
    
    private AppData() {
    }

    public static AppData getInstance() {
        return INSTANCE;
    }

    public void connectToServer() {
        String uri = "ws://" + ip + ":" + port;

        try {
            wSClient = new WebSocketClient(new URI(uri), (Draft) new Draft_6455()) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    connectionStatus = ConnectionStatus.CONNECTING; 
                    System.out.println("Connected to: " + getURI());
                    Platform.runLater(() -> {
                        UtilsViews.setViewAnimating("ViewConnecting");
                    });
                    wSClient.send("{ \"type\": \"name\", \"value\": \"" + name + "\"}");
                }

                @Override
                public void onMessage(String message) {
                    
                    JSONObject data = new JSONObject(message);

                    if (connectionStatus != ConnectionStatus.CONNECTED) {
                        connectionStatus = ConnectionStatus.CONNECTED;
                    }

                    String type = data.getString("type");
                    switch (type) {
                        case "id":
                            try {
                                Thread.sleep(500);
                                Platform.runLater(() -> {
                                    UtilsViews.setViewAnimating("View0");
                                });
                                
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            setMyTurn(data.getBoolean("can"));
                            setEnemyId(data.getString("enemy"));
                            setEnemyName(data.getString("enemyName"));

                            // cards
                            JSONArray cardsArray = data.getJSONArray("cards");
            
                            // Convert JSONArray to ArrayList<String>
                            ArrayList<Integer> cardsList = new ArrayList<>();
                            for (int i = 0; i < cardsArray.length(); i++) {
                                cardsList.add(cardsArray.getInt(i));
                            }

                            setCards(cardsList);

                            System.out.println("-|-|-|-|-|-|-|-|-|-");
                            System.out.println(myTurn);
                            System.out.println(enemyId);
                            System.out.println(enemyName);
                            System.out.println(cards);
                            System.out.println("-|-|-|-|-|-|-|-|-|-");

                            Platform.runLater(() -> {
                                c0.labelUser.setText("You: " + name );
                                c0.labelRival.setText("Rival: " + enemyName);

                                if (myTurn){
                                    c0.labelUser.setTextFill(Color.GREEN);
                                    c0.labelRival.setTextFill(Color.RED);
                                } else {
                                    c0.labelRival.setTextFill(Color.GREEN);
                                    c0.labelUser.setTextFill(Color.RED);
                                }
                            });

                            //  { "type": "id", "can": true, "me": "001", "enemy": "002", "enemyName": PP, "cards": [], "torn": 0, "waiting": 1}
                            break;
                        case "move":
                            int valueCard = data.getInt("value");
                            c0.enemyMove(valueCard);

                            break;
                        case "torn":
                            setMyTurn(data.getBoolean("value"));

                            Platform.runLater(() -> {
                                if (myTurn){
                                    c0.labelUser.setTextFill(Color.GREEN);
                                    c0.labelRival.setTextFill(Color.RED);
                                } else {
                                    c0.labelRival.setTextFill(Color.GREEN);
                                    c0.labelUser.setTextFill(Color.RED);
                                }
                            });
                            
                            break;
                        case "disconnected":
                            System.out.println("L'enemic s'ha desconectat");
                            UtilsViews.setViewAnimating("ViewWinner");
                            connectionStatus = ConnectionStatus.DISCONNECTED;
                            
                            break;
                        default:
                            System.out.println("Message: " + message);
                            break;
                    }
                    if (connectionStatus == ConnectionStatus.CONNECTED) {  }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    connectionStatus = ConnectionStatus.DISCONNECTED;
                    System.out.println("Disconnected from: " + getURI());
                    Platform.runLater(() -> {
                        UtilsViews.setViewAnimating("ViewWinner");
                    });
                }

                @Override
                public void onError(Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            };

                wSClient.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
            System.out.println("Error: " + uri + " no es una dirección URI de WebSocket válida");
        }

    }

    public void sendCardMessage(String msn){
        wSClient.send(msn);
    }

    public void disconnectFromServer() {
        if (wSClient != null && wSClient.isOpen()) {
            connectionStatus = ConnectionStatus.DISCONNECTING;
            wSClient.close();
        }
    }

    public WebSocketClient getConnexion() {
        if (wSClient != null && wSClient.isOpen()) {
            return wSClient;
        }
        return null;
    }

    public String getIp() {
        return ip;
    }

    public String setIp (String ip) {
        return this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public String setPort (String port) {
        return this.port = port;
    }

    public String getName() {
        return name;
    }

    public String setName(String name) {
        return this.name = name;
    }
    
    public boolean getMyTurn(){
        return myTurn;
    }
    public boolean setMyTurn(boolean myTurn){
        return this.myTurn = myTurn;
    }

    public String getEnemyId(){
        return enemyId;
    }
    public String setEnemyId(String enemyId){
        return this.enemyId = enemyId;
    }

    public String getEnemyName(){
        return enemyName;
    }
    public String setEnemyName(String enemyName){
        return this.enemyName = enemyName;
    }

    public List<Integer> getCards(){
        return cards;
    }
    public ArrayList<Integer> setCards(ArrayList<Integer> cardsList){
        return this.cards = cardsList;
    }

    public int getEnemyPoints(){
        return enemyPoints;
    }
    public int setEnemyPoints(int enemyPoints){
        return this.enemyPoints = enemyPoints;
    }

    public int getUserPoints(){
        return userPoints;
    }
    public int setUserPoints(int userPoints){
        return this.userPoints = userPoints;
    }

}
