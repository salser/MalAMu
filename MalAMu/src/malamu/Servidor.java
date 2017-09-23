/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package malamu;

import comunicacion.ServiciosComunicacion;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Jugada;
import modelo.Jugador;
import modelo.Partida;
import modelo.Ronda;
import modelo.TipoAccion;

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
    
    protected int numJugadoresMax;

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
     * Tiene las conexiones actuales.
     */
    protected List<Socket> sockets = new ArrayList<>();
    
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
     * @param numJugadoresMax
     * @param tiempoInicioInactividad
     * @param duracionMaximaInactividad
     * @param tiempoInicioEmparejamiento
     * @param duracionMaximaEmparejamiento
     */
    public Servidor(InetAddress direccion, int numJugadoresMax, LocalDateTime tiempoInicioInactividad, Duration duracionMaximaInactividad, LocalDateTime tiempoInicioEmparejamiento, Duration duracionMaximaEmparejamiento) {
        this.direccion = direccion;
        this.numJugadoresMax = numJugadoresMax;
        this.tiempoInicioInactividad = tiempoInicioInactividad;
        this.duracionMaximaInactividad = duracionMaximaInactividad;
        this.tiempoInicioEmparejamiento = tiempoInicioEmparejamiento;
        this.duracionMaximaEmparejamiento = duracionMaximaEmparejamiento;
    }
    
    public static void main(String [ ] args) {
        Servidor servidor;
        try {
            servidor = new Servidor(InetAddress.getByName("127.0.0.1"), 12, null, Duration.ofMillis(1000), null, Duration.ofMillis(0));
            
            for (int i = 0; i < 12; i++) {
                servidor.colaClientes.add(new Cliente(InetAddress.getLocalHost(), new Jugador("Jugador " + i)));
            }
            servidor.iniciarPartida();
        } catch (UnknownHostException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Ejecuta toda la ronda del juego, hace los respectivos movimientos de recarga, defensa y ataque, en ese orden
     * @param ronda que contiene las jugadas de dicha ronda 
     * @param jugadores la lista de los jugadores del juego a los que recaen las jugadas
     */
    public void ejecutarRonda(Ronda ronda, List<Jugador> jugadores) {
        
        List<Jugada> jugadas = ronda.getJugadas();
        // Ejecutar jugadas de recarga
        for (Jugada jugada : jugadas) {
            if (jugada.getTipo() == TipoAccion.RECARGA) {
                Jugador jugador = jugada.getJugador();
                jugador.recargar();
            }
        }

        // Ejecutar jugadas de defensa
        List<Jugador> defienden = new ArrayList<>();
        for (Jugada jugada : jugadas) {
            if (jugada.getTipo() == TipoAccion.DEFENSA) {
                Jugador jugador = jugada.getJugador();
                jugador.gastarCargaDefensa();
                defienden.add(jugador);
            }
        }

        // Ejecutar jugadas de ataque
        for (Jugada jugada : jugadas) {
            if (jugada.getTipo() == TipoAccion.ATAQUE) {
                Jugador jugador = jugada.getJugador();
                Jugador objetivo = jugada.getObjetivo();
                jugador.gastarCargaAtaque();
                if (!defienden.contains(objetivo)) {
                    // Si el objetivo no se esta defendiendo
                    // Hacer daño
                    objetivo.recibirDano(jugador.getDanoAtaque());
                }
            }
        }
    }

    public void iniciarPartida() {
        clientes = colaClientes.subList(colaClientes.size() - 12, colaClientes.size());
        colaClientes = colaClientes.subList(0, colaClientes.size() - 12);
        List<InetAddress> direccionesDst = new ArrayList<>();
        for (int i = 0; i < clientes.size(); i++) {
            direccionesDst.add(clientes.get(i).getDireccion());
        }
        sockets = ServiciosComunicacion.abrirSockets(direccionesDst);
    }

    public void recibirJugadas() {
        //TODO
    }

    public void enviarResultados() {
        //TODO
    }

    public void matarDormidos() {
        //TODO
    }

    public void recibirJugadores() {
        //TODO
    }

    public boolean pedirConfirmacion(Cliente c) {
        //TODO
        return true;
    }
    
    /**
     * Envia una notificación diciendo que perdio y fue sacado del juego
     * @param perdedores Lista de los perdedores a los cual se va a notificar
     */
    private void enviarNotificacionPerdio(List<Jugador> perdedores) {
        //TODO
    }
    
    /**
     * Notifica enviando al cliente ganador diciendo que fue el ganador de la partida
     * @param ganador el jugador ganador de la partida
     */
    private void notificarGanador(Jugador ganador) {
        //TODO
    }

    /**
     * Notifica enviando a los clientes ganadores diciendo que fueron los ganadores de la partida
     * @param jugadores Lista de los jugadores ganadores
     */
    private void notificarGanadores(List<Jugador> jugadores) {
        //TODO
    }
    
    /**
     * El cliente designado lo coloca en la cola de espera del nuevo juego
     * @param c Es el cliente que se encola en el servidor del juego
     */
    private void encolarJugador(Cliente c){
        //TODO
    }
    
    /**
     * Acaba El juego por completo y libera todos los recursos para volver a empezar
     */
    private void acabarJuego() {
        //TODO
    }
    /**
     * Envía el estado nuevo del juego luego de una Ronda
     * @param jugadores Lista de los jugadores que estan jugando
     */
    private void enviarEstadoJuego(List<Jugador> jugadores){
        //TODO
    }
    
    /**
     * Saca los jugadores que ya estan muertos de la lista de jugadores, estan muuertos si su vida en menor iguala cero
     * @param jugadores Jugadores que actualmente estan en el juego y que se quieren revisar
     * @return retorna la lista con solo los jugadores que están vivios
     */
    public List<Jugador> sacarMuertos(List<Jugador> jugadores){
        List<Jugador> perdedores = new ArrayList<Jugador>();
        for (Jugador j : jugadores) {
            if(j.getVida() <= 0){
                perdedores.add(j);
                jugadores.remove(j);
            }
        }
        enviarNotificacionPerdio(perdedores);
        return jugadores;
    }

    /**
     * Define si el juego terminó, definomos que se acaba, sí queda un solo jugador,
     * o si todoslos jugadores quedan sin vida, de esta manera todos los jugadores ganan la partida
     * @param jugadores actualmente en el juego
     * @return falso si el juego aun sigue, de lo contrario si el juego terminó retorna true
     */
    private boolean juegoAcabo(List<Jugador> jugadores){
        List<Jugador> auxiliares = jugadores;
        List<Jugador> perdedores = new ArrayList<Jugador>();
        for (Jugador j : auxiliares) {
             if(j.getVida() <= 0){
                perdedores.add(j);
                jugadores.remove(j);
            }
        }
        if(auxiliares.size() == 1){
            enviarNotificacionPerdio(perdedores);
            Jugador ganador = jugadores.get(0);
            notificarGanador(ganador);
            acabarJuego();
            return true;
        }else if(auxiliares.size() == 0){
            notificarGanadores(jugadores);
            acabarJuego();
            return true;
        }
        return false;
    }    
}
