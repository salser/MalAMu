package malamu;

import java.net.InetAddress;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import modelo.Jugada;
import modelo.Jugador;
import modelo.Partida;
import malamu.Servidor;
import modelo.Ronda;

/**
 * Clase que modela la lógica e información del cliente del juego.
 *
 * @author Juan Espinosa, Henry Salazar, David Villamizar
 */
public class Cliente {

    /**
     * Atributo que representa la dirección IP de un cliente.
     */
    private InetAddress direccion;
    /**
     * Valor en el que se almacena la última.
     */
    private LocalDate tiempoUltimoMensaje;

    /**
     * Valor que representa la mayor cantidad de tiempo que el cliente ha estado
     * inactivo.
     */
    private Duration duracionMaximaInactividad;

    /**
     * Valor que representa el estado del jugador asociado al cliente.
     */
    private Jugador jugador;

    /**
     * Interfaz gráfica de usuario del cliente.
     */
    private GUICliente gui;

    /**
     * Relación con la clase partida de la cual el cliente forma parte.
     */
    private Partida partida;
    
    /**
     * Última decisión tomada por el jugador.
     */
    private Jugada ultimaJugada;
    
    /**
     *  Registro de la información de la última ronda.
     */
    private Ronda ultimaRonda;
    

    /**
     * Constructor de un cliente.
     *
     * @param direccion
     * @param jugador
     * @param jugadas
     * @param gui
     */
    public Cliente(InetAddress direccion, Jugador jugador, List<Jugada> jugadas, GUICliente gui) {
        this.direccion = direccion;
        this.tiempoUltimoMensaje = LocalDate.now();

        this.jugador = jugador;
        this.gui = gui;
    }

    /**
     * Método que se invoca para unirse a partida.
     */
    public void iniciarPartida() {

    }

    /**
     * Método que se invoca para que el jugador envíe una jugada a la partida.
     */
    public void enviarJugada() {

    }

    /**
     * Método que se invoca para solicitar a la partida los resultados de la
     * ronda anterior.
     */
    public void recibirResultados() {

    }

    /**
     * Método que se invoca para confirmarle al servidor que el cliente decide
     * conectarse
     *
     * @param res Decisión del jugador.
     */
    public void responderConfirmacion(boolean res) {

    }

}
