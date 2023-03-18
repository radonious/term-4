package com.example.lab1;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static javafx.scene.control.Alert.AlertType;


public class Controller implements Initializable {
    @FXML private Label time;
    @FXML private MediaView mediaView;
    @FXML private CheckBox InfoCheckBox;
    @FXML private Button StartBtn;
    @FXML private Button StopBtn;
    @FXML private RadioButton hideTimeRadio;
    @FXML private RadioButton showTimeRadio;
    @FXML private Pane mainPane;
    @FXML private Pane menuPane;
    @FXML private Pane viewPane;
    @FXML private RadioMenuItem EditHideTime;
    @FXML private RadioMenuItem EditShowTime;
    @FXML private RadioMenuItem EditStart;
    @FXML private RadioMenuItem EditStop;
    @FXML private ComboBox<Integer> comboBoxK;
    @FXML private ComboBox<Float> comboBoxP;
    @FXML private TextField textFieldN1;
    @FXML private TextField textFieldN2;

    private Stage mainStage;
    private Habitat habitat;
    private static int population;
    private static boolean isStarted;
    private static long startTime;
    private static Timer timer;
    private static MediaPlayer player;
    private static final String mp3Path;
    private long simulationTime;

    static {
        mp3Path = "src/main/resources/com/example/lab1/music/song3.mp3";
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    public void setHabitat(Habitat donor) {
        habitat = donor;
    }

    @Override public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> tmp1 = (ObservableList<Integer>) FXCollections.observableArrayList(0,10,20,30,40,50,60,70,80,90,100);
        comboBoxK.setItems(FXCollections.observableList(tmp1));
        comboBoxK.getSelectionModel().select((Integer)30);
        ObservableList<Float> tmp2 = (ObservableList<Float>) FXCollections.observableArrayList(0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f);
        comboBoxP.setItems(FXCollections.observableList(tmp2));
        comboBoxP.getSelectionModel().select(0.8f);
        isStarted = false;
        timer = new Timer();
    }

    @FXML Pane getViewPane() {
        return viewPane;
    }
    @FXML MediaView getmedia() {
        return mediaView;
    }
    @FXML Pane getMenuPane() {
        return menuPane;
    }
    @FXML Label gettime() {
        return time;
    }

    @FXML private void setActionsOnKeys(KeyEvent keyEvent) throws MalformedURLException {
        KeyCode pressedKey = keyEvent.getCode();
        if (pressedKey == KeyCode.B && !isStarted) {
            System.out.println("Pressed B\nSimulation Started");
            viewPane.getChildren().removeIf(node -> (node instanceof ImageView));
            StartSimulation();
        }
        else if (pressedKey == KeyCode.E && isStarted){
            newModalWindow();
            //StopSimulation();
            System.out.println("Pressed E\nSimulation has stopped");
        }
        else if (pressedKey == KeyCode.E){
            viewPane.getChildren().clear();
            System.out.println("Pressed E after E\nSimulation already stopped, but screen cleared again");
        }
        else if (pressedKey == KeyCode.T){
            if (gettime().isVisible()) {
                hideTime();
            }
            else {
                showTime();
            }

        }
    }

    @FXML public void showTime() {
        gettime().setVisible(true);
        showTimeRadio.setSelected(true);
        EditShowTime.setSelected(true);
    }

    @FXML public void closeApp() {
        mainStage.close();
    }

    @FXML public void hideTime() {
        gettime().setVisible(false);
        hideTimeRadio.setSelected(true);
        EditHideTime.setSelected(true);
    }

    @FXML private void mediaStart() throws MalformedURLException {
        File mediaFile = new File(mp3Path);
        Media pick = new Media(mediaFile.toURI().toURL().toString());
        player = new MediaPlayer(pick);
        MediaView mediaView = getmedia();
        player.play();
    }

    @FXML private void settingInit() {
        population = 0;
        startTime = System.currentTimeMillis();
        isStarted = true;
    }

    @FXML private void labelReset() {
        showTime();
        //gettime().setFont(new Font("Arial", 19));
        gettime().setText("Time: 0");
    }

