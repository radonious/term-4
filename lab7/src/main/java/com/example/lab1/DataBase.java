package com.example.lab1;

import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;

public class DataBase {
    static boolean connected = false;
    static Connection connection = null;
    static final String DB_URL = "jdbc:postgresql://127.0.0.1:5432/postgres";
    static final String USER = "radon";
    static final String PASS = "";

    private static void connectDB() throws SQLException {
        System.out.println("Connection to PostgreSQL JDBC...");

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        try {
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            connected = true;
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You successfully connected to" + connection.getCatalog() + " database now");
            System.out.println("URL: " + connection.getMetaData().getURL());
        } else {
            System.out.println("Failed to make connection to database");
        }
    }

    private static void disconnectDB()  {
        if (connected)  {
            try {
                connection.close();
                connected = false;
            } catch (SQLException e) {
                System.out.println("Connection Failed");
                e.printStackTrace();
            }
        }
    }

    public static void save(ArrayList<Bee> bees) throws SQLException {
        if (bees == null || bees.size() == 0) return;
        if (!connected) connectDB();

        connection.prepareStatement("DELETE FROM bees").executeUpdate();
        String sql_request = "INSERT INTO bees (xs, ys, xc, yc, bt, dt, v, type) values (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql_request);

        for (Bee bee : bees) {
            statement.setInt(1, (int)bee.getX_start());
            statement.setInt(2, (int)bee.getY_start());
            statement.setInt(3, (int)bee.getX_curr());
            statement.setInt(4, (int)bee.getY_curr());
            statement.setInt(5, (int)bee.getBornTime());
            statement.setInt(6, (int)bee.getDeathTime());
            statement.setInt(7, (int)bee.getV());
            statement.setString(8, bee.getClass().getSimpleName());
            System.out.println(statement);
            statement.executeUpdate();
        }
        disconnectDB();
    }

    public static ArrayList<Bee> load() throws SQLException, FileNotFoundException {
        if (!connected) connectDB();
        ArrayList<Bee> arr = new ArrayList<>();
        ResultSet res = connection.prepareStatement("SELECT * FROM bees").executeQuery();
        while (res.next()) {
            String type = res.getString("type");
            if (type.equals("Drone")) {
                Drone drone = new Drone(
                        res.getInt("xs"),
                        res.getInt("ys"),
                        Habitat.gifDrone,
                        0,
                        res.getInt("bt"),
                        res.getInt("dt"),
                        res.getInt("v")
                );
                drone.setX_curr(res.getInt("xc"));
                drone.setY_curr(res.getInt("yc"));
                drone.syncPos();
                arr.add(drone);
            } else {
                Worker worker = new Worker(
                        res.getInt("xs"),
                        res.getInt("ys"),
                        Habitat.gifWorker,
                        0,
                        res.getInt("bt"),
                        res.getInt("dt"),
                        res.getInt("v")
                );
                worker.setX_curr(res.getInt("xc"));
                worker.setY_curr(res.getInt("yc"));
                worker.syncPos();
                arr.add(worker);
            }
        }
        disconnectDB();
        return arr;
    }
}
