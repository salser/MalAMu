package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServiciosComunicacion {

	public static final int PUERTO = 7896;

	/**
	 * Envía un mensaje y luego recibe una respuesta.
	 * 
	 * @param socket socket mediante el cual se realiza la comunicación.
	 * @param mensaje mensaje que se envía como petición.
	 * 
	 * @return objeto recibido como respuesta.
	 */
	public static Object enviarYRecibirRespuestaTCP(Socket socket, Object mensaje) {
		// Envía petición
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(mensaje);
			System.out.println("Received: " + mensaje);
		} catch (UnknownHostException e) {
			System.out.println("Socket:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			System.out.println("readline:" + e.getMessage());
		}
		
		// Recibe respuesta
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			return in.readObject();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Envía un objeto serializado mediante un socket.
	 *
	 * @param socket socket mediante el cual se va a enviar el mensaje.
	 * @param mensaje mensaje que se va a enviar.
	 */
	public static void enviarTCP(Socket socket, Object mensaje) {
		// Pool de hilos que crea un solo hilo nuevo
		ExecutorService es = Executors.newSingleThreadExecutor();
		
		// Crear un hilo que envíe el mensaje
		Runnable hilo = () -> {
			try {
				// Enviar el mensaje por el socket
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(mensaje);
				System.out.println("Received: " + mensaje);
			} catch (UnknownHostException e) {
				System.out.println("Socket:" + e.getMessage());
			} catch (EOFException e) {
				System.out.println("EOF:" + e.getMessage());
			} catch (IOException e) {
				System.out.println("readline:" + e.getMessage());
			}
		};
		
		// Ejecutar hilo
		es.submit(hilo);
	}

	/**
	 * Envía un objeto mediante cada socket en sockets.
	 *
	 * @param sockets sockets por dodne se va a enviar el mensaje.
	 * @param mensaje mensaje que se va a enviar por todos los sockets.
	 */
	public static void enviarTCP(List<Socket> sockets, Object mensaje) {
		// Pool de hilos que maximiza uso del procesador
		ExecutorService es = Executors.newWorkStealingPool(sockets.size());
		// Para cada socket...
		for (Socket socket : sockets) {
			// ...crear un hilo que envie el mensaje
			Runnable hilo = () -> {
				try {
					// Enviar el mensaje por el socket
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(mensaje);
					System.out.println("Received: " + mensaje);
				} catch (UnknownHostException e) {
					System.out.println("Socket:" + e.getMessage());
				} catch (EOFException e) {
					System.out.println("EOF:" + e.getMessage());
				} catch (IOException e) {
					System.out.println("readline:" + e.getMessage());
				}
			};
			
			// Ejecutar hilo
			es.submit(hilo);
		}
	}

	/**
	 * Envía objetos de una lista mediante cada sockets de una lista.
	 *
	 * @param sockets lista de sockets por donde se van enviar los mensajes.
	 * @param mensajes lista con cada mensaje que se quiere enviar por cada socket.
	 */
	public static void enviarTCP(List<Socket> sockets, List<Object> mensajes) {
		// Pool de hilos que maximiza uso del procesador
		ExecutorService es = Executors.newWorkStealingPool(sockets.size());
		
		// Para cada socket...
		for (int i = 0; i < sockets.size(); i++) {
			Socket socket = sockets.get(i);
			Object mensaje = mensajes.get(i);
			// ...crear un hilo que envíe el mensaje que le corresponde
			Runnable hilo = () -> {
				try {
					// Enviar el mensaje correspondiente por el socket correspondiente
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(mensaje);
					System.out.println("Received: " + mensaje);
				} catch (UnknownHostException e) {
					System.out.println("Socket:" + e.getMessage());
				} catch (EOFException e) {
					System.out.println("EOF:" + e.getMessage());
				} catch (IOException e) {
					System.out.println("readline:" + e.getMessage());
				}
			};
			
			// Ejecutar hilo
			es.submit(hilo);
		}
	}

	/**
	 * Escucha PUERTO y crea nuevas conexiones con clientes que realizan
	 * peticiones en este.
	 *
	 * @param cola cola bloqueante donde se guardan objetos conexión obtenidos.
	 * 
	 * @return pool del hilo que escucha peticiones nuevas.
	 */
	public static ExecutorService recibirTCP(BlockingQueue<Conexion> cola) {
		try {
			// ServerSocket que escucha peticiones en PUERTO
			ServerSocket listenSocket = new ServerSocket(PUERTO);

			// Pool de hilos que crea un solo hilo nuevo
			ExecutorService esBase = Executors.newSingleThreadExecutor();

			// Hilo que escucha peticiones
			Runnable hiloEscucha = () -> {
				// Pool de hilos que maximiza uso del procesador
				ExecutorService es = Executors.newWorkStealingPool();
				while (!Thread.interrupted()) {
					try {
						// Bloquea hasta que llegue una petición y crea un socket para leerla cuando llega
						Socket clientSocket = listenSocket.accept();

						// Hilo que lee la petición en el nuevo socket
						// y guarda la conexión (el socket + el objeto recibido)
						// en la cola bloqueante
						Runnable hilo = new Runnable() {
							private Socket s;
							private BlockingQueue<Conexion> q;

							// Permite inicializar la clase anónima dado que java no permite definir un constructor
							private Runnable init(Socket s, BlockingQueue<Conexion> q) {
								this.s = s;
								this.q = q;
								return this;
							}

							// Función que corre en un hilo aparte
							@Override
							public void run() {
								try {
									// Leer objeto, crear conexion y encolar
									ObjectInputStream in = new ObjectInputStream(s.getInputStream());
									q.put(new Conexion(s, in.readObject()));
								} catch (IOException e) {
									System.out.println("Connection:" + e.getMessage());
								} catch (ClassNotFoundException ex) {
									Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
								} catch (InterruptedException ex) {
									Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						}.init(clientSocket, cola);
						
						// Ejecutar hilo
						es.submit(hilo);
					} catch (IOException ex) {
						Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			};
			
			// Ejecutar hilo
			esBase.submit(hiloEscucha);
			
			return esBase;
		} catch (IOException e) {
			System.out.println("Listen socket:" + e.getMessage());
		}
		return null;
	}

	/**
	 * Recibe un objeto mediante un socket.
	 *
	 * @param socket socket mediante el cual se va a recibir el objeto.
	 * 
	 * @return el objeto que se recibió.
	 */
	public static Object recibirTCP(Socket socket) {
		try {
			// Leer objeto y retornar
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			return in.readObject();
		} catch (IOException e) {
			System.out.println("Connection:" + e.getMessage());
		} catch (ClassNotFoundException ex) {
			Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Recibe objetos mediante cada uno de los sockets de la lista.
	 *
	 * @param sockets lista de sockets mediante los cuales se va a recibir.
	 * 
	 * @return lista de objetos recibidos.
	 */
	public static List<Object> recibirTCP(List<Socket> sockets) {
		// Lista donde se guardan los objetos recibidos
		List<Object> recibidos = new ArrayList<>(sockets.size());
		// Lista donde se guardan los objetos Future obtenidos del ExecutorService
		List<Future<?>> tareas = new ArrayList<>();

		// Pool de hilos que maximiza uso del procesador 
		ExecutorService es = Executors.newWorkStealingPool(sockets.size());

		// Para cada socket...
		for (int i = 0; i < sockets.size(); ++i) {
			// ...crear un hilo que escuche reciba un objeto y lo guarde en su posición de la lista
			Runnable hilo = new Runnable() {
				private Socket s;
				private List<Object> r;
				private int pos;

				// Permite inicializar la clase anónima dado que java no permite definir un constructor
				private Runnable init(Socket s, List<Object> r, int pos) {
					this.s = s;
					this.r = r;
					this.pos = pos;
					return this;
				}

				// Función que corre en un hilo aparte
				@Override
				public void run() {
					try {
						// Leer objeto y guardar en la posición correspondiente dentro de la lista
						ObjectInputStream in = new ObjectInputStream(s.getInputStream());
						r.set(pos, (Object) in.readObject());
					} catch (IOException e) {
						System.out.println("Connection:" + e.getMessage());
					} catch (ClassNotFoundException ex) {
						Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}.init(sockets.get(i), recibidos, i);

			// Ejecutar hilo y guardar tarea para esperar su terminación
			tareas.add(es.submit(hilo));
		}
		// Para cada tarea...
		for (Future<?> tarea : tareas) {
			try {
				// ...esperar a que la tarea termine
				tarea.get();
			} catch (InterruptedException ex) {
				Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ExecutionException ex) {
				Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		return recibidos;
	}

	/**
	 * Abre un socket con el servidor que escucha en la dirección dada.
	 *
	 * @param direccionDst dirección donde escucha el servidor.
	 * 
	 * @return el socket que se abrió con el servidor.
	 */
	public static Socket abrirSocketConServidor(InetAddress direccionDst) {
		try {
			// Crear socket y retornar
			Socket socket = new Socket(direccionDst, PUERTO);
			return socket;
		} catch (IOException ex) {
			Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}
}
