package malamu;

import comunicacion.ServiciosComunicacion;
import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
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
	 * Atributo que representa la dirección IP del servidor con el que se comunica este cliente.
	 */
	private InetAddress direccionServidor;
	/**
	 * Valor en el que se almacena la última.
	 */
	private LocalDate tiempoUltimoMensaje;

	/**
	 * Valor que representa la mayor cantidad de tiempo que el cliente espera a 
	 * que el servidor envie un paquete.
	 */
	private int duracionMaximaInactividadMS;

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
	private UUID codigoAcceso;
        
	/**
	 * Socket de conexion con el servidor,
	 */
	protected transient Socket socket;

	/**
	 * Constructor de un cliente.
	 *
	 * @param direccionServidor direccion IP del servidor con que se conecta este cliente.
	 * @param jugador jugador que maneja este cliente.
	 */
	public Cliente(InetAddress direccionServidor, Jugador jugador) {
		this.direccionServidor = direccionServidor;
		this.tiempoUltimoMensaje = null;
		this.duracionMaximaInactividadMS = 30000;
		this.jugador = jugador;
		this.ultimaJugada = null;
		this.ultimaRonda = null;
		this.codigoAcceso = null;
	}

	public static void main(String[] args) {
		Cliente cliente;
		try {
			cliente = new Cliente(InetAddress.getByName("127.0.0.1"), new Jugador("David"));            
			boolean terminado = false;
			while (!terminado) {
				try {
					cliente.iniciarPartida();
					terminado = true;
				} catch (IOException ex1) {
					Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex1);
					terminado = false;
				}
			}
		} catch (UnknownHostException ex) {
			Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Método que se invoca para unirse a una partida.
	 * @throws java.io.IOException
	 */
	public void iniciarPartida() throws IOException {
		// Iniciar conexión con el servidor
		socket = ServiciosComunicacion.abrirSocketConServidor(direccionServidor);

		// Enviar objeto cliente al servidor
		ServiciosComunicacion.enviarTCP(socket, this);

		// Recibir confirmación y codigo de acceso del servidor
		socket.setSoTimeout(duracionMaximaInactividadMS);
		Cliente recepcion;
		
		recepcion =	(Cliente) ServiciosComunicacion.recibirTCP(socket);
		this.codigoAcceso = recepcion.getCodigoAcceso();
		this.jugador = recepcion.getJugador();
		
		System.out.println(this.codigoAcceso);
	}

	/**
	 * Método que se invoca para confirmarle al servidor que el cliente decide
	 * conectarse
	 *
	 * @param res Decisión del jugador.
	 */
	public void responderConfirmacion(boolean res) throws IOException {
		if (res) {
			// Responder al servidor
			ServiciosComunicacion.enviarTCP(socket, this);
		} else {
			cerrarConexion();
		}
	}
	
	
	/**
	 * Método que se invoca para solicitar a la partida los resultados de la
	 * ronda anterior.
	 * @return 
	 */
	public List<Jugador> recibirResultados() {
		boolean terminado = false;
		while (!terminado) {
			try {
				this.jugador = (Jugador)ServiciosComunicacion.recibirTCP(socket);
				List<Jugador> resultadoJugadores = (List<Jugador>)ServiciosComunicacion.recibirTCP(socket);
				resultadoJugadores.remove(this.jugador);
				return resultadoJugadores;
			} catch (SocketTimeoutException e) {
				this.cerrarConexion();
				terminado = true;
			} catch (IOException ex) {
				Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);

				// Iniciar conexión con el servidor
				socket = ServiciosComunicacion.abrirSocketConServidor(direccionServidor);
				terminado = false;
			}
		}
		return null;
	}

	/**
	 * Método que se invoca para que el jugador envíe una jugada a la partida.
	 * @param jugada Decision tomada por el usuario.
	 */
	public void enviarJugada(Jugada jugada) {
		ultimaJugada = jugada;
		
		boolean terminado = false;
		while (!terminado) {
			try {
				ServiciosComunicacion.enviarTCP(socket, this);
				ServiciosComunicacion.enviarTCP(socket, jugada);
				terminado = true;
			} catch (IOException ex) {
				Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);

				// Iniciar conexión con el servidor
				socket = ServiciosComunicacion.abrirSocketConServidor(direccionServidor);
				terminado = false;
			}
		}
	}

    /**
	 * Metodo que cierra conexion para que un cliente pueda reiniciarse.
	 */    
	public void cerrarConexion(){
		if(socket != null) {
			try {    
				socket.close();
			} catch (IOException ex) {
				Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}
                
	public Jugador getJugador() {
		return jugador;
	}

	public void setJugador(Jugador jugador) {
		this.jugador = jugador;
	}

	public UUID getCodigoAcceso() {
		return codigoAcceso;
	}

	public void setCodigoAcceso(UUID codigoAcceso) {
		this.codigoAcceso = codigoAcceso;
	}
	
	public Ronda getUltimaRonda() {
		return ultimaRonda;
	}

	public InetAddress getDireccionServidor() {
		return direccionServidor;
	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	} 
        
}
