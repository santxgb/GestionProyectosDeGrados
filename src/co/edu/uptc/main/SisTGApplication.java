package co.edu.uptc.main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SisTGApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SisTGApplication.class.getResource("/co/edu/uptc/view/fxml/mainView.fxml"));
        Scene scene = new Scene(loader.load());

        stage.setTitle("SisTG UPTC - Inicio JavaFX");
        stage.setMinWidth(760);
        stage.setMinHeight(540);
        stage.setScene(scene);
        stage.show();

        MainFxController controller = loader.getController();
        Platform.runLater(controller::showStartupAlerts);
    }
}
