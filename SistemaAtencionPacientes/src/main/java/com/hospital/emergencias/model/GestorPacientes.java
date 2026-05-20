package com.hospital.emergencias.model;

import java.util.PriorityQueue;
import java.util.ArrayList;
import java.util.List;

public class GestorPacientes {
    // la cola de prioridad que organizará a los pacientes automáticamente 
    private PriorityQueue<Paciente> colaEspera;

    public GestorPacientes() {
        // Inicializamos la cola
        this.colaEspera = new PriorityQueue<>();
    }

    // metodo para registrar un nuevo paciente
    public void registrarPaciente(Paciente paciente) {
        colaEspera.add(paciente);
        System.out.println("Paciente registrado: " + paciente.getNombreCompleto());
    }

    // metodo para atender al siguiente paciente
    public Paciente atenderSiguiente() {
        // poll() extrae y elimina al paciente con mayor prioridad
        return colaEspera.poll(); 
    }

    // metodo para obtener la lista actual (para mostrarla en la tabla de la interfaz
    public List<Paciente> obtenerListaPacientes() {
        // convertimos la cola a una lista para que sea más fácil de leer para JavaFX
        return new ArrayList<>(colaEspera);
    }
    
    // verifica si hay pacientes en la cola
    public boolean tienePacientes() {
        return !colaEspera.isEmpty();
    }
    
    public PriorityQueue<Paciente> getCola() {
    return colaEspera;
}
}