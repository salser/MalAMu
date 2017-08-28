package malamu;

import java.net.InetAddress;
import java.time.LocalDate;
import modelo.Jugador;

/**
 * Clase que modela la lógica e información del cliente del juego.
 * @author Juan Espinosa, Henry Salazar, David Villamizar
 */
public class Cliente {
    private InetAddress direccion;
    private LocalDate tiempoUltimoMensaje;
    private LocalDate duracionMaximaInactividad;
    private Jugador jugador;
    private List<Jugada> jugadas;
    private GUICliente gui;

    public Cliente(InetAddress direccion, Jugador jugador, List<Jugada> jugadas, GUICliente gui) {
        this.direccion = direccion;
        this.tiempoUltimoMensaje = LocalDate.now();
        this.duracionMaximaInactividad = LocalDate.now();
        this.jugador = jugador;
        this.jugadas = jugadas;
        this.gui = gui;
    }
    
    
    
    
    
}
