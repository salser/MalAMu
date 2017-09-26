package malamu;

import comunicacion.ServiciosComunicacion;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import modelo.Jugada;
import modelo.Jugador;
import modelo.Ronda;

/**
 * Clase que modela la lógica e información del cliente del juego.
 *
 * @author Juan Espinosa, Henry Salazar, David Villamizar
 */
public class Cliente implements Serializable {

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
	 * Última decisión tomada por el jugador.
	 */
	private Jugada ultimaJugada;

	/**
	 * Registro de la información de la última ronda.
	 */
	private Ronda ultimaRonda;

	/**
	 * Código único que le otorga el servidor a cada cliente.
	 */
	private String codigoAcceso;
        

	/**
	 * Constructor de un cliente.
	 *
	 * @param direccion
	 * @param jugador
	 * @param jugadas
	 * @param gui
	 */
	public Cliente(InetAddress direccion, Jugador jugador) {
		this.direccion = direccion;
		this.tiempoUltimoMensaje = null;
		this.duracionMaximaInactividad = Duration.ofMillis(1000);
		this.jugador = jugador;
		this.ultimaJugada = null;
		this.ultimaRonda = null;
		this.codigoAcceso = null;
	}

	public static void main(String[] args) {
		try {
			Cliente cliente = new Cliente(InetAddress.getByName("127.0.0.1"), new Jugador("David"));
			cliente.iniciarPartida();
		} catch (UnknownHostException ex) {
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}
		return;
	}

	/**
	 * Método que se invoca para unirse a una partida.
	 */
	public void iniciarPartida() {
		try {
			// Iniciar conexión con el servidor
			Socket socket = ServiciosComunicacion.abrirSocketConServidor(InetAddress.getByName("127.0.0.1"));

			// Enviar objeto cliente al servidor
			ServiciosComunicacion.enviarTCP(socket, this);

			// Recibir confirmación del servidor
			String pedidoConfirmacion = (String) ServiciosComunicacion.recibirTCP(socket);
			System.out.println(pedidoConfirmacion);

			// Pedir confirmación al usuario
			Scanner in = new Scanner(System.in);
			String respuesta = in.nextLine();
			if (respuesta.equals("Y")) {
				// Responder al servidor
				ServiciosComunicacion.enviarTCP(socket, this);

				// Recibir jugadores
				List<Jugador> jugadores = (List<Jugador>) ServiciosComunicacion.recibirTCP(socket);
				System.out.println("jugadores: " + jugadores);
				// Enviar jugada
			}
			socket.close();
		} catch (UnknownHostException ex) {
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		} catch (IOException ex) {
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}
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

	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	public Ronda getUltimaRonda() {
		return ultimaRonda;
	}

	public InetAddress getDireccion() {
		return direccion;
	}
}
