package modelo;

/**
 *
 * @author Henry Salazar, David Villamizar, Juan Espinosa
 */
public class Jugada {
    
    private Jugador jugador;
    private Jugador objetivo;
    private TipoAccion tipo;

    public Jugada(Jugador jugador, Jugador objetivo, TipoAccion tipo) {
        this.jugador = jugador;
        this.objetivo = objetivo;
        this.tipo = tipo;
    }
    
    @Override
    public String toString() {
        return "Jugada{" + "jugador=" + jugador + ", objetivo=" + objetivo + ", tipo=" + tipo + '}';
    }
    
    
}
