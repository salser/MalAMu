
package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import malamu.Cliente;
import malamu.Servidor;

public class ServiciosComunicacion {

    public static final int PUERTO = 7896;
    public static final long TIEMPO_MS_ESPERA_MAX = 1000;
    
    public static void enviarTCP(InetAddress direccion, Object mensaje) {
        Socket s = null;
        try {
            int serverPort = PUERTO;
            s = new Socket(direccion, serverPort);
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(mensaje);
           System.out.println("Received: " + mensaje);
        } catch (UnknownHostException e) {
            System.out.println("Socket:" + e.getMessage());
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } finally {
            if (s != null) {
                try {
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }

    public static Object recibirTCP(InetAddress direccion) {
        Object resultado = new Object();
        ExecutorService es = Executors.newSingleThreadExecutor();
        es.submit(new Connection(direccion, PUERTO, resultado));
        try {
            es.awaitTermination(TIEMPO_MS_ESPERA_MAX, TimeUnit.MILLISECONDS);
            return resultado;
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    
    public static List<Object> recibirTCP(List<Cliente> clientes) {
        List<Object> recibidos = new ArrayList<>();
        for (int i = 0; i < clientes.size(); ++i) {
            ExecutorService es = Executors.newFixedThreadPool(clientes.size());
            es.submit(new Connection(clientes.get(i).getDireccion(), PUERTO, recibidos, i));
        }
        return recibidos;
    }
}
