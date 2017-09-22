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

    public static void enviarTCP(InetAddress direccion, Object mensaje) {
        Socket socket = null;
        try {
            int serverPort = PUERTO;
            socket = new Socket(direccion, serverPort);
            //ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
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
            if (socket != null) {
                try {
                    socket.close();
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
