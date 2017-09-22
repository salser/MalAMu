package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import malamu.Cliente;

/**
 * Clase Partida que modela la lógica que representa una partida de MalAMu, esto
 * es, asignación de turnos e informar a los jugadores del progreso del juego.
 *
 * @author Juan Espinosa, Henry Salazar, David Villamizar
 */
public class Partida {

    /**
     * Valor de tipo LocalDate que representa la fecha en la que se inició la
     * partida de MalAMu.
     */
    private LocalDate fechaInicio;

    /**
     * Número de turnos jugados en la partida.
     */
    private int numeroTurno;

    /**
     * Lista de clientes que están conectados en la partida.
     */
    private List<Cliente> clientes;
    
    /**
     * Registro de todas las jugadas hechas.
     */
    private List<Ronda> rondas;
    
    public Partida() {
        this.fechaInicio = LocalDate.now();
        this.numeroTurno = 0;
        this.clientes = new ArrayList<Cliente>();
        this.rondas = new ArrayList<Ronda>();
    }

    public void agregarCliente(Cliente cliente) {
        clientes.add(cliente);
    }

}
