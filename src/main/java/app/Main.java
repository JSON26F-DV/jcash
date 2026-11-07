package app;

import app.controller.common.AppRoutes;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        stage.setTitle("JCash");
        AppRoutes.init(stage);
        AppRoutes.landing();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
