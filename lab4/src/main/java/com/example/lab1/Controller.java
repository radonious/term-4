package com.example.lab1;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

import static javafx.scene.control.Alert.AlertType;


public class Controller implements Initializable {
    @FXML
    private Label time;
    @FXML
    private MediaView mediaView;
    @FXML
    private CheckBox InfoCheckBox;
    @FXML
    private CheckBox toggleWorkerAICheckBox;
    @FXML
    private CheckBox toggleDroneAICheckBox;
    @FXML
    private Button StartBtn;
    @FXML
    private Button StopBtn;
    @FXML
    private Button currentBtn;
    @FXML
    private RadioButton hideTimeRadio;
    @FXML
    private RadioButton showTimeRadio;
    @FXML
    private Pane mainPane;
    @FXML
    private Pane menuPane;
    @FXML
    private Pane viewPane;
    @FXML
    private RadioMenuItem EditHideTime;
    @FXML
    private RadioMenuItem EditShowTime;
    @FXML
    private RadioMenuItem EditStart;
    @FXML
    private RadioMenuItem EditStop;
    @FXML
    private ComboBox<Integer> comboBoxK;
    @FXML
    private ComboBox<Float> comboBoxP;
    @FXML
    private TextField textFieldN1;
    @FXML
    private TextField textFieldN2;
    @FXML
    private Slider deathTimeWorker;
    @FXML
    private Slider deathTimeDrone;

