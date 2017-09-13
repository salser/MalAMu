package modelo;

/**
 * Clase que modela una jugada especifica que un jugador hace en su turno
 * @author Henry Salazar, David Villamizar, Juan Espinosa
 */
public class Jugada {
    
    /**
     * jugador: Es el jugador destino de la jugada desde donde va el ataque o donde se recarga o protege
     */
    private Jugador jugador;
    /**
     * objetivo: En el caso de ser el tipo de jugada atacar, este será el objetivo que tiene el jugador descrito arriba
     */
    private Jugador objetivo;
    /**
     * TipoAccion: define el tipo de acción que el jugador va a jugar en su turno
     */
    private TipoAccion tipo;

    /**
     * Constructor de una Jugada
     * @param jugador
     * @param objetivo
     * @param tipo 
     */
    public Jugada(Jugador jugador, Jugador objetivo, TipoAccion tipo) {
        this.jugador = jugador;
        this.objetivo = objetivo;
        this.tipo = tipo;
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Jugador getObjetivo() {
        return objetivo;
    }

    public TipoAccion getTipo() {
        return tipo;
    }
    
    
    
    /**
     * Convierte una jugada a algo entendible
     * @return una jugada en una cadena de caracteres
     */
    @Override
    public String toString() {
        return "Jugada{" + "jugador=" + jugador + ", objetivo=" + objetivo + ", tipo=" + tipo + '}';
    }
    
    
}
