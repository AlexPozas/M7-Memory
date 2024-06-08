package com.project;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class Controller0 {

    private AppData appData;

    @FXML
    private GridPane boardPane;
    @FXML
    public Label labelRival, labelUser;

    private ImageView firstCard = null;
    private boolean canSelect = true;

    private List<ImageView> imagesList = new ArrayList();
    private List<String> hiddenList = new ArrayList<>();

    @FXML
    private void sortir(ActionEvent event) {
        UtilsViews.setViewAnimating("ViewLogin");
        appData.disconnectFromServer();
        System.out.println("Salir");
    }

    @FXML
    private void initialize() {
        appData = AppData.getInstance();
        appData.c0 = this;

        hiddenList = generateHiddenList();
        
        int count = 0;
        for (int col = 0; col < 4; col++) {
            for (int row = 0; row < 4; row++) {
                ImageView imageView = new ImageView(getClass().getResource("/images/card.png").toString());
                imageView.setFitWidth(70);
                imageView.setFitHeight(80);
                imageView.setId(Integer.toString(count));
                count++;
                imageView.setOnMouseClicked(event -> cardListener(event));
                imagesList.add(imageView);
                StackPane stackPane = new StackPane(imageView);
                boardPane.add(stackPane, row, col);
            }
        }

        labelRival.setText("Rival: ");
        labelUser.setText("You: ");
    }

    private List<String> generateHiddenList() {
        List<String> list = new ArrayList<>();
        for (int i=0;i<16;i++) {
            list.add("hidden");
        } 
        return list;
    }

    public void cardListener(MouseEvent event) {
        if (!canSelect || !appData.getMyTurn()){
            return;
        } 
        
        ImageView clickedCard = (ImageView) event.getSource();
        int clickedCardID = Integer.valueOf(clickedCard.getId());

        if (hiddenList.get(clickedCardID).equals("hidden")) {
            Image image = new Image(getClass().getResource("/images/card"+appData.getCards().get(clickedCardID)+".png").toString());
            clickedCard.setImage(image);

            hiddenList.set(clickedCardID, appData.getCards().get(clickedCardID).toString());

            appData.sendCardMessage("{ \"type\": \"move\", \"value\":" + clickedCardID + ", \"enemyId\": \"" + appData.getEnemyId() + "\"}");
        } else {
            return;
        }

        if (firstCard == null) {
            firstCard = clickedCard; 
        } else {
            if (appData.getCards().get(Integer.parseInt(firstCard.getId())).equals(appData.getCards().get(Integer.parseInt(clickedCard.getId())))) {
                // Las cartas son iguales
                System.out.println("Emparejadas!");
                appData.setUserPoints(appData.getUserPoints()+1);

                Platform.runLater(() -> {
                    labelRival.setText("Rival: "+ appData.getEnemyName() + " Points: " + appData.getEnemyPoints());
                    labelUser.setText("You: " + appData.getName() + " Points: " + appData.getUserPoints());
                });
                

                firstCard = null;
            } else {
                // Las cartas no son iguales
                System.out.println("No emparejadas!");
                canSelect = false; 

                hiddenList.set(clickedCardID, "hidden");
                hiddenList.set(Integer.valueOf(firstCard.getId()), "hidden");

                new Thread(() -> {
                    try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); } 

                    Image img = new Image(getClass().getResource("/images/card.png").toString());
                    firstCard.setImage(img);

                    clickedCard.setImage(img);

                    firstCard = null;
                    canSelect = true; 
                }).start();

                appData.setMyTurn(false);
                Platform.runLater(() -> {
                    if (appData.getMyTurn()){
                        labelUser.setTextFill(Color.GREEN);
                        labelRival.setTextFill(Color.RED);
                    } else {
                        labelRival.setTextFill(Color.GREEN);
                        labelUser.setTextFill(Color.RED);
                    }
                });
                appData.getConnexion().send("{ \"type\": \"torn\", \"value\": true, \"enemyId\": \"" + appData.getEnemyId() + "\"}");
            }
        }
        checkWinner();
    }

    public void enemyMove(int valueMove) {
        
        ImageView clickedCard = (ImageView) imagesList.get(valueMove);
        int clickedCardID = Integer.valueOf(clickedCard.getId());

        if (hiddenList.get(clickedCardID).equals("hidden")) {
            
            Image image = new Image(getClass().getResource("/images/card"+appData.getCards().get(clickedCardID)+".png").toString());
            clickedCard.setImage(image);

            hiddenList.set(clickedCardID, appData.getCards().get(clickedCardID).toString());

        } else {
            return;
        }

        if (firstCard == null) {
            firstCard = clickedCard; 
            
        } else {
            if (appData.getCards().get(Integer.parseInt(firstCard.getId())).equals(appData.getCards().get(Integer.parseInt(clickedCard.getId())))) {
                // Las cartas son iguales
                System.out.println("Emparejadas!");

                appData.setEnemyPoints(appData.getEnemyPoints()+1);

                Platform.runLater(() -> {
                    labelRival.setText("Rival: "+ appData.getEnemyName() + " Points: " + appData.getEnemyPoints());
                    labelUser.setText("You: " + appData.getName() + " Points: " + appData.getUserPoints());
                });

                firstCard = null;
            } else {
                // Las cartas no son iguales
                System.out.println("No emparejadas!");
                canSelect = false; 

                hiddenList.set(clickedCardID, "hidden");
                hiddenList.set(Integer.valueOf(firstCard.getId()), "hidden");

                new Thread(() -> {
                    try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); } 

                    Image img = new Image(getClass().getResource("/images/card.png").toString());
                    firstCard.setImage(img);

                    clickedCard.setImage(img);

                    firstCard = null;
                    canSelect = true; 
                }).start();

                appData.getConnexion().send("{ \"type\": \"torn\", \"value\": true, \"enemyId\": \"" + appData.getEnemyId() + "\"}");
            }
        }
        checkWinner();
    }

    private void checkWinner() {
        
        if (appData.getEnemyPoints()+appData.getUserPoints()==8) {
            Platform.runLater(() -> {
                UtilsViews.setViewAnimating("ViewWinner");
            });
            appData.getConnexion().close();
        }

    }
}
