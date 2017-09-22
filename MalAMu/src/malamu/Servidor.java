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
import java.util.Scanner;
import modelo.Jugada;
import modelo.Jugador;
import modelo.Partida;
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

    public List<Jugador> ejecutarRonda(Ronda ronda) {
        // Inicio ronda
        List<Jugada> jugadas = new ArrayList();
        Scanner in = new Scanner(System.in);
        // Receibir jugadas
        for (int i = 0; i < jugadores.size(); i++) {
            System.out.println("Turno de " + jugadores.get(i).getNombre());
            System.out.println("Indique 1 para atacar 2 para defender 3 para cargar");
            int jugada = in.nextInt();
            switch (jugada) {
                case 1:
                    System.out.println("hacia que jugador desea atacar");
                    for (int j = 0; j < jugadores.size(); j++) {
                        System.out.println((j+1) + ". " + jugadores.get(j).getNombre() + ".");
                    }
                    int indObjetivo = in.nextInt() - 1;
                    if (jugadores.get(i).puedeAtacar()) {
                        jugadas.add(new Jugada(jugadores.get(i), jugadores.get(indObjetivo), TipoAccion.ATAQUE));
                    }
                    break;
                case 2:
                    if (jugadores.get(i).puedeDefenderse()) {
                        jugadas.add(new Jugada(jugadores.get(i), null, TipoAccion.DEFENSA));
                    }
                    break;
                case 3:
                    if (jugadores.get(i).puedeRecargar()) {
                        jugadas.add(new Jugada(jugadores.get(i), null, TipoAccion.RECARGA));
                    }
                    break;
                default:
                    System.out.println("Turno de " + jugadores.get(i).getNombre() + " otravez por que no dio donde era!");
                    i--;
                    break;

            }
        }

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
                defienden.add(jugada.getJugador());
            }
        }

        // Imprimir jugadores que se defendieron
        System.out.println("-------------------");
        System.out.println("   Se defienden");
        System.out.println("-------------------");
        for (Jugador jugador : defienden) {
            System.out.println(jugador.getNombre());
        }
        System.out.println("-------------------");

        // Ejecutar jugadas de ataque
        for (Jugada jugada : jugadas) {
            if (jugada.getTipo() == TipoAccion.ATAQUE) {
                if (!defienden.contains(jugada.getObjetivo())) {
                    // Si el objetivo no se esta defendiendo
                    // Hacer daño
                    jugada.getObjetivo().recibirDano(jugada.getJugador().getDanoAtaque());
                }
            }
        }

        // Terminar juego si hay muertos
        for (int i = 0; i < jugadores.size(); i++) {
            if (jugadores.get(i).getVida() <= 0) {
                seAcabo = true;
            }
        }

        // Imprimir nuevo estado de los jugadores
        for (Jugador jugador : jugadores) {
            System.out.println(jugador);
        }
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
