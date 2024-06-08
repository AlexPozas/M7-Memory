package com.project;

import javafx.fxml.FXML;

public class ControllerConnecting {
    private AppData appData;

    public void initialize(){
        appData = AppData.getInstance();
    }

    @FXML
    void cancelConnection(){
        appData.disconnectFromServer();
    }
}
