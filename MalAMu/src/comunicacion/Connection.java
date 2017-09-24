
package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import malamu.Cliente;

class Connection extends Thread {

    ObjectInputStream in;
    
    Object recibido;
    List<Object> recibidos;
    int pos;

    Connection(Socket socket, List<Object> recibidos, int pos) {
        try {
            this.recibidos = recibidos;
            this.pos = pos;
            in = new ObjectInputStream(socket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    Connection(Socket socket, Object recibido) {
        try {
            this.recibido = recibido;
            in = new ObjectInputStream(socket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }
    
    Connection(Socket socket, ConcurrentLinkedQueue<Object> recibido) {
        try {
            this.recibido = recibido;
            in = new ObjectInputStream(socket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {
            if (recibido == null) {
                recibidos.set(pos, (Object)in.readObject());
            } else {
                recibido = (Object)in.readObject();
            }
        } catch (EOFException e) {
            System.out.println("EOF:" + e.getMessage());
        } catch (IOException e) {
            System.out.println("readline:" + e.getMessage());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
