
package comunicacion;

import java.net.Socket;

/**
 * Representa una conexión recibida en el servidor.
 * 
 * @author davl3232
 */
public class Conexion {

	/**
	 * Socket mediante el cual se realizó la conexión.
	 */
	public Socket socket;
	
	/**
	 * Objeto enviado como petición al crear la conexión.
	 */
	public Object objeto;

	/**
	 * Constructor de una conexión.
	 * 
	 * @param socket
	 * @param objeto 
	 */
	public Conexion(Socket socket, Object objeto) {
		this.socket = socket;
		this.objeto = objeto;
	}
}
