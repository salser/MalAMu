package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import malamu.Cliente;
import malamu.Servidor;

public class ServiciosComunicacion {

    public static final int PUERTO = 7896;

    /**
     * Envía un objeto serializado mediante un socket.
     *
     * @param socket
     * @param mensaje
     */
    public static void enviarTCP(Socket socket, Object mensaje) {
        ExecutorService es = Executors.newSingleThreadExecutor();
        Runnable hilo = () -> {
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
        };
        es.submit(hilo);
    }

    /**
     * Envía un objeto serializado mediante cada socket en sockets.
     *
     * @param sockets
     * @param mensaje
     */
    public static void enviarTCP(List<Socket> sockets, Object mensaje) {
        ExecutorService es = Executors.newWorkStealingPool(sockets.size());
        for (Socket socket : sockets) {
            Runnable hilo = () -> {
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
            };
            es.submit(hilo);
        }
    }

    /**
     * Envía objetos serializados de una lista mediante cada socket en la misma
     * posición en sockets.
     *
     * @param sockets
     * @param mensajes
     */
    public static void enviarTCP(List<Socket> sockets, List<Object> mensajes) {
        ExecutorService es = Executors.newWorkStealingPool(sockets.size());
        for (int i = 0; i < sockets.size(); i++) {
            Socket socket = sockets.get(i);
            Object mensaje = mensajes.get(i);
            Runnable hilo = () -> {
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
            };
            es.submit(hilo);
        }
    }

    /**
     * Recibe objetos serializados mediante un socket que escucha PUERTO, y los
     * guarda en la cola dada.
     *
     * @param socket
     * @param cola
     * @return
     */
    public static void recibirTCP(ServerSocket serverSocket, ConcurrentLinkedQueue<Object> cola) {
        try {
            ServerSocket listenSocket = new ServerSocket(PUERTO);
            ExecutorService esBase = Executors.newSingleThreadExecutor();
            Runnable hiloEscucha = () -> {
                ExecutorService es = Executors.newWorkStealingPool();
                while (true) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        Runnable hilo = new Runnable() {
                            private Socket s;
                            private ConcurrentLinkedQueue<Object> q;
                            
                            private Runnable init(Socket s, ConcurrentLinkedQueue<Object> q) {
                                this.s = s;
                                this.q = q;
                                return this;
                            }
                            
                            @Override
                            public void run() {
                                try {
                                    ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                                    q.add(in.readObject());
                                } catch (IOException e) {
                                    System.out.println("Connection:" + e.getMessage());
                                } catch (ClassNotFoundException ex) {
                                    Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }.init(clientSocket, cola);
                        es.submit(hilo);
                    } catch (IOException ex) {
                        Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            };
            esBase.submit(hiloEscucha);
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }

    /**
     * Recibe un objeto serializado mediante un socket.
     *
     * @param socket
     * @return
     */
    public static Object recibirTCP(Socket socket) {
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
     * Recibe objectos serializados mediante cada uno de los sockets de la
     * lista.
     *
     * @param sockets
     * @return
     */
    public static List<Object> recibirTCP(List<Socket> sockets) {
        List<Object> recibidos = new ArrayList<>(sockets.size());
        List<Future<?>> tareas = new ArrayList<>();
        ExecutorService es = Executors.newWorkStealingPool(sockets.size());
        for (int i = 0; i < sockets.size(); ++i) {
            Runnable hilo = new Runnable() {
                private Socket s;
                private List<Object> r;
                private int pos;

                private Runnable init(Socket s, List<Object> r, int pos) {
                    this.s = s;
                    this.r = r;
                    this.pos = pos;
                    return this;
                }

                @Override
                public void run() {
                    try {
                        ObjectInputStream in = new ObjectInputStream(s.getInputStream());
                        r.set(pos, (Object) in.readObject());
                    } catch (IOException e) {
                        System.out.println("Connection:" + e.getMessage());
                    } catch (ClassNotFoundException ex) {
                        Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.init(sockets.get(i), recibidos, i);
            tareas.add(es.submit(hilo));
        }
        for (Future<?> tarea : tareas) {
            try {
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
     * Establece conexiones mediantes sockets con cada una de las direcciones de
     * la lista.
     *
     * @param direccionesDst
     * @return
     */
    public static List<Socket> abrirSockets(List<InetAddress> direccionesDst) {
        List<Socket> sockets = new ArrayList<>(direccionesDst.size());
        for (int i = 0; i < direccionesDst.size(); i++) {
            try {
                Socket socket = new Socket(direccionesDst.get(i), PUERTO);
                sockets.set(i, socket);
            } catch (IOException ex) {
                Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sockets;
    }
}
