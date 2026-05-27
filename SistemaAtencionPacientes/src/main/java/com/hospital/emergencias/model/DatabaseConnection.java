package com.hospital.emergencias.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DatabaseConnection {
    private static final String URL = "jdbc:sqlite:hospital.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBaseDatos() {
        String sql = """
            CREATE TABLE IF NOT EXISTS pacientes (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                edad INTEGER,
                dpi TEXT,
                sintomas TEXT,
                prioridad TEXT,
                hora_ingreso TEXT
            );
            """;
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
