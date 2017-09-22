/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package malamu;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import modelo.Partida;

/**
 * Responde peticiones de los clientes. Cada turno, recibe todas las jugadas de
 * los clientes y envía una respuesta de vuelta con una nueva lista de jugadores
 * que representa la .
 *
 * @author davl3232
 */
public class Servidor {

    /**
     * Dirección IP del servidor.
     */
    protected InetAddress direccion;

    /**
     * Tiempo de inactividad tomado desde la recepción del último mensaje desde
     * este servidor.
     */
    protected LocalDateTime tiempoInicioInactividad;

    /**
     * Tiempo de inactividad máximo que se permite antes de terminar la conexión
     * con este servidor.
     */
    protected Duration duracionMaximaInactividad;

    /**
     * Tiempo tomado al iniciar el emparejamiento para una nueva partida.
     */
    protected LocalDateTime tiempoInicioEmparejamiento;

    /**
     * Tiempo de emparejamiento máximo que se permite antes de iniciar una
     * partida con los clientes seleccionados hasta ese momento.
     */
    protected Duration duracionMaximaEmparejamiento;

    /**
     * Tiene la partida actual.
     */
    protected Partida partida;
    
    /**
     * Lista de los clientes que se encuentran en la partida.
     */
    protected List<Cliente> clientes = new ArrayList<Cliente>();

    /**
     * Tiene la cola con los clientes que desean unirse a la nueva partida.
     */
    protected List<Cliente> colaClientes = new ArrayList<Cliente>();

    /**
     * Constructor de un servidor.
     *
     * @param direccion
     * @param tiempoInicioInactividad
     * @param duracionMaximaInactividad
     * @param tiempoInicioEmparejamiento
     * @param duracionMaximaEmparejamiento
     */
    public Servidor(InetAddress direccion, LocalDateTime tiempoInicioInactividad, Duration duracionMaximaInactividad, LocalDateTime tiempoInicioEmparejamiento, Duration duracionMaximaEmparejamiento) {
        this.direccion = direccion;
        this.tiempoInicioInactividad = tiempoInicioInactividad;
        this.duracionMaximaInactividad = duracionMaximaInactividad;
        this.tiempoInicioEmparejamiento = tiempoInicioEmparejamiento;
        this.duracionMaximaEmparejamiento = duracionMaximaEmparejamiento;
    }

    public void calcularTurno() {

    }

    public void iniciarPartida() {
        while (!colaClientes.isEmpty()) {

        }
    }

    public void recibirJugadas() {

    }

    public void enviarResultados() {

    }

    public void matarDormidos() {

    }

    public void recibirJugadores() {

    }

    public boolean pedirConfirmacion(Cliente c) {
        return true;
    }
}