    private DroneAI droneAI;
    private WorkerAI workerAI;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> tmp1 = FXCollections.observableArrayList(
                0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100
        );
        comboBoxK.setItems(FXCollections.observableList(tmp1));
        comboBoxK.getSelectionModel().select((Integer) 30);
        ObservableList<Float> tmp2 = FXCollections.observableArrayList(
                0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f
        );
        comboBoxP.setItems(FXCollections.observableList(tmp2));
        comboBoxP.getSelectionModel().select(0.8f);
        //ThreadGroup AIGroup = new ThreadGroup("beeAI");
        droneAI = new DroneAI((int) viewPane.getPrefWidth(), (int) viewPane.getPrefHeight());
        workerAI = new WorkerAI((int) viewPane.getPrefWidth(), (int) viewPane.getPrefHeight());
        Stream.of(workerAI, droneAI).forEach(BaseAI::pauseAI);
        isStarted = false;
        timer = new Timer();
    }

    @FXML
    Pane getViewPane() {
        return viewPane;
    }

    @FXML
    MediaView getmedia() {
        return mediaView;
    }

    @FXML
    Pane getMenuPane() {
        return menuPane;
    }

    @FXML
    Label gettime() {
        return time;
    }

    @FXML
    private void setActionsOnKeys(KeyEvent keyEvent) throws MalformedURLException {
        KeyCode pressedKey = keyEvent.getCode();
        if (pressedKey == KeyCode.B && !isStarted) {
            System.out.println("Pressed B\nSimulation Started");
            viewPane.getChildren().removeIf(node -> (node instanceof ImageView));
            startSimulation();
        } else if (pressedKey == KeyCode.E && isStarted) {
            newModalWindow();
            System.out.println("Pressed E\nSimulation has stopped");
        } else if (pressedKey == KeyCode.E) {
            viewPane.getChildren().clear();
            System.out.println("Pressed E after E\nSimulation already stopped, but screen cleared again");
        } else if (pressedKey == KeyCode.T) {
            if (gettime().isVisible()) {
                hideTime();
            } else {
                showTime();
            }

        }
    }

    @FXML
    public void showTime() {
        gettime().setVisible(true);
        showTimeRadio.setSelected(true);
        EditShowTime.setSelected(true);
    }

    @FXML
    public void closeApp() {
        mainStage.close();
    }

    @FXML
    public void hideTime() {
        gettime().setVisible(false);
        hideTimeRadio.setSelected(true);
        EditHideTime.setSelected(true);
    }

    @FXML
    private void mediaStart() throws MalformedURLException {
        File mediaFile = new File(mp3Path);
        Media pick = new Media(mediaFile.toURI().toURL().toString());
        player = new MediaPlayer(pick);
        MediaView mediaView = getmedia();
        player.play();
    }

    @FXML
    private void settingInit() {
        population = 0;
        startTime = System.currentTimeMillis();
        isStarted = true;
    }

    @FXML
    private void labelReset() {
        showTime();
        gettime().setText("Time: 0");
    }

    @FXML
    private void popAlert(String msg) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setHeaderText("Look, an Information Dialog");
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void handleCloseRequest() {
        stopSimulation();
    }

    @FXML
    private void checkSettings() {
        int tmpN1 = habitat.getN1();
        try {
            if (!textFieldN1.getText().isBlank()) {
                tmpN1 = Integer.parseInt(textFieldN1.getText());
                if (tmpN1 < 0) {
                    tmpN1 = -tmpN1;
                    textFieldN1.setText(Integer.toString(tmpN1));
                    popAlert("N1 is less than zero\nN1 set to abs(N1)");
                }
            } else {
                textFieldN1.setText(Integer.toString(tmpN1));
                popAlert("Blank string for N1\nN1 was not changed");
            }
        } catch (NumberFormatException ex) {
            textFieldN1.setText(Integer.toString(tmpN1));
            popAlert("Forbidden characters in N1 input\nN1 was not changed");
        }

        int tmpN2 = habitat.getN2();
        try {
            if (!textFieldN2.getText().isBlank()) {
                tmpN2 = Integer.parseInt(textFieldN2.getText());
                if (tmpN2 < 0) {
                    tmpN2 = -tmpN2;
                    textFieldN2.setText(Integer.toString(tmpN2));
                    popAlert("N2 is less than zero\nN2 set to abs(N2)");
                }
            } else {
                textFieldN2.setText(Integer.toString(tmpN2));
                popAlert("Blank string for N2\nN2 was not changed");
            }
        } catch (NumberFormatException ex) {
            textFieldN2.setText(Integer.toString(tmpN2));
            popAlert("Forbidden characters in N2 input\nN2 was not changed");
        }

        int tmpK = comboBoxK.getValue() != null ? comboBoxK.getValue() : habitat.getK();
        float tmpP = comboBoxP.getValue() != null ? comboBoxP.getValue() : habitat.getP();

        System.out.println("___________________________");
        System.out.println("N1: " + tmpN1 + " K: " + tmpK + " | N2: " + tmpN2 + " P: " + tmpP);

        habitat.setN1(tmpN1);
        habitat.setN2(tmpN2);
        habitat.setK(tmpK);
        habitat.setP(tmpP);
    }

    public static class tmpData {
        private int time;
        private int id;

        public tmpData(Integer time, Integer id) {
            this.time = time;
            this.id = id;
        }

        public Integer getTime() {
            return time;
        }

        public Integer getId() {
            return id;
        }
    }

    @FXML
    private void showCurrentObjects() {
        ObservableList<tmpData> arr = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : habitat.getTree().entrySet()) {
            arr.add(new tmpData(entry.getValue(), entry.getKey()));
        }

        arr.sort(new Comparator<tmpData>() {
            public int compare(tmpData a, tmpData b) {
                int n1 = a.time;
                int n2 = b.time;
                return n1 - n2;
            }
        });

        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        StackPane pane = new StackPane();

        Button btnClose = new Button("Close");
        btnClose.setOnAction(actionEvent -> {
            window.close();
        });

        TableView<tmpData> table = new TableView<tmpData>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<tmpData, Integer> timeColumn = new TableColumn<>("Time");
        TableColumn<tmpData, Integer> idColumn = new TableColumn<>("Id");
        timeColumn.setCellValueFactory(new PropertyValueFactory<tmpData, Integer>("time"));
        idColumn.setCellValueFactory(new PropertyValueFactory<tmpData, Integer>("id"));

        table.getColumns().add(timeColumn);
        table.getColumns().add(idColumn);

        table.setItems(arr);

        final VBox vbox = new VBox();
        vbox.setSpacing(15);
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(table, btnClose);

        pane.setAlignment(vbox, Pos.CENTER);
        pane.getChildren().add(vbox);
        Scene scene = new Scene(pane, 300, 475);
        window.setScene(scene);
        window.setTitle("Information");
        window.showAndWait();
    }

    @FXML
    private void newModalWindow() {
        if (InfoCheckBox.isSelected()) {
            Stage window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            StackPane pane = new StackPane();

            Button btnClose = new Button("Close");
            btnClose.setOnAction(actionEvent -> {
                window.close();
                stopSimulation();
            });

            Button btnContinue = new Button("Continue");
            btnContinue.setOnAction(actionEvent -> {
                window.close();
                EditStart.setSelected(true);
            });

            Label data = new Label("Time: " + String.valueOf(simulationTime) +
                    "\n\nCount: " + population);
            HBox hbox = new HBox(btnContinue, btnClose);
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(20);
            VBox vbox = new VBox(data, hbox);
            vbox.setSpacing(20);
            vbox.setAlignment(Pos.CENTER);
            pane.setAlignment(vbox, Pos.CENTER);
            pane.getChildren().add(vbox);

            Scene scene = new Scene(pane, 300, 200);
            window.setScene(scene);
            window.setTitle("Information");
            window.showAndWait();
        } else {
            stopSimulation();
        }
    }

    public void startSimulation() throws MalformedURLException {
        if (!isStarted) {
            deathTimeWorker.setDisable(true);
            deathTimeDrone.setDisable(true);
            currentBtn.setDisable(false);
            StartBtn.setDisable(true);
            StopBtn.setDisable(false);
            EditStart.setSelected(true);
            textFieldN1.setDisable(true);
            textFieldN2.setDisable(true);
            comboBoxP.setDisable(true);
            comboBoxK.setDisable(true);
            checkSettings();
            mediaStart();
            settingInit();
            labelReset();
            timerInit();
            Stream.of(workerAI, droneAI).forEach(i -> {
                i.startAI();
                i.pauseAI();
            });
            checkAICheckBoxes();
            timer.schedule(timerInit(), 0, 1000);
        }
    }

    private TimerTask timerInit() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    simulationTime = (System.currentTimeMillis() - startTime) / 1000 + 1;
                    System.out.println("Update: " + simulationTime);
                    gettime().setText("Time: " + simulationTime);
                    clearDead();
                    try {
                        update(simulationTime);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    checkAISettings();
                });
            }
        };
    }

    private void checkAICheckBoxes() {
        if (toggleDroneAICheckBox.isSelected()) droneAI.resumeAI();
        else droneAI.pauseAI();
        if (toggleWorkerAICheckBox.isSelected()) workerAI.resumeAI();
        else workerAI.pauseAI();
    }

    private void checkAISettings() {
        checkAICheckBoxes();
        if (simulationTime % habitat.getN() == 0) {
            Random r = new Random();
            droneAI.setAng((int) (r.nextFloat(360) * Math.PI / 180));
        }
    }

    private void clearDead() {
        ArrayList<Bee> deads = new ArrayList<Bee>();
        synchronized (habitat.getList()) {
            for (Bee bee : habitat.getList()) {
                if (simulationTime - bee.getBornTime() > bee.getDeathTime()) {
                    System.out.println(bee.getClass().getSimpleName() + " dead");
                    deads.add(bee);
                }
            }
            population -= deads.size();
            for (Bee bee : deads) {
                int id = bee.getId();
                habitat.getTree().remove(id);
                habitat.getSet().remove(id);
                Platform.runLater(() -> viewPane.getChildren().remove(bee.getView()));
                habitat.getList().remove(bee);
            }
        }
    }

    private void update(long simulationTime) throws FileNotFoundException {
        Random r = new Random();
        int x, y;
        synchronized (habitat.getList()) {
            if (simulationTime % habitat.getN1() == 0 && habitat.getDronsSize() < (float) population * habitat.getK() / 100) {
                Drone drone = (Drone) habitat.create(viewPane.getHeight(), viewPane.getWidth(), Drone.class, simulationTime, deathTimeDrone.getValue());
                System.out.println("Drone born");
                ++population;
                Platform.runLater(() -> viewPane.getChildren().add(drone.getView()));
            }

            int percent = r.nextInt(100);
            if (simulationTime % habitat.getN2() == 0 && percent < habitat.getP() * 100) {
                Worker worker = (Worker) habitat.create(viewPane.getHeight(), viewPane.getWidth(), Worker.class, simulationTime, deathTimeWorker.getValue());
                System.out.println("Worker born");
                ++population;
                Platform.runLater(() -> viewPane.getChildren().add(worker.getView()));
            }
        }
        System.out.println("Population: " + population + "\n");
    }

    public void stopSimulation() {
        deathTimeWorker.setDisable(false);
        deathTimeDrone.setDisable(false);
        currentBtn.setDisable(true);
        StartBtn.setDisable(false);
        StopBtn.setDisable(true);
        EditStop.setSelected(true);
        textFieldN1.setDisable(false);
        textFieldN2.setDisable(false);
        comboBoxP.setDisable(false);
        comboBoxK.setDisable(false);
        player.stop();
        timer.cancel();
        timer.purge();
        viewPane.getChildren().clear();
        isStarted = false;
        long simulationTime = (System.currentTimeMillis() - startTime) / 1000 + 1;
        Stream.of(workerAI, droneAI).forEach(BaseAI::pauseAI);
        gettime().setText("Time: " + String.valueOf(simulationTime));
        habitat.clearData();
        timer = new Timer();
    }
}