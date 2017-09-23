
package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
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
     * @param socket
     * @param mensaje 
     */
    public static void enviarTCP(Socket socket, Object mensaje) {
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
    }
    
    /**
     * Envía un objeto serializado mediante cada socket en sockets.
     * @param sockets
     * @param mensaje 
     */
    public static void enviarTCP(List<Socket> sockets, Object mensaje) {
        for (Socket socket : sockets) {
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
        }
    }
    
    /**
     * Envía objetos serializados de una lista mediante cada socket en la misma posición en sockets.
     * @param sockets
     * @param mensajes 
     */
    public static void enviarTCP(List<Socket> sockets, List<Object> mensajes) {
        for (int i = 0; i < sockets.size(); i++) {
            Socket socket = sockets.get(i);
            Object mensaje = mensajes.get(i);
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
        }
    }
    
    /**
     * Recibe objetos serializados mediante un socket que escucha PUERTO.
     * @param socket
     * @return 
     */
    public static void recibirTCP(ServerSocket serverSocket) {
        // TODO Implementar.
        throw new UnsupportedOperationException();
    }
    
    /**
     * Recibe un objeto serializado mediante un socket.
     * @param socket
     * @return 
     */
    public static Object recibirTCP(Socket socket) {
        Object resultado = new Object();
        ExecutorService es = Executors.newSingleThreadExecutor();
        Future<?> tarea = es.submit(new Connection(socket, PUERTO, resultado));
        try {
            tarea.get();
            return resultado;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        } catch (ExecutionException ex) {
            Logger.getLogger(ServiciosComunicacion.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * Recibe objectos serializados mediante cada uno de los sockets de la lista.
     * @param sockets
     * @return 
     */
    public static List<Object> recibirTCP(List<Socket> sockets) {
        List<Object> recibidos = new ArrayList<>();
        List<Future<?>> tareas = new ArrayList<>();
        for (int i = 0; i < sockets.size(); ++i) {
            ExecutorService es = Executors.newWorkStealingPool(sockets.size());
            tareas.add(es.submit(new Connection(sockets.get(i), PUERTO, recibidos, i)));
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
     * Establece conexiones mediantes sockets con cada una de las direcciones de la lista.
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
