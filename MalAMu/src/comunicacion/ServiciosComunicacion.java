package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import malamu.Cliente;

public class ServiciosComunicacion {

	public static final int PUERTO = 7896;

	/**
	 * Envía un objeto serializado mediante un socket.
	 *
	 * @param socket socket mediante el cual se va a enviar el mensaje.
	 * @param mensaje mensaje que se va a enviar.
	 */
	public static void enviarTCP(Socket socket, Object mensaje) throws IOException {
		if (socket == null) {
			System.out.println("SOCKET ES NULL");
			return;
		}
		try {
			// Enviar el mensaje por el socket
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			out.writeObject(mensaje);
			System.out.println("Received: " + mensaje);
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout:" + e.getMessage());
		} catch (UnknownHostException e) {
			System.out.println("Socket:" + e.getMessage());
		} catch (EOFException e) {
			System.out.println("EOF:" + e.getMessage());
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * Envía un objeto mediante cada socket en sockets.
	 *
	 * @param sockets sockets por dodne se va a enviar el mensaje.
	 * @param mensaje mensaje que se va a enviar por todos los sockets.
	 */
	public static void enviarTCP(List<Socket> sockets, Object mensaje) {
		System.out.println("TAMAÑO LISTA SOCKETS:" + sockets.size());
		if (sockets == null || sockets.size() <= 0) {
			return;
		}
		// Pool de hilos que maximiza uso del procesador
		ExecutorService es = Executors.newWorkStealingPool(sockets.size());
		
		// Lista de tareas para esperar respuesta de los hilos
		List<Future<?>> tareas = new ArrayList<>();
		
		// Para cada socket...
		for (Socket socket : sockets) {
			// ...crear un hilo que envie el mensaje
			Runnable hilo = () -> {
				try {
					// Enviar el mensaje por el socket
					ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
					out.writeObject(mensaje);
					System.out.println("Received: " + mensaje);
				} catch (SocketTimeoutException e) {
					System.out.println("Timeout:" + e.getMessage());
				} catch (UnknownHostException e) {
					System.out.println("Socket:" + e.getMessage());
				} catch (EOFException e) {
					System.out.println("EOF:" + e.getMessage());
				} catch (IOException e) {
					System.out.println("readline:" + e.getMessage());
				}
			};

			// Ejecutar hilo y guardar tarea
			Future<?> tarea = es.submit(hilo);
			tareas.add(tarea);
		}
		
		// Para cada tarea...
		for (Future<?> tarea : tareas) {
			try {
				// ...esperar a que termine
				tarea.get();
			} catch (InterruptedException ex) {
				Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ExecutionException ex) {
				Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		es.shutdown();
	}

	/**
	 * Envía objetos de una lista mediante cada sockets de una lista.
	 *
	 * @param sockets lista de sockets por donde se van enviar los mensajes.
	 * @param mensajes lista con cada mensaje que se quiere enviar por cada
	 * socket.
	 */
	public static void enviarTCP(List<Socket> sockets, List<Object> mensajes) {
		System.out.println("TAMAÑO LISTA SOCKETS:" + sockets.size());
		if (sockets == null || sockets.size() <= 0) {
			return;
		}
		// Pool de hilos que maximiza uso del procesador
		ExecutorService es = Executors.newWorkStealingPool(sockets.size());

		// Lista de tareas para esperar respuesta de los hilos
		List<Future<?>> tareas = new ArrayList<>();
		
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
				} catch (SocketTimeoutException e) {
					System.out.println("Timeout:" + e.getMessage());
				} catch (UnknownHostException e) {
					System.out.println("Socket:" + e.getMessage());
				} catch (EOFException e) {
					System.out.println("EOF:" + e.getMessage());
				} catch (IOException e) {
					System.out.println("readline:" + e.getMessage());
				}
			};

			// Ejecutar hilo y guardar tarea
			Future<?> tarea = es.submit(hilo);
			tareas.add(tarea);
		}
		
		// Para cada tarea...
		for (Future<?> tarea : tareas) {
			try {
				// ...esperar a que termine
				tarea.get();
			} catch (InterruptedException ex) {
				Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
			} catch (ExecutionException ex) {
				Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		es.shutdown();
	}

	/**
	 * Escucha PUERTO y crea nuevas conexiones con clientes que realizan
	 * peticiones en este, o reconecta clientes que piden reconectarse.
	 *
	 * @param cola cola bloqueante donde se guardan objetos conexión recibidos de clientes que piden iniciar partida.
	 * @param colaReconectados cola bloqueante donde se guardan objetos conexión recibidos de clientes que piden reconexión.
	 *
	 * @return pool del hilo que escucha peticiones nuevas.
	 */
	public static ExecutorService recibirTCP(BlockingQueue<Conexion> cola, BlockingQueue<Conexion> colaReconectados) {
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
							private BlockingQueue<Conexion> qr;

							// Permite inicializar la clase anónima dado que java no permite definir un constructor
							private Runnable init(Socket s, BlockingQueue<Conexion> q, BlockingQueue<Conexion> qr) {
								this.s = s;
								this.q = q;
								this.qr = qr;
								return this;
							}

							// Función que corre en un hilo aparte
							@Override
							public void run() {
								try {
									// Leer objeto, crear conexion y encolar
									ObjectInputStream in = new ObjectInputStream(s.getInputStream());
									Object o = in.readObject();
									if (o instanceof Cliente) {
										q.put(new Conexion(s, o));
									} else if (o instanceof Conexion) {
										qr.put(new Conexion(s, ((Conexion) o).objeto));
									}
								} catch (SocketTimeoutException e) {
									System.out.println("Timeout:" + e.getMessage());
								} catch (IOException e) {
									System.out.println("Connection:" + e.getMessage());
								} catch (ClassNotFoundException ex) {
									Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
								} catch (InterruptedException ex) {
									Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
								}
							}
						}.init(clientSocket, cola, colaReconectados);

						// Ejecutar hilo
						es.submit(hilo);
					} catch (IOException ex) {
						Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
				es.shutdown();
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
	 * 
	 * @throws SocketTimeoutException cuando el timeout del socket se cumple antes de recibir un mensaje.
	 */
	public static Object recibirTCP(Socket socket) throws SocketTimeoutException, IOException {
		if (socket == null) {
			System.out.println("SOCKET ES NULL");
			return null;
		}
		try {
			// Leer objeto y retornar
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			return in.readObject();
		} catch (SocketTimeoutException e) {
			throw e;
		} catch (IOException e) {
			throw e;
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
		List<Object> recibidos = new ArrayList<>(Collections.nCopies(sockets.size(), null));
		// Lista donde se guardan los objetos Future obtenidos del ExecutorService
		List<Future<?>> tareas = new ArrayList<>();

		System.out.println("TAMAÑO LISTA SOCKETS:" + sockets.size());
		if (sockets == null || sockets.size() <= 0) {
			return recibidos;
		}
		
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
					} catch (SocketTimeoutException e) {
						r.set(pos, null);
						System.out.println("Timeout:" + e.getMessage());
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
		es.shutdown();
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
