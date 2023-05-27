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
import javafx.scene.image.Image;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
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
    @FXML
    private TextArea textAreaConnections;

    private Stage sendStage;
    private DroneAI droneAI;
    private WorkerAI workerAI;
    private Stage mainStage;
    private Habitat habitat;
    private static int population;
    private static boolean isStarted;
    private static boolean isConnected;
    private static Timer timer;
    private static MediaPlayer player;
    private static final String mp3Path;
    private long simulationTime;
    private static Client client;

    static {
        mp3Path = "src/main/resources/com/example/lab1/music/song3.mp3";
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    public void setHabitat(Habitat donor) {
        habitat = donor;
    }

    @FXML
    MediaView getMediaView() {
        return mediaView;
    }

    @FXML
    Label getTimeLabel() {
        return time;
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
        textAreaConnections.appendText("Disconnected");
        labelReset();
        initAI();
        isStarted = false;
        timer = new Timer();
    }

    public void initAI() {
        //ThreadGroup AIGroup = new ThreadGroup("beeAI");
        droneAI = new DroneAI((int) viewPane.getPrefWidth(), (int) viewPane.getPrefHeight());
        workerAI = new WorkerAI((int) viewPane.getPrefWidth(), (int) viewPane.getPrefHeight());
        Stream.of(workerAI, droneAI).forEach(BaseAI::pauseAI);
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
            checkPaneSettings();
            //mediaStart();
            simulationStatusReset();
            labelReset();
            Stream.of(workerAI, droneAI).forEach(i -> {
                i.startAI();
                i.pauseAI();
            });
            checkAICheckBoxes();
            timer.schedule(timerInit(), 0, 1000);
        }
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
        if (player != null) player.stop();
        timer.cancel();
        timer.purge();
        isStarted = false;
        Stream.of(workerAI, droneAI).forEach(BaseAI::pauseAI);
        getTimeLabel().setText("Time: " + simulationTime + " s");
        labelReset();
        habitat.clearData();
        viewPane.getChildren().clear();
        timer = new Timer();
    }

    private TimerTask timerInit() {
        return new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    simulationTime++;
                    System.out.println("Update: " + simulationTime);
                    getTimeLabel().setText("Time: " + simulationTime + " s");
                    checkAISettings();
                    clearDeadObjects();
                    try {
                        update(simulationTime);
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        };
    }

    private void update(long simulationTime) throws FileNotFoundException {
        Random r = new Random();
        int x, y;
        synchronized (habitat.getList()) {
            if (simulationTime % habitat.getN1() == 0 && habitat.getDronsSize() < (float) population * habitat.getK() / 100) {
                Drone drone = (Drone) habitat.create(viewPane.getHeight(), viewPane.getWidth(), Drone.class, simulationTime);
                System.out.println("Drone born");
                ++population;
                Platform.runLater(() -> viewPane.getChildren().add(drone.getView()));
            }

            int percent = r.nextInt(100);
            if (simulationTime % habitat.getN2() == 0 && percent < habitat.getP() * 100) {
                Worker worker = (Worker) habitat.create(viewPane.getHeight(), viewPane.getWidth(), Worker.class, simulationTime);
                System.out.println("Worker born");
                ++population;
                Platform.runLater(() -> viewPane.getChildren().add(worker.getView()));
            }
        }
        System.out.println("Population: " + population + "\n");
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
            if (getTimeLabel().isVisible()) {
                hideTime();
            } else {
                showTime();
            }

        }
    }

    @FXML
    public void showTime() {
        getTimeLabel().setVisible(true);
        showTimeRadio.setSelected(true);
        EditShowTime.setSelected(true);
    }

    @FXML
    public void hideTime() {
        getTimeLabel().setVisible(false);
        hideTimeRadio.setSelected(true);
        EditHideTime.setSelected(true);
    }

    @FXML
    private void mediaStart() throws MalformedURLException {
        File mediaFile = new File(mp3Path);
        Media pick = new Media(mediaFile.toURI().toURL().toString());
        player = new MediaPlayer(pick);
        MediaView mediaView = getMediaView();
        player.play();
    }

    @FXML
    private void simulationStatusReset() {
        population = 0;
        simulationTime = 0;
        isStarted = true;
    }

    @FXML
    private void labelReset() {
        showTime();
        getTimeLabel().setText("Time: 0 s");
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
    public void closeRequest() throws IOException {
        stopSimulation();
        saveConfigOnClose();
        if (sendStage != null)
            sendStage.close();
    }

    @FXML
    private void checkPaneSettings() {
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
        habitat.setDeathDrone(deathTimeDrone.getValue());
        habitat.setDeathWorker(deathTimeWorker.getValue());
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

            Label data = new Label("Time: " + simulationTime + " s" +
                    "\n\nCount: " + population);
            HBox hbox = new HBox(btnContinue, btnClose);
            hbox.setAlignment(Pos.CENTER);
            hbox.setSpacing(20);
            VBox vbox = new VBox(data, hbox);
            vbox.setSpacing(20);
            vbox.setAlignment(Pos.CENTER);
            StackPane.setAlignment(vbox, Pos.CENTER);
            pane.getChildren().add(vbox);

            Scene scene = new Scene(pane, 300, 200);
            window.setScene(scene);
            window.setTitle("Information");
            window.showAndWait();
        } else {
            stopSimulation();
        }
    }

    private void checkAISettings() {
        checkAICheckBoxes();
        if (simulationTime % habitat.getN() == 0) {
            Random r = new Random();
            droneAI.setAng((int) (r.nextFloat(360) * Math.PI / 180));
        }
    }

    private void checkAICheckBoxes() {
        if (toggleDroneAICheckBox.isSelected()) droneAI.resumeAI();
        else droneAI.pauseAI();
        if (toggleWorkerAICheckBox.isSelected()) workerAI.resumeAI();
        else workerAI.pauseAI();
    }

    private void clearDeadObjects() {
        ArrayList<Bee> deadList = new ArrayList<Bee>();
        synchronized (habitat.getList()) {
            for (Bee bee : habitat.getList()) {
                if (simulationTime - bee.getBornTime() > bee.getDeathTime()) {
                    System.out.println(bee.getClass().getSimpleName() + " dead");
                    deadList.add(bee);
                }
            }
            population -= deadList.size();
            for (Bee bee : deadList) {
                int id = bee.getId();
                habitat.getTree().remove(id);
                habitat.getSet().remove(id);
                Platform.runLater(() -> viewPane.getChildren().remove(bee.getView()));
                habitat.getList().remove(bee);
            }
        }
    }

    @FXML
    public void showConsole() {
        sendStage = new Stage();
        sendStage.initModality(Modality.NONE);
        StackPane pane = new StackPane();

        Button btnClose = new Button("Close");
        btnClose.setOnAction(actionEvent -> {
            sendStage.close();
        });

        TextArea area = new TextArea();
        final VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        vbox.getChildren().addAll(area, btnClose);
        StackPane.setAlignment(vbox, Pos.CENTER);
        pane.getChildren().add(vbox);
        Scene scene = new Scene(pane, 550, 200);

        area.setOnKeyPressed(keyEvent -> {
            KeyCode pressedKey = keyEvent.getCode();
            if (pressedKey == KeyCode.ENTER) {
                ObservableList<CharSequence> strings = area.getParagraphs();
                String lastStr = strings.get(strings.size() - 2).toString();
                int wordsCount = lastStr.isEmpty() ? 0 : new StringTokenizer(lastStr).countTokens();
                if (wordsCount != 0) {
                    String[] tokens = lastStr.split(" ", wordsCount);
                    if (tokens[0].equals("count") && wordsCount > 1) {
                        if (tokens[1].equals("drone") || tokens[1].equals("drones")) {
                            area.appendText("Drones alive: " + habitat.getDronsSize() + "\n");
                        } else if (tokens[1].equals("worker") || tokens[1].equals("workers")) {
                            area.appendText("Workers alive: " + habitat.getWorkersSize() + "\n");
                        } else {
                            area.appendText("Wrong parameter [" + tokens[1] + "] in command [" + tokens[0] + "]\n");
                        }
                    } else if (tokens[0].equals("clear")) {
                        area.clear();
                    } else {
                        area.appendText("Wrong command: " + tokens[0] + "\n");
                    }
                }
            }
            if (pressedKey == KeyCode.UP) {
                ObservableList<CharSequence> strings = area.getParagraphs();
                if (strings.size() > 2) {
                    area.appendText(strings.get(strings.size() - 3).toString());
                }
            }
        });
        sendStage.setScene(scene);
        sendStage.setTitle("Console");
        sendStage.showAndWait();
    }

    private void updateHabitatSettings() {
        comboBoxK.setValue(habitat.getK());
        comboBoxP.setValue(habitat.getP());
        textFieldN1.setText(String.valueOf(habitat.getN1()));
        textFieldN2.setText(String.valueOf(habitat.getN2()));
        deathTimeDrone.setValue(habitat.getDeathDrone());
        deathTimeWorker.setValue(habitat.getDeathWorker());
    }

    private void save(File file) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
        habitat.setSimulationTime(simulationTime);
        oos.writeObject(habitat);
        oos.close();
    }

    @FXML
    public void saveState() throws IOException {
        checkPaneSettings();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Config File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("bin file", "*.bin");
        fileChooser.getExtensionFilters().addAll(extFilter);
        fileChooser.setInitialFileName("custom_bee_state.bin");
        File file = fileChooser.showSaveDialog(mainStage);
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            save(file);
        } else {
            popAlert("file was not chosen");
        }

    }

    private void load(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        Habitat habitatRead = (Habitat) ois.readObject();
        ois.close();
        setHabitat(habitatRead);
        updateHabitatSettings();
        for (Bee bee : habitat.getList()) {
            if (bee.getClass() == Drone.class) {
                Image img = new Image(new FileInputStream(habitat.getImgDrone()));
                bee.setView(new ImageView(img));
            } else if (bee.getClass() == Worker.class) {
                Image img = new Image(new FileInputStream(habitat.getImgWorker()));
                bee.setView(new ImageView(img));
            }
            bee.syncPos();
            Platform.runLater(() -> viewPane.getChildren().add(bee.getView()));
        }
        BeeData.getInstance().setList(habitat.getList());
        BeeData.getInstance().setSet(habitat.getSet());
        BeeData.getInstance().setTree(habitat.getTree());
        habitat.initData();
        Stream.of(workerAI, droneAI).forEach(BaseAI::updateList);
    }

    @FXML
    public void loadState() throws IOException, ClassNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Config File");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("bin file", "*.bin");
        fileChooser.getExtensionFilters().addAll(extFilter);
        File file = fileChooser.showOpenDialog(mainStage);
        if (file != null) {
            fileChooser.setInitialDirectory(file.getParentFile());
            if (isStarted) stopSimulation();
            load(file);
            startSimulation();
            simulationTime = habitat.getSimulationTime();
            population += habitat.getList().size();
        } else {
            popAlert("file was not chosen");
        }
    }

    public void loadConfigOnStart() throws IOException {

        InputStream input= new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/com/example/lab1/config/default.cfg");
            Properties prop = new Properties();
            prop.load(input);

            habitat.setK(Integer.parseInt(prop.getProperty("habitat.K")));
            habitat.setP(Float.parseFloat(prop.getProperty("habitat.P")));
            habitat.setN1(Integer.parseInt(prop.getProperty("habitat.N1")));
            habitat.setN2(Integer.parseInt(prop.getProperty("habitat.N2")));
            habitat.setV(Integer.parseInt(prop.getProperty("habitat.V")));
            habitat.setDeathDrone(Double.parseDouble(prop.getProperty("habitat.deathDrone")));
            habitat.setDeathWorker(Double.parseDouble(prop.getProperty("habitat.deathWorker")));
            updateHabitatSettings();
    }

    public void saveConfigOnClose() throws IOException {
        checkPaneSettings();

        OutputStream output = new FileOutputStream(System.getProperty("user.dir") + "/src/main/resources/com/example/lab1/config/default.cfg");
        Properties prop = new Properties();
        prop.setProperty("habitat.K", Integer.toString(habitat.getK()));
        prop.setProperty("habitat.P", Float.toString(habitat.getP()));
        prop.setProperty("habitat.N1", Integer.toString(habitat.getN1()));
        prop.setProperty("habitat.N2", Integer.toString(habitat.getN2()));
        prop.setProperty("habitat.V", Integer.toString(habitat.getV()));
        prop.setProperty("habitat.deathDrone", Double.toString(habitat.getDeathDrone()));
        prop.setProperty("habitat.deathWorker", Double.toString(habitat.getDeathWorker()));

        prop.store(output, null);
    }

    @FXML
    void connectToServer() {
        if (isConnected) return;

        sendStage = new Stage();
        sendStage.initModality(Modality.APPLICATION_MODAL);
        StackPane pane = new StackPane();

        TextField area_ip = new TextField();
        area_ip.setText("127.0.0.1");
        Label label_ip = new Label();
        label_ip.setText("          IP:");
        HBox box_ip = new HBox();
        box_ip.getChildren().addAll(label_ip, area_ip);
        box_ip.setSpacing(20);

        TextField area_port = new TextField();
        area_port.setText("8888");
        Label label_port = new Label();
        label_port.setText("  PORT:");
        HBox box_port = new HBox();
        box_port.getChildren().addAll(label_port, area_port);
        box_port.setSpacing(20);

        Button btnConnect = new Button("Connect");
        btnConnect.setDefaultButton(true);
        btnConnect.setOnAction(actionEvent -> {
            client = new Client(this);
            isConnected = client.connect(area_ip.getText(), Integer.parseInt(area_port.getText()));
            if (isConnected) {
                client.start();
            } else {
                popAlert("Can not connect");
            }
            sendStage.close();
        });
        Button btnClose = new Button("Close");
        btnClose.setOnAction(actionEvent -> {
            sendStage.close();
        });
        HBox box_btn = new HBox();
        box_btn.getChildren().addAll(btnConnect, btnClose);
        box_btn.setAlignment(Pos.CENTER);
        box_btn.setSpacing(10);

        final VBox vbox = new VBox();
        vbox.getChildren().addAll(box_ip, box_port, box_btn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        pane.getChildren().add(vbox);
        Scene scene = new Scene(pane, 250, 150);

        sendStage.setScene(scene);
        sendStage.setTitle("Connection");
        sendStage.showAndWait();
    }

    @FXML
    void disconnectFromServer() {
        if (!isConnected) return;
        isConnected = client.disconnect();
        textAreaConnections.clear();
        textAreaConnections.appendText("Disconnected");
    }

    @FXML
    void sentObjectsOnServer() {
        if (!isConnected || habitat.getList().size() == 0) return;
        sendStage = new Stage();
        sendStage.initModality(Modality.APPLICATION_MODAL);
        StackPane pane = new StackPane();

        ObservableList<String> tmp = FXCollections.observableArrayList(client.clients);
        System.out.println(tmp);
        ComboBox clientsComboBox = new ComboBox(tmp);

        Button btnSend = new Button("Send");
        btnSend.setDefaultButton(true);
        btnSend.setOnAction(actionEvent -> {
            ArrayList<Bee> toSend = new ArrayList<>();
            Random r = new Random();
            while (toSend.size() < habitat.getN()) {
                toSend.add(habitat.getList().get(r.nextInt(habitat.getList().size())));
            }
            System.out.println(toSend);
            ClientDTO DTO = ClientDTO.builder()
                    .dtoType(ClientDTO.dtoType.CLIENT_REQUEST)
                    .dtoObject(ClientDTO.dtoObject.OBJECTS)
                    .toName((String) clientsComboBox.getValue())
                    .beeList(toSend)
                    .build();
            client.sendDTO(DTO);
            sendStage.close();
        });
        Button btnClose = new Button("Close");
        btnClose.setOnAction(actionEvent -> {
            sendStage.close();
        });

        HBox box_btn = new HBox();
        box_btn.getChildren().addAll(btnSend, btnClose);
        box_btn.setAlignment(Pos.CENTER);
        box_btn.setSpacing(10);

        final VBox vbox = new VBox();
        vbox.getChildren().addAll(clientsComboBox, box_btn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setSpacing(20);
        pane.getChildren().add(vbox);
        Scene scene = new Scene(pane, 250, 150);

        sendStage.setScene(scene);
        sendStage.setTitle("Sending");
        sendStage.showAndWait();
    }

    public void setConnections(ArrayList<String> clients, String you) {
        if (!textAreaConnections.getText().isBlank() && !textAreaConnections.getText().isEmpty())
            textAreaConnections.clear();
        for (String client : clients) {
            textAreaConnections.appendText(client);
            if (client.equals(you)) textAreaConnections.appendText(" - You");
            textAreaConnections.appendText("\n");

        }
    }

    public synchronized void showBeeOnViewPane(Bee bee) {
        ++population;
        Platform.runLater(() -> viewPane.getChildren().add(bee.getView()));
    }

    public String getDronePath() {
        return habitat.getGifDrone();
    }

    public String getWorkerPath() {
        return habitat.getGifWorker();
    }

    @FXML
    public void saveIntoDB() throws SQLException, ClassNotFoundException {
        DataBase.save(habitat.getList());
    }

    @FXML
    public void loadFromDB() throws SQLException, ClassNotFoundException, FileNotFoundException, MalformedURLException {
        if (isStarted) stopSimulation();
        startSimulation();

        ArrayList<Bee> bees = DataBase.load();
        for (Bee bee : bees) {
            if (bee.getClass() == Drone.class) {
                Image img = new Image(new FileInputStream(getDronePath()));
            } else if (bee.getClass() == Worker.class) {
                Image img = new Image(new FileInputStream(getWorkerPath()));
            }
            bee.syncPos();
            Platform.runLater(() -> viewPane.getChildren().add(bee.getView()));
        }
        BeeData.getInstance().setList(habitat.getList());
        BeeData.getInstance().setSet(habitat.getSet());
        BeeData.getInstance().setTree(habitat.getTree());
        habitat.initData();
        Stream.of(workerAI, droneAI).forEach(BaseAI::updateList);
        simulationTime = habitat.getSimulationTime();
        population += habitat.getList().size();
    }
}