    @FXML private void popAlert(String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML private void checkSettings() {
        int tmpN1;
        if (!textFieldN1.getText().isBlank()) {
            tmpN1 = Integer.parseInt(textFieldN1.getText());
            if (tmpN1 < 0) {
                tmpN1 = -tmpN1;
                popAlert("N1 is less than zero\nN1 set to abs(N1)");
            }
        } else {
            tmpN1 = habitat.getN1();
            popAlert("Blank string for N1\nN1 set as it was");
        }
        // можно вынести в одну функцию, но надо передавать функции как параметры, лень
        int tmpN2;
        if (!textFieldN2.getText().isBlank()) {
            tmpN2 = Integer.parseInt(textFieldN2.getText());
            if (tmpN2 < 0) {
                tmpN2 = -tmpN2;
                popAlert("N2 is less than zero\nN2 set to abs(N2)");
            }
        } else {
            tmpN2 = habitat.getN2();
            popAlert("Blank string for N2\nN2 set as it was");
        }

        int tmpK = comboBoxK.getValue() != null ? comboBoxK.getValue() : habitat.getK();
        float tmpP = comboBoxP.getValue() != null ? comboBoxP.getValue() : habitat.getP();

        System.out.println("___________________________");
        System.out.println("N1: " + tmpN1 + " K: " + tmpK + " | N2: " + tmpN2 + " P: " + tmpP );
        habitat.setN1(tmpN1);
        habitat.setN2(tmpN2);
        habitat.setK(tmpK);
        habitat.setP(tmpP);
    }

    @FXML private void newModalWindow() {
        if (InfoCheckBox.isSelected()) {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            StackPane pane = new StackPane();

            Button btnClose = new Button("Close");
            btnClose.setOnAction(actionEvent -> {
                window.close();
                StopSimulation();
            });

            Button btnContinue = new Button("Continue");
            btnContinue.setOnAction(actionEvent -> {
                window.close();
                EditStart.setSelected(true);
            });

            Label data = new Label("Time: " + String.valueOf(simulationTime) +
                    "\n\nTotal: " + population +
                    "\nDrons: " + habitat.getDronsSize() +
                    "\nWorkers: " + habitat.getWorkersSize());
            HBox hbox = new HBox(btnContinue, btnClose);
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(20);
            VBox vbox = new VBox(data, hbox);
            vbox.setSpacing(20);
            vbox.setAlignment(Pos.CENTER);
            pane.setAlignment(vbox,Pos.CENTER);
            pane.getChildren().add(vbox);

            Scene scene = new Scene(pane, 300, 200);
            window.setScene(scene);
            window.setTitle("Information");
            window.showAndWait();
        } else {
            StopSimulation();
        }
    }


    public void StartSimulation() throws MalformedURLException {
        if (!isStarted) {
            StartBtn.setDisable(true);
            StopBtn.setDisable(false);
            EditStart.setSelected(true);
            checkSettings();
            mediaStart();
            settingInit();
            labelReset();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        simulationTime = (System.currentTimeMillis() - startTime) / 1000 + 1;
                        try {
                            System.out.println("Update: " + simulationTime);
                            update(simulationTime);
                            gettime().setText("Time: " + simulationTime);
                        } catch (FileNotFoundException exception) {
                            throw new RuntimeException(exception);
                        }
                    });
                }
            }, 0, 1000);
        }
    }

    private void update(long simulationTime) throws FileNotFoundException {
        Random r = new Random();
        int x,y;
        // Трутни: каждые Н1 если их число меньше К% от общего
        if (simulationTime % habitat.getN1() == 0  && habitat.getDronsSize() < (float)population * habitat.getK() / 100) {
            Drone drone = (Drone) habitat.create(viewPane.getHeight(), viewPane.getWidth(), Drone.class);
            System.out.println("Drone born");
            ++population;
            viewPane.getChildren().add(drone.getView());
        }
        // Рабочие: каждые Н2, с вероятностью Р
        int percent = r.nextInt(100);
        if (simulationTime % habitat.getN2() == 0 && percent < habitat.getP() * 100) {
            Worker worker = (Worker) habitat.create(viewPane.getHeight(), viewPane.getWidth(), Worker.class);
            System.out.println("Worker born");
            ++population;
            viewPane.getChildren().add(worker.getView());
        }
        System.out.println("Population:" + population + "\n");
    }

    public void StopSimulation() {
        StartBtn.setDisable(false);
        StopBtn.setDisable(true);
        EditStop.setSelected(true);
        player.stop();
        timer.cancel();
        timer.purge();
        viewPane.getChildren().clear();
        isStarted = false;
        long simulationTime = (System.currentTimeMillis() - startTime) / 1000 + 1;
        gettime().setText("Time: " + String.valueOf(simulationTime));
        habitat.clearLists();
        timer = new Timer();

    }
}