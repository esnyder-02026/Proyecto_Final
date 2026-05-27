package com.hospital.emergencias.controller;

import com.hospital.emergencias.model.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.sql.*;
import com.hospital.emergencias.model.DatabaseConnection;

public class MainController {

    @FXML private TextField txtNombre, txtEdad, txtDPI;
    @FXML private TextArea txtSintomas;
    @FXML private ComboBox<PriorityLevel> cmbPrioridad;
    @FXML private TableView<Paciente> tblPacientes;
    @FXML private TableColumn<Paciente, String> colNombre;
    @FXML private TableColumn<Paciente, PriorityLevel> colPrioridad;
    @FXML private TableColumn<Paciente, LocalDateTime> colHora;
    @FXML private TableColumn<Paciente, String> colSintomas; 

    private GestorPacientes gestor = new GestorPacientes();

    @FXML
    public void initialize() {
        // 1. Llenar ComboBox
        cmbPrioridad.setItems(FXCollections.observableArrayList(PriorityLevel.values()));

        // 2. Configurar Columnas
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colPrioridad.setCellValueFactory(new PropertyValueFactory<>("prioridad"));
        colSintomas.setCellValueFactory(new PropertyValueFactory<>("sintomas"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("horaIngreso"));
        
        // Formato de hora limpio
        colHora.setCellFactory(column -> new TableCell<Paciente, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });

        // 3. Cargar datos guardados de la sesión anterior
        cargarDatosDesdeBD();
    }

    @FXML
private void handleRegistrar() {
    try {
        String nombre = txtNombre.getText();
        int edad = Integer.parseInt(txtEdad.getText());
        String dpi = txtDPI.getText();
        String sintomas = txtSintomas.getText();
        PriorityLevel prioridad = cmbPrioridad.getValue();

        if (prioridad == null) throw new Exception("Debe seleccionar una prioridad");

        Paciente nuevo = new Paciente(nombre, edad, dpi, sintomas, prioridad);
        
        // 1. Guardamos en la Base de Datos física
        guardarEnBaseDatos(nuevo);
        
        // 2. Registramos en la lógica del gestor
        gestor.registrarPaciente(nuevo);

        // 3. Limpiamos pero NO llamamos a handleActualizar()
        limpiarFormulario();
        
        mostrarAlerta("Registro Exitoso", "Paciente guardado en el sistema. \nPresiona el botón 'Actualizar' para ver la lista ordenada.");
        
    } catch (Exception e) {
        mostrarAlerta("Error", "Datos inválidos: " + e.getMessage());
    }
}

    @FXML
    private void handleAtender() {
        if (gestor.tienePacientes()) {
            Paciente atendido = gestor.atenderSiguiente();
            
            // --- NUEVO: ELIMINAR DE BASE DE DATOS AL ATENDER ---
            eliminarDeBaseDatos(atendido.getDpi());
            
            mostrarAlerta("Atención", "Atendiendo a: " + atendido.getNombreCompleto());
            handleActualizar(); 
        } else {
            mostrarAlerta("Aviso", "No hay pacientes en espera.");
        }
    }

    @FXML
private void handleActualizar() {
    // 1. Verificamos que el gestor y la cola no sean nulos
    if (gestor != null && gestor.getCola() != null) {
        
        // 2. Extraemos los pacientes de la cola a una lista para poder ordenarlos visualmente
        List<Paciente> listaTemporal = new ArrayList<>(gestor.getCola());

        // 3. Ordenamos: Primero por Valor de Prioridad (1, 2, 3...) y luego por Hora
        listaTemporal.sort(Comparator
            .comparingInt((Paciente p) -> p.getPrioridad().getValue()) 
            .thenComparing(Paciente::getHoraIngreso));

        // 4. Creamos la lista observable y la vinculamos a la tabla
        ObservableList<Paciente> listaObservable = FXCollections.observableArrayList(listaTemporal);
        tblPacientes.setItems(listaObservable);
        
        // 5. Forzamos el refresco visual
        tblPacientes.refresh();
        
        System.out.println("Lista actualizada manualmente."); // Esto saldrá en tu consola de NetBeans
    }
}

    // --- MÉTODOS DE BASE DE DATOS IMPLEMENTADOS ---

    private void guardarEnBaseDatos(Paciente p) {
        String sql = "INSERT INTO pacientes (nombre, edad, dpi, sintomas, prioridad, hora_ingreso) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, p.getNombreCompleto());
            pstmt.setInt(2, p.getEdad());
            pstmt.setString(3, p.getDpi());
            pstmt.setString(4, p.getSintomas());
            pstmt.setString(5, p.getPrioridad().name());
            pstmt.setString(6, p.getHoraIngreso().toString());
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al guardar en BD: " + e.getMessage());
        }
    }

    private void eliminarDeBaseDatos(String dpi) {
        String sql = "DELETE FROM pacientes WHERE dpi = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dpi);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error al eliminar de BD: " + e.getMessage());
        }
    }

    private void cargarDatosDesdeBD() {
        String sql = "SELECT * FROM pacientes";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Paciente p = new Paciente(
                    rs.getString("nombre"),
                    rs.getInt("edad"),
                    rs.getString("dpi"),
                    rs.getString("sintomas"),
                    PriorityLevel.valueOf(rs.getString("prioridad"))
                );
                // La fecha se asigna al crear el objeto Paciente en su constructor
                gestor.registrarPaciente(p);
            }
            handleActualizar();
        } catch (Exception e) {
            System.err.println("Error al cargar datos: " + e.getMessage());
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtEdad.clear();
        txtDPI.clear();
        txtSintomas.clear();
        cmbPrioridad.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
