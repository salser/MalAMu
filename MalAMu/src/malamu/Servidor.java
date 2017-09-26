/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package malamu;

import comunicacion.Conexion;
import comunicacion.ServiciosComunicacion;
import static comunicacion.ServiciosComunicacion.PUERTO;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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
	protected int duracionMaximaInactividadMS;

	/**
	 * Tiempo tomado al iniciar el emparejamiento para una nueva partida.
	 */
	protected LocalDateTime tiempoInicioEmparejamiento;

	/**
	 * Tiempo de emparejamiento máximo que se permite antes de iniciar una
	 * partida con los clientes seleccionados hasta ese momento.
	 */
	protected int duracionMaximaEmparejamientoMS;

	/**
	 * Pool de hilos que ejecuta al hilo base de escucha de nuevas peticiones de
	 * clientes.
	 */
	protected ExecutorService esBase;

	/**
	 * Pool de hilos que ejecuta al hilo que reconecta clientes.
	 */
	protected ExecutorService esReconectados;

	/**
	 * Tiene la partida actual.
	 */
	protected Partida partida;

	/**
	 * Lista de los sockets de los clientes que se encuentran en la partida.
	 * Cada posición corresponde con una posición de la lista de clientes de
	 * partida.
	 */
	protected List<Socket> sockets = new ArrayList<>();

	/**
	 * Cola de los clientes que han pedido unirse a una partida.
	 */
	protected BlockingQueue<Conexion> colaClientes = new LinkedBlockingQueue<>();

	/**
	 * Cola de los clientes que intentan reconectarse.
	 */
	protected BlockingQueue<Conexion> colaClientesReconectados = new LinkedBlockingQueue<>();

	/**
	 * Constructor de un servidor que inicia escuchando pedidos de unirse a
	 * partida.
	 *
	 * @param numJugadoresMin la cantidad mínima de jugadores que se necesita
	 * para iniciar una partida.
	 * @param numJugadoresMax la cantidad máxima de jugadores con que se puede
	 * crear una partida.
	 * @param tiempoInicioInactividad el tiempo en que inició la inactividad
	 * actual de este servidor.
	 * @param duracionMaximaInactividadMS la duración máxima de inactividad
	 * permitida con este servidor en milisegundos.
	 * @param tiempoInicioEmparejamiento el tiempo en que inició el
	 * emparejamiento actual.
	 * @param duracionMaximaEmparejamientoMS el tiempo máximo que puede durar el
	 * emparejamiento en milisegundos.
	 */
	public Servidor(int numJugadoresMin, int numJugadoresMax, LocalDateTime tiempoInicioInactividad, int duracionMaximaInactividadMS, LocalDateTime tiempoInicioEmparejamiento, int duracionMaximaEmparejamientoMS) {
		this.numJugadoresMin = numJugadoresMin;
		this.numJugadoresMax = numJugadoresMax;
		this.tiempoInicioInactividad = tiempoInicioInactividad;
		this.duracionMaximaInactividadMS = duracionMaximaInactividadMS;
		this.tiempoInicioEmparejamiento = tiempoInicioEmparejamiento;
		this.duracionMaximaEmparejamientoMS = duracionMaximaEmparejamientoMS;
		this.esBase = null;
		this.esReconectados = null;
		recibirJugadoresNuevos();
		recibirJugadoresReconectados();
	}

	public static void main(String[] args) {
		Servidor servidor;
		servidor = new Servidor(2, 12, null, 60000, null, 60000);
		while (true) {
			servidor.iniciarPartida();
		}
	}

	/**
	 * Pone al servidor a escuchar peticiones de inicio de partida.
	 */
	public void recibirJugadoresNuevos() {
		if (esBase != null) {
			esBase.shutdownNow();
		}
		esBase = ServiciosComunicacion.recibirTCP(colaClientes, colaClientesReconectados);
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

				try {
					// Ajustar timeout del socket
					socket.setSoTimeout(duracionMaximaInactividadMS);

					// Generar código único de acceso para el cliente
					UUID codigo = UUID.randomUUID();
					UUID codigoJugador = UUID.randomUUID();
					cliente.setCodigoAcceso(codigo);

					// Si confirmó correctamente
					if (pedirConfirmacion(cliente, socket)) {
						// Limpiar objeto cliente
						cliente = new Cliente(cliente.getDireccionServidor(), new Jugador(cliente.getJugador().getNombre()));
						cliente.getJugador().setId(codigoJugador);
						cliente.setCodigoAcceso(codigo);

						// Guardar la conexión
						sockets.add(socket);
						clientes.add(cliente);
						numConfirmados++;
					}
				} catch (SocketException ex) {
					Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
				}
			} catch (InterruptedException ex) {
				Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		// Generar lista de jugadores
		List<Jugador> jugadores = partida.getJugadores();

		enviarResultados(jugadores);

		while (!partida.getClientes().isEmpty()) {

			System.out.println("JUGADORES:" + jugadores);
			Ronda ronda = iniciarRonda();

			ejecutarRonda(ronda);

			// Actualizar lista de jugadores
			jugadores = partida.getJugadores();
			

			enviarResultados(jugadores);

			desconectarMuertos();

			// Actualizar lista de jugadores
			jugadores = partida.getJugadores();
		}
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
		// TODO revisar si soporta que el cliente se reconecte
		try {
			// Pedir confirmación al cliente
			ServiciosComunicacion.enviarTCP(socket, cliente);
			cliente = (Cliente) ServiciosComunicacion.recibirTCP(socket);
		} catch (SocketTimeoutException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		} catch (IOException ex) {
			Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
			return false;
		}

		// Resultado confirmación
		return cliente != null && cliente.getDireccionServidor() != null && cliente.getJugador() != null && cliente.getJugador().getNombre() != null;
	}

	/**
	 * Crea una nueva ronda, a partir de jugadas recibidas desde los clientes.
	 *
	 * @return la ronda creada.
	 */
	public Ronda iniciarRonda() {
		Ronda ronda = new Ronda();

		List<Cliente> clientes = (List<Cliente>) (List<?>) ServiciosComunicacion.recibirTCP(sockets);

		// Recibir una jugada de cada cliente
		List<Jugada> jugadas = (List<Jugada>) (List<?>) ServiciosComunicacion.recibirTCP(sockets);
		
		// Para cada jugada...
		for (int i = 0; i < jugadas.size(); i++) {
			// ...validar si el jugador que hace la jugada es el mismo del cliente que la envió.
			if (
				jugadas.get(i) == null ||
				clientes.get(i) == null ||
				clientes.get(i).getCodigoAcceso() == null ||
				!clientes.get(i).getCodigoAcceso().equals(partida.getClientes().get(i).getCodigoAcceso()) ||
				jugadas.get(i).getJugador() == null ||
				jugadas.get(i).getJugador().getId() == null ||
				!jugadas.get(i).getJugador().equals(partida.getClientes().get(i).getJugador()) ||
				(
					jugadas.get(i).getTipo().equals(TipoAccion.ATAQUE) &&
					(
						jugadas.get(i).getPosObjetivo() >= jugadas.size() ||
						jugadas.get(i).getPosObjetivo() < -1
					)
				)
			) {
				jugadas.set(i, null);
			} else {
				clientes.get(i).setJugador(jugadas.get(i).getJugador());
			}
		}
		

		jugadas = matarDormidos(jugadas);

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
		System.out.println("JUGADAS:" + jugadas);
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
				
				Jugador objetivo = partida.getClientes().get(jugada.getPosObjetivo()).getJugador();
				jugador.gastarCargaAtaque();

				// Si el objetivo no se esta defendiendo
				if (!defienden.contains(objetivo)) {
					// Hacer daño
					objetivo.recibirDano(jugador.getDanoAtaque());
				}
			}
		}
		System.out.println("JUGADAS A EJECUTAR:" + jugadas);
		System.out.println("JUGADORES:" + partida.getJugadores());
		List<Jugador> jugadores = new ArrayList<>();
		for (Jugada jugada : jugadas) {
			jugadores.add(jugada.getJugador());
		}
		partida.getRondas().add(ronda);
	}

	/**
	 * Envía última ronda y estado actual de los jugadores en juego a los
	 * clientes.
	 */
	public void enviarResultados(List<Jugador> jugadores) {
		ServiciosComunicacion.enviarTCP(sockets, (List<Object>) (List<?>) jugadores);
		ServiciosComunicacion.enviarTCP(sockets, (Object) jugadores);
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
					System.out.println("CERRAR SOCKET: " + i);
					sockets.get(i).close();
				} catch (IOException ex) {
					Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
				}
				sockets.remove(i);
			}
		}
	}

	/**
	 * Hace que el servidor maneje peticiones de reconexión.
	 */
	public void recibirJugadoresReconectados() {
		// TODO Arreglar timeouts

		// Pool de hilos que crea un solo hilo nuevo
		ExecutorService esBase = Executors.newSingleThreadExecutor();

		if (partida != null) {
			// Hilo que escucha peticiones
			Runnable hiloEscucha = new Runnable() {
				private List<Cliente> cs;

				// Permite inicializar la clase anónima dado que java no permite definir un constructor
				private Runnable init(List<Cliente> cs) {
					this.cs = cs;
					return this;
				}

				@Override
				public void run() {
					// Reconectar clientes
					while (!Thread.interrupted()) {
						try {
							Conexion conexion = null;

							// Sacar de la cola
							System.out.println("Sacando.");
							conexion = colaClientes.take();
							System.out.println("Sacado.");

							Cliente cliente = (Cliente) conexion.objeto;
							Socket socket = conexion.socket;

							try {
								// Ajustar timeout del socket
								socket.setSoTimeout(duracionMaximaInactividadMS);

								// Buscar código de acceso de cliente
								boolean clienteValido = false;
								int indCliente = -1;
								for (int i = 0; i < cs.size(); i++) {
									if (cs.get(i).getCodigoAcceso().equals(cliente.getCodigoAcceso())) {
										if (cs.get(i).getJugador().equals(cliente.getJugador())) {
											indCliente = i;
											clienteValido = true;
										} else {
											clienteValido = false;
										}
										break;
									}
								}

								if (clienteValido) {
									// Guardar la conexión
									sockets.set(indCliente, socket);
								}
							} catch (SocketException ex) {
								Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
							}
						} catch (InterruptedException ex) {
							Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
						}
					}
				}
			}.init(partida.getClientes());
		}
	}

	/**
	 * Hace que el servidor ignore peticiones de reconexión.
	 */
	public void ignorarJugadoresReconectados() {
		if (esReconectados != null) {
			esReconectados.shutdownNow();
		}
		esReconectados = null;
	}

	private List<Jugada> matarDormidos(List<Jugada> jugadas) {
		List<Jugada> jugadas1 = new ArrayList<>(jugadas);
		List<Jugador> jugadores = partida.getJugadores();
		for (int i = jugadores.size() - 1; i >= 0; i--) {
			Jugada jugada = jugadas1.get(i);
			if (jugada == null) {
				partida.getClientes().remove(i);
				jugadas.remove(i);
				try {
					System.out.println("CERRAR SOCKET: " + i);
					sockets.get(i).close();
				} catch (IOException ex) {
					Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
				}
				sockets.remove(i);
			}
		}
		return jugadas;
	}
}
