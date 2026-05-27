package com.hospital.emergencias.sistemaatencionpacientes;

import com.hospital.emergencias.model.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        DatabaseConnection.inicializarBaseDatos(); // Crea el archivo .db si no existe
    // ... resto de tu código de carga de FXML ...
       // En App.java, línea 12 aproximadamente:
        Parent root = FXMLLoader.load(getClass().getResource("/com/hospital/emergencias/view/MainView.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("Sistema de Emergencias - Control de Pacientes");
        stage.setScene(scene);
        // 'scene'el tipo de color de la interfas
        scene.getRoot().setStyle("-fx-background-color: #388e3c;");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}