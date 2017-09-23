
package comunicacion;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import malamu.Cliente;

class Connection extends Thread {

    ObjectInputStream in;
    Socket conSocket;
    ServerSocket serverSocket;
    
    Object recibido;
    List<Object> recibidos;
    int pos;

    Connection(InetAddress direccion, int puerto, List<Object> recibidos, int pos) {
        try {
            this.recibidos = recibidos;
            this.pos = pos;
            serverSocket = new ServerSocket(puerto, 1, direccion);
            conSocket = serverSocket.accept();
            in = new ObjectInputStream(conSocket.getInputStream());
            this.start();
        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    Connection(InetAddress direccion, int puerto, Object recibido) {
        try {
            this.recibido = recibido;
            serverSocket = new ServerSocket(puerto, 1, direccion);
            conSocket = serverSocket.accept();
            in = new ObjectInputStream(conSocket.getInputStream());
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
        } finally {
            try {
                conSocket.close();
                serverSocket.close();
            } catch (IOException e) {/*close failed*/
            }
        }
    }
}
