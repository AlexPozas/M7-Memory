package com.project;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class ControllerWinner {
    
    private AppData appData;

    @FXML
    public Label winnerMessage, resultMessage;
    @FXML
    public Button buttonResult, buttonPlayAgain;

    @FXML
    private void initialize(){
        appData = AppData.getInstance();

        winnerMessage.setText("AND THE WINNER IS ...");
        winnerMessage.setAlignment(Pos.CENTER);

        resultMessage.setText(" ");
        resultMessage.setAlignment(Pos.CENTER);

        buttonPlayAgain.setVisible(false);
    }
    @FXML
    private void showResult() {
        buttonResult.setVisible(false);
        buttonPlayAgain.setVisible(true);

        Platform.runLater(() -> {
            if (appData.getUserPoints() == appData.getEnemyPoints()) {
                winnerMessage.setText("THERE WAS A TIE AGAINST "+appData.getEnemyName().toUpperCase());
                winnerMessage.setTextFill(Color.GREEN);
            } else if (appData.getUserPoints() > appData.getEnemyPoints()) {
                winnerMessage.setText("YOU HAVE WON AGAINST "+appData.getEnemyName().toUpperCase());
                winnerMessage.setTextFill(Color.GREEN);
            } else {
                winnerMessage.setText("YOU HAVE LOST AGAINST "+appData.getEnemyName().toUpperCase());
                winnerMessage.setTextFill(Color.RED);
            }
            resultMessage.setText(appData.getUserPoints() + " - " + appData.getEnemyPoints());
            resultMessage.setTextFill(Color.BLUE);
        });
    }
    @FXML
    private void playAgain(){
        Platform.runLater(() -> {
            UtilsViews.setViewAnimating("ViewLogin");
        });
    }
}
