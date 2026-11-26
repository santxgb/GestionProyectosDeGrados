package co.edu.uptc.main;

import co.edu.uptc.controller.fx.MainFxController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Region;

import java.io.IOException;

public class SisTGApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                SisTGApplication.class.getResource("/co/edu/uptc/view/fxml/mainView.fxml"));
        Region root = loader.load();
        Scene scene = new Scene(root);

        stage.setTitle("SisTG UPTC - Inicio JavaFX");
        stage.setScene(scene);

        Rectangle2D visible = Screen.getPrimary().getVisualBounds();
        double width = Math.min(root.prefWidth(-1) + 18, visible.getWidth() - 32);
        double height = Math.min(root.prefHeight(-1) + 40, visible.getHeight() - 32);
        stage.setMinWidth(Math.min(600, width));
        stage.setMinHeight(Math.min(400, height));
        stage.setWidth(width);
        stage.setHeight(height);
        stage.setX(visible.getMinX() + (visible.getWidth() - width) / 2);
        stage.setY(visible.getMinY() + (visible.getHeight() - height) / 2);
        stage.show();

        MainFxController controller = loader.getController();
        Platform.runLater(controller::showStartupAlerts);
    }
}
