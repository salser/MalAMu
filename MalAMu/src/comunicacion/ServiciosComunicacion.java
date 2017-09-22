/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author davl3232
 */
public class ServiciosComunicacion {

    public static final int PUERTO = 7896;
    private ProducerConsumer pc;

    public static void enviarTCP(InetAddress direccion, Object mensaje) {
        Socket s = null;
        try {
            int serverPort = PUERTO;
            s = new Socket(direccion, serverPort);
            //ObjectInputStream in = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(s.getOutputStream());
            out.writeObject(mensaje);      	// Enviar el socket con un objeto serializado
           // String data = in.readUTF();	    // read a line of data from the stream
           // System.out.println("Received: " + data);
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

    public static Object recibirTCP(InetAddress filtro) {
        try {
            int serverPort = PUERTO; // the server port
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
               
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
        return "NO SOCKET!";
    }
}
