package app.FXparts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Starter extends Application {


    private double xOffset = 0;
    private double yOffset = 0;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNDECORATED);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/trading-panel.fxml"));
        AnchorPane root = fxmlLoader.load();

        // Create custom title bar
        HBox titleBar = new HBox();
        titleBar.setStyle("-fx-background-color: #222B36;");
        titleBar.setMinSize(Region.USE_COMPUTED_SIZE,Region.USE_COMPUTED_SIZE);

        // Add label for the title
        Label titleLabel = new Label("    TradingHelper - based on RSI");
        titleLabel.setStyle("-fx-font-size: 12");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setAlignment(Pos.CENTER_LEFT);
        titleLabel.setPrefSize(486, 28);
        titleLabel.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        titleLabel.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - xOffset);
            primaryStage.setY(event.getScreenY() - yOffset);
        });

        // Create HBox for the title label and another HBox for buttons
        HBox titleBox = new HBox();
        HBox buttonsBox = new HBox();
        titleBox.getChildren().add(titleLabel);
        titleBox.setStyle("-fx-alignment: CENTER_LEFT;");
        buttonsBox.setStyle("-fx-alignment: CENTER_RIGHT;");

        // Add close button
        Button closeButton = new Button("\uD83D\uDDD9");

//        closeButton.setMaxSize(20,20);
        closeButton.setStyle("-fx-background-color: #222B36; -fx-font-size: 12");

        closeButton.setTextFill(Paint.valueOf("#ff8e00"));
        closeButton.setTextAlignment(TextAlignment.JUSTIFY);
        closeButton.setOnAction(event -> primaryStage.close());

        // Add title label and close button to their respective HBox containers
        titleBar.getChildren().addAll(titleBox, buttonsBox);
        buttonsBox.getChildren().add(closeButton);
        titleBar.setAlignment(Pos.CENTER);
        // Set custom title bar as the top of the AnchorPane
        root.getChildren().add(titleBar);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

//    @Override
//    public void start(Stage stage) throws IOException {
//        FXMLLoader fxmlLoader = new FXMLLoader(Starter.class.getResource("/hello-view-updated.fxml"));
//        Scene scene = new Scene(fxmlLoader.load());
//        stage.setTitle("TradingHelper - based on RSI");
//        Image icon = new Image("icon.png");
//        stage.getIcons().add(icon);
//        stage.setScene(scene);
//        stage.show();
//    }

    public static void main(String[] args) {
        launch();
    }
}