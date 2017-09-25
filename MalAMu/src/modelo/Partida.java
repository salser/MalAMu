package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import malamu.Cliente;

/**
 * Representa una partida en del juego. Guarda información sobre tiempo de
 * inicio, las rondas jugadas hasta ahora y los clientes que están en juego
 * actualmente.
 *
 * @author Juan Espinosa, Henry Salazar, David Villamizar
 */
public class Partida implements Serializable {

	/**
	 * La fecha en que se inició el emparejamiento para esta partida.
	 */
	private LocalDate fechaInicioEmparejamiento;
	
	/**
	 * La fecha en que se inició la partida.
	 */
	private LocalDate fechaInicio;

	/**
	 * Lista de clientes que están conectados en la partida.
	 */
	private List<Cliente> clientes;

	/**
	 * Registro de todas las rondas jugadas hasta el momento.
	 */
	private List<Ronda> rondas;

	/**
	 * Crea una nueva partida con
	 */
	public Partida() {
		this.fechaInicio = LocalDate.now();
		this.fechaInicioEmparejamiento = LocalDate.now();
		this.clientes = new ArrayList<Cliente>();
		this.rondas = new ArrayList<Ronda>();
	}

	public void setFechaInicioEmparejamiento(LocalDate fechaInicioEmparejamiento) {
		this.fechaInicioEmparejamiento = fechaInicioEmparejamiento;
	}
	
	public List<Cliente> getClientes() {
		return clientes;
	}

	public List<Ronda> getRondas() {
		return rondas;
	}

	public List<Jugador> getJugadores() {
		List<Jugador> jugadores = new ArrayList<>();
		for (int i = 0; i < clientes.size(); i++) {
			jugadores.add(clientes.get(i).getJugador());
		}
		
		return jugadores;
	}
}
