package app.FXparts;

import app.TradingAppCore.*;
import javafx.scene.control.*;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class JavaFXController implements Initializable {
    private IndicatorsService indicator;
    private User user;
    private Controller controller;
    private PositionInfo info;
    private Timeline timeline, buyTimeoutTimer, sellTimeoutTimer, progressTime, pauseOpenings, pauseClosings;
    private String indTFParam, period;
    private double buyTimeoutProgress, sellTimeoutProgress, timeoutChange, progress;
    private int positionOpen, minutesTimeout, positionClose, partlyCloseValue;
    private boolean notPausedOpenings = true;
    private boolean notPausedClosings = true;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");


    @FXML
    private Label counter, userInfoLabel, openings, lastTrade, userInfoLabel1;
    @FXML
    private CheckBox profit;
    @FXML
    private TextField openLevel, closeLevel, quantity;
    @FXML
    private ProgressBar progressBar, buyTimeoutBar, sellTimeoutBar;
    @FXML
    private ChoiceBox<String> indTF, indPeriodBox, tradingSide,
            tradingTickers, orderTimeout, partlyClose = new ChoiceBox<>();
    private final String[] partlyCloseVariants = {"1", "2", "3", "4", "5"};
    private final String[] bitmexTickers = {"ETHUSDT"}; //"XBTUSD",
    private final String[] orderSide = {"Buy", "Sell"};
    private final Map<String, String> oppositeSide = Map.of("Buy", "Sell", "Sell", "Buy");
    private final String[] indicatorTimeframe = {"1m", "5m", "15m", "30m", "1h", "4h"};
    private final String[] indicatorPeriods = {"3", "4", "5", "6", "7", "8"};  //, "9", "10", "11", "12", "13", "14", "15"
    private final String[] orderTimeoutArray = {"1", "5", "10", "15", "30", "60"};

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        indicator = new IndicatorsService();
        user = new User();
        controller = new Controller();
        info = new PositionInfo();
        controller.setUser(user);
        info.setUser(user);

        indTF.getItems().addAll(indicatorTimeframe);
        indTF.setOnAction(this::selectIndTF);

        indPeriodBox.getItems().addAll(indicatorPeriods);
        indPeriodBox.setOnAction(this::savePeriod);

        tradingTickers.getItems().addAll(bitmexTickers);
        tradingTickers.setOnAction(this::saveOrderTicker);

        tradingSide.getItems().addAll(orderSide);
        tradingSide.setOnAction(this::saveOrderSide);

        orderTimeout.getItems().addAll(orderTimeoutArray);
        orderTimeout.setOnAction(this::saveTimeout);

        partlyClose.getItems().addAll(partlyCloseVariants);
        partlyClose.setOnAction(this::savePartlyClose);

//        counter.setText("go?");

    }

    public void initializeValues() {
        readFile();
        HttpResponse<String> response = controller.getUserAccountInfo();
        String id = KeyResponseParser.parseInfo(response, "id");
        if (!id.equals("ERROR")) {
            userInfoLabel.setText("");
            userInfoLabel1.setText("User id: " + id);
        }
        positionOpen = Integer.parseInt(openLevel.getText());
        positionClose = Integer.parseInt(closeLevel.getText());
        controller.setQuantity(Integer.parseInt(quantity.getText()));
    }

    public void onStartButton(ActionEvent e) {
//        counter.setText("started");
        final int[] indValue = new int[1];
        indicator.setPeriod(period);
        indicator.setSymbol(controller.getSymbol());
        indicator.setInterval(indTFParam);

        //Прогресс Бар FX на индикатор
        progressTime = new Timeline(new KeyFrame(Duration.seconds(1), actionEvent -> progressBar.setProgress(progress += 0.0625)));
        progressTime.setCycleCount(Animation.INDEFINITE);
        progressTime.play();

        //Прогресс Бар FX pause BUY
        timeoutChange = 100.0 / (minutesTimeout * 60 / 16.0) / 100;
        buyTimeoutTimer = new Timeline(new KeyFrame(Duration.seconds(16), actionEvent -> buyTimeoutBar.setProgress(buyTimeoutProgress += timeoutChange)));
        buyTimeoutTimer.setCycleCount(Animation.INDEFINITE);

        //Timer for openings timeout BUY
        pauseOpenings = new Timeline(new KeyFrame(Duration.minutes(minutesTimeout), actionEvent -> {
            pauseOpenings.stop();
            notPausedOpenings = true;
        }));
        pauseOpenings.setCycleCount(Animation.INDEFINITE);


        //Прогресс Бар FX pause sell
        sellTimeoutTimer = new Timeline(new KeyFrame(Duration.seconds(16), actionEvent -> sellTimeoutBar.setProgress(sellTimeoutProgress += timeoutChange)));
        sellTimeoutTimer.setCycleCount(Animation.INDEFINITE);

        //Timer for openings timeout sell
        pauseClosings = new Timeline(new KeyFrame(Duration.minutes(minutesTimeout), actionEvent -> {
            pauseClosings.stop();
            notPausedClosings = true;
        }));
        pauseClosings.setCycleCount(Animation.INDEFINITE);


        //тут логика покупки и продажи
        timeline = new Timeline(new KeyFrame(Duration.seconds(16), event -> {
            checkAndSetTimeout();
            progressBar.setProgress(progress = 0.0625);
            indValue[0] = indicator.reqRSI();
            counter.setText(String.valueOf(indValue[0]));
            switch (controller.getSide()) {
                case "Buy":
                    if (indValue[0] <= positionOpen) {
                        openPositionAndPause();
                    } else if (indValue[0] >= positionClose) {
                        closePositionIfOpened();
                    }
                    break;
                case "Sell":
                    if (indValue[0] >= positionOpen) {
                        openPositionAndPause();
                    } else if (indValue[0] <= positionClose) {
                        closePositionIfOpened();
                    }
                    break;
            }
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void checkAndSetTimeout() {
        if (notPausedOpenings) {
            buyTimeoutTimer.stop();
            buyTimeoutBar.setProgress(buyTimeoutProgress = 0);
        }
        if (notPausedClosings) {
            sellTimeoutTimer.stop();
            sellTimeoutBar.setProgress(sellTimeoutProgress = 0);
        }
    }

    private void closePositionIfOpened() {
        int comm = Integer.parseInt(info.getPosCommByTicker());
        if (comm != 0) {
            if (profit.isSelected()) {
                if (Integer.parseInt(info.getCurrentPnlByTicker()) >= comm) {
                    closeAndPause();
                }
            } else {
                closeAndPause();
            }
        }
    }

    private void closeAndPause() {
        if (notPausedClosings) {
            int part = controller.getBuysCount() / partlyCloseValue;
            if (part != 0) {
                manualClose(part);
                notPausedClosings = false;
                pauseClosings.play();
                sellTimeoutTimer.play();
                sellTimeoutBar.setProgress(timeoutChange);
            }
        }
    }

    private void openPositionAndPause() {
        if (notPausedOpenings) {
            manualOpen();
            notPausedOpenings = false;
            pauseOpenings.play();
            buyTimeoutTimer.play();
            buyTimeoutBar.setProgress(timeoutChange);
        }
    }

    public void onStopButton(ActionEvent e) {
        timeline.stop();
        progressTime.stop();
        buyTimeoutTimer.stop();
        sellTimeoutTimer.stop();
        counter.setText("---");
    }

    public void manualOpen() {
        controller.createMarketOrder();
        controller.setBuysCount(controller.getBuysCount() + 1);
        changeOpeningsLabel();
        LocalDateTime time = LocalDateTime.now();
        lastTrade.setText(timeFormatter.format(time) + " " + controller.getSide());
    }

    private void changeOpeningsLabel() {
        int buysCount = controller.getBuysCount();
        if (buysCount != 0) {
            openings.setText(String.valueOf(buysCount));
        } else {
            openings.setText("---");
        }
    }

    public void manualClosePart() {
        int part = controller.getBuysCount() / partlyCloseValue;
        manualClose(part);
    }

    public void manualClose(int part) {
        controller.createPartMarketOrder(oppositeSide.get(controller.getSide()), part * controller.getQuantity());
        controller.setBuysCount(controller.getBuysCount() - part);
        changeOpeningsLabel();
        LocalDateTime time = LocalDateTime.now();
        lastTrade.setText(timeFormatter.format(time) + " " + oppositeSide.get(controller.getSide()));
    }

    public void manualClose() {
        controller.closePositionByTicker();
        controller.setBuysCount(0);
        changeOpeningsLabel();
        LocalDateTime time = LocalDateTime.now();
        lastTrade.setText(timeFormatter.format(time) + " " + oppositeSide.get(controller.getSide()));    }

    public void saveTimeout(ActionEvent e) {
        minutesTimeout = Integer.parseInt(orderTimeout.getValue());
    }

    public void savePartlyClose(ActionEvent e) {
        partlyCloseValue = Integer.parseInt(partlyClose.getValue());
    }

    public void saveOrderSide(ActionEvent e) {
        String value = tradingSide.getValue();
        controller.setSide(value);
    }

    public void saveOrderTicker(ActionEvent e) {
        String value = tradingTickers.getValue();
        info.setSymbol(value);
        controller.setSymbol(value);
    }

    public void selectIndTF(ActionEvent e) {
        indTFParam = indTF.getValue();
    }

    public void savePeriod(ActionEvent e) {
        period = indPeriodBox.getValue();
    }

    public void readFile() {
        ArrayList<String> input = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("input.txt"))) {
            while (reader.ready()) {
                input.add(reader.readLine());
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
//        input.forEach(System.out::println);
        user.setKey(input.get(0));
        user.setSecret(input.get(1));
        indicator.setSecret(input.get(2));


    }
}