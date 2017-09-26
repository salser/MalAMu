package modelo;

import java.io.Serializable;

/**
 * Clase que modela una jugada especifica que un jugador hace en su turno
 *
 * @author Henry Salazar, David Villamizar, Juan Espinosa
 */
public class Jugada implements Serializable {

	/**
	 * jugador: Es el jugador destino de la jugada desde donde va el ataque o
	 * donde se recarga o protege
	 */
	private Jugador jugador;
	/**
	 * objetivo: En el caso de ser el tipo de jugada atacar, este será el
	 * objetivo que tiene el jugador descrito arriba
	 */
	private int posObjetivo;
	/**
	 * TipoAccion: define el tipo de acción que el jugador va a jugar en su
	 * turno
	 */
	private TipoAccion tipo;

	/**
	 * Constructor de una Jugada.
	 *
	 * @param jugador
	 * @param posObjetivo
	 * @param tipo
	 */
	public Jugada(Jugador jugador, int posObjetivo, TipoAccion tipo) {
		this.jugador = jugador;
		this.tipo = tipo;
		if (!tipo.equals(TipoAccion.ATAQUE)) {
			this.posObjetivo = -1;
		} else {
			this.posObjetivo = posObjetivo;
		}
	}

	public Jugador getJugador() {
		return jugador;
	}

	public int getPosObjetivo() {
		return posObjetivo;
	}

	public TipoAccion getTipo() {
		return tipo;
	}

	@Override
	public String toString() {
		return "Jugada{" + "jugador=" + jugador + ", posObjetivo=" + posObjetivo + ", tipo=" + tipo + '}';
	}

	

	
}
