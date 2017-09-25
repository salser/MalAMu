/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package malamu;

import comunicacion.Conexion;
import comunicacion.ServiciosComunicacion;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Jugada;
import modelo.Jugador;
import modelo.Partida;
import modelo.Ronda;
import modelo.TipoAccion;

/**
 * Responde peticiones de los clientes.
 *
 * @author davl3232
 */
public class Servidor {

	/**
	 * Dirección IP del servidor.
	 */
	protected InetAddress direccion;

	protected int numJugadoresMax;

	protected int numJugadoresMin;

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
	 * Pool de hilos que ejecuta al hilo base de escucha de nuevas peticiones de
	 * clientes.
	 */
	protected ExecutorService esBase;

	/**
	 * Tiene la partida actual.
	 */
	protected Partida partida;

	/**
	 * Lista de los sockets de los clientes que se encuentran en la partida.
	 * Cada posición corresponde con una posición de la lista de clientes de
	 * partida.
	 */
	protected List<Socket> sockets = new ArrayList<Socket>();

	/**
	 * Cola de los clientes que han pedido unirse a una partida.
	 */
	protected BlockingQueue<Conexion> colaClientes = new LinkedBlockingQueue<>();

	/**
	 * Constructor de un servidor que inicia escuchando pedidos de unirse a
	 * partida.
	 *
	 * @param direccion la dirección IP donde se expone el servidor.
	 * @param numJugadoresMin la cantidad mínima de jugadores que se necesita
	 * para iniciar una partida.
	 * @param numJugadoresMax la cantidad máxima de jugadores con que se puede
	 * crear una partida.
	 * @param tiempoInicioInactividad el tiempo en que inició la inactividad
	 * actual de este servidor.
	 * @param duracionMaximaInactividad la duración máxima de inactividad
	 * permitida con este servidor.
	 * @param tiempoInicioEmparejamiento el tiempo en que inició el
	 * emparejamiento actual.
	 * @param duracionMaximaEmparejamiento el tiempo máximo que puede durar el
	 * emparejamiento.
	 */
	public Servidor(InetAddress direccion, int numJugadoresMin, int numJugadoresMax, LocalDateTime tiempoInicioInactividad, Duration duracionMaximaInactividad, LocalDateTime tiempoInicioEmparejamiento, Duration duracionMaximaEmparejamiento) {
		this.direccion = direccion;
		this.numJugadoresMin = numJugadoresMin;
		this.numJugadoresMax = numJugadoresMax;
		this.tiempoInicioInactividad = tiempoInicioInactividad;
		this.duracionMaximaInactividad = duracionMaximaInactividad;
		this.tiempoInicioEmparejamiento = tiempoInicioEmparejamiento;
		this.duracionMaximaEmparejamiento = duracionMaximaEmparejamiento;
		this.esBase = null;
		recibirJugadoresNuevos();
	}

	public static void main(String[] args) {
		Servidor servidor;
		try {
			servidor = new Servidor(InetAddress.getByName("127.0.0.1"), 4, 12, null, Duration.ofMillis(1000), null, Duration.ofMillis(0));
			while (true) {
				servidor.iniciarPartida();
			}
		} catch (UnknownHostException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Pone al servidor a escuchar peticiones de inicio de partida.
	 */
	public void recibirJugadoresNuevos() {
		if (esBase != null) {
			esBase.shutdownNow();
		}
		esBase = ServiciosComunicacion.recibirTCP(colaClientes);
	}

	/**
	 * Hace que el servidor ignore peticiones de inicio de partida.
	 */
	public void ignorarJugadoresNuevos() {
		if (esBase != null) {
			esBase.shutdownNow();
		}
		esBase = null;
	}

	/**
	 * Inicia una partida nueva. Saca clientes de la cola de clientes y les pide
	 * confirmación de su intención de unirse a partida.
	 */
	public void iniciarPartida() {
		// Crear la partida
		partida = new Partida();

		List<Cliente> clientes = partida.getClientes();
		sockets = new ArrayList<>();

		// Confirmar clientes
		int numConfirmados = 0;
		while (numConfirmados < numJugadoresMax) {
			try {
				System.out.println("Sacando.");
				Conexion conexion = null;
				// Si ya se tiene la cantidad requeria de jugadores
				if (numConfirmados >= numJugadoresMin) {
					conexion = colaClientes.poll();
					if (conexion == null) {
						System.out.println("Terminado.");
						break;
					}
				} else {
					conexion = colaClientes.take();
				}
				System.out.println("Sacado.");

				Cliente cliente = (Cliente) conexion.objeto;
				Socket socket = conexion.socket;

				// Si confirmó correctamente
				if (pedirConfirmacion(cliente, socket)) {
					// Limpiar objeto cliente
					cliente = new Cliente(cliente.getDireccion(), new Jugador(cliente.getJugador().getNombre()));

					// Guardar la conexión
					sockets.add(socket);
					clientes.add(cliente);
					numConfirmados++;
				}
			} catch (InterruptedException ex) {
				Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		// Generar lista de jugadores
		List<Jugador> jugadores = partida.getJugadores();

		// Enviar lista inicial de jugadores
		ServiciosComunicacion.enviarTCP(sockets, jugadores);

		Ronda ronda = iniciarRonda();
		ejecutarRonda(ronda);
		enviarResultados();
		desconectarMuertos();
	}

	/**
	 * Pide confirmación al cliente y valida si el cliente de hecho confirmó
	 * correctamente.
	 *
	 * @param cliente cliente que va a confirmar.
	 * @param socket socket mediante el cual se recibe la confirmación.
	 *
	 * @return true si confirmó correctamente, false de lo contrario.
	 */
	public boolean pedirConfirmacion(Cliente cliente, Socket socket) {
		// Pedir confirmación al cliente
		ServiciosComunicacion.enviarTCP(socket, "¿Desea jugar?");
		cliente = (Cliente) ServiciosComunicacion.recibirTCP(socket);

		// Resultado confirmación
		return cliente != null && cliente.getDireccion() != null && cliente.getJugador() != null && cliente.getJugador().getNombre() != null;
	}

	/**
	 * Crea una nueva ronda, a partir de jugadas recibidas desde los clientes.
	 *
	 * @return la ronda creada.
	 */
	public Ronda iniciarRonda() {
		Ronda ronda = new Ronda();

		// Recibir una jugada de cada cliente
		List<Jugada> jugadas = (List<Jugada>) (List<?>) ServiciosComunicacion.recibirTCP(sockets);
		ronda.setJugadas(jugadas);

		return ronda;
	}

	/**
	 * Ejecuta una ronda del juego.
	 *
	 * @param ronda la ronda a ejecutar.
	 */
	public void ejecutarRonda(Ronda ronda) {
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

				// Si el objetivo no se esta defendiendo
				if (!defienden.contains(objetivo)) {
					// Hacer daño
					objetivo.recibirDano(jugador.getDanoAtaque());
				}
			}
		}
	}

	/**
	 * Envía última ronda y estado actual de los jugadores en juego a los
	 * clientes.
	 */
	public void enviarResultados() {
		//TODO
	}

	/**
	 * Desconectar clientes que perdieron la partida.
	 */
	public void desconectarMuertos() {
		List<Jugador> jugadores = partida.getJugadores();
		for (int i = jugadores.size() - 1; i >= 0; i--) {
			Jugador jugador = jugadores.get(i);
			if (jugador.getVida() <= 0) {
				partida.getClientes().remove(i);
				try {
					sockets.get(i).close();
				} catch (IOException ex) {
					Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
				}
				sockets.remove(i);
			}
		}
	}
}
