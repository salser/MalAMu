package modelo;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * Define los atributos de un jugador, desde su nombre hasta la cantidad máxima
 * de cargas de defensa y ataque que pueda tener
 *
 * @author Henry Salazar, David Villamizar, Juan Espinosa
 */
public class Jugador implements Serializable {

	/**
	 * nombre: define e indica cual es el nombre del usuario que esta jugando.
	 */
	private String nombre;
	/**
	 * Indica cuanto daño hace un ataque de este jugador.
	 */
	private int danoAtaque;
	/**
	 * vida: indica cuanta vida le queda al jugador actualmente.
	 */
	private int vida;
	/**
	 * Indica la cantidad máxima de vida que puede tener un jugador.
	 */
	private int vidaMaxima;
	/**
	 * cargaAtaque: Indica la carga de ataque que tiene un jugador de esto
	 * depende si puede recargar o atacar.
	 */
	private int cargaAtaque;
	/**
	 * cargaDefensa: Indica la carga de defensa que tiene un jugador de esto
	 * depende si puede recargar o defenderse de otros ataques.
	 */
	private int cargaDefensa;
	/**
	 * cargaMáximaAtaque: Indica cual es el máximo de cargas de ataque que un
	 * jugador puede tener.
	 */
	private int cargaMaximaAtaque;
	/**
	 * cargaMáximaDefensa: Indica cual es el máximo de cargas de defensa que un
	 * jugador puede tener.
	 */
	private int cargaMaximaDefensa;
	/**
	 * Indica cuanto cuesta realizar un ataque en cargas de ataque.
	 */
	private int costoAtaque;
	/**
	 * Indica cuanto cuesta realizar una defensa en cargas de defensa.
	 */
	private int costoDefensa;
	/**
	 * Indica cuanto se recupera de carga de ataque al recargar.
	 */
	private int cantidadRecargaAtaque;
	/**
	 * Indica cuanto se recupera de carga de defensa al recargar.
	 */
	private int cantidadRecargaDefensa;
        
	/**
	* Identificador de la posicion del cliente en el registro del servidor
	*/
	private UUID id;

	/**
	 * Crea un jugador con un nombre específico y parámetros por defecto.
	 *
	 * @param nombre
	 */
	public Jugador(String nombre) {
		this.nombre = nombre;
		this.danoAtaque = 30;
		this.cantidadRecargaAtaque = 1;
		this.cantidadRecargaDefensa = 2;
		this.cargaAtaque = 1;
		this.cargaDefensa = 2;
		this.cargaMaximaAtaque = 3;
		this.cargaMaximaDefensa = 5;
		this.costoAtaque = 1;
		this.costoDefensa = 1;
		this.vidaMaxima = 100;
		this.vida = 100;
		this.id = null;
                
	}

	/**
	 * Crea un jugador con parámetros específicos.
	 *
	 * @param nombre
	 * @param danoAtaque
	 * @param vida
	 * @param vidaMaxima
	 * @param cargaAtaque
	 * @param cargaDefensa
	 * @param cargaMaximaAtaque
	 * @param cargaMaximaDefensa
	 * @param costoAtaque
	 * @param costoDefensa
	 * @param cantidadRecargaAtaque
	 * @param cantidadRecargaDefensa
	 */
	public Jugador(String nombre, int danoAtaque, int vida, int vidaMaxima, int cargaAtaque, int cargaDefensa, int cargaMaximaAtaque, int cargaMaximaDefensa, int costoAtaque, int costoDefensa, int cantidadRecargaAtaque, int cantidadRecargaDefensa) {
		this.nombre = nombre;
		this.danoAtaque = danoAtaque;
		this.vida = vida;
		this.vidaMaxima = vidaMaxima;
		this.cargaAtaque = cargaAtaque;
		this.cargaDefensa = cargaDefensa;
		this.cargaMaximaAtaque = cargaMaximaAtaque;
		this.cargaMaximaDefensa = cargaMaximaDefensa;
		this.costoAtaque = costoAtaque;
		this.costoDefensa = costoDefensa;
		this.cantidadRecargaAtaque = cantidadRecargaAtaque;
		this.cantidadRecargaDefensa = cantidadRecargaDefensa;
		this.id = null;
	}

	/**
	 * Realiza el daño indicado a este jugador.
	 *
	 * @param cantidad la cantidad de daño a recibir.
	 */
	public void recibirDano(int cantidad) {
		if (cantidad > 0) {
			this.vida = Math.max(this.vida - cantidad, 0);
		}
	}

	/**
	 * El jugador en su turno efectua un ataque, es decir que se disminuye en la
	 * cantidad especificada la carga de ataque, es valido aclarar que solo si
	 * puede atacar gasta carga y efectua el ataque, de lo contrario no.
	 *
	 * @return verdadero si puede atacar y falso si no lo puede hacer
	 */
	public boolean gastarCargaAtaque() {
		if (puedeAtacar()) {
			this.cargaAtaque = Math.max(this.cargaAtaque - this.costoAtaque, 0);
			return true;
		}
		return false;
	}

	/**
	 * El jugador en su turno se defiende, es decir que se disminuye en la
	 * cantidad especificada la carga de defensa, es valido aclarar que solo si
	 * puede defender gasta carga y se defiende, de lo contrario no.
	 *
	 * @return verdadero si puede defenderse y falso si no lo puede hacer
	 */
	public boolean gastarCargaDefensa() {
		if (puedeDefenderse()) {
			this.cargaDefensa = Math.max(this.cargaDefensa - this.costoDefensa, 0);
			return true;
		}
		return false;
	}

	/**
	 * Aplica el cambio en cargas de ataque y defensa de este jugador, producto
	 * de recargar.
	 *
	 * @return verdadero si el jugador puede cargar alguno de sus atributos y
	 * false si no puede recargar ninguno de los dos atributos
	 */
	public boolean recargar() {
		if (puedeRecargar()) {
			if (this.cargaAtaque + this.cantidadRecargaAtaque <= this.cargaMaximaAtaque) {
				this.cargaAtaque += this.cantidadRecargaAtaque;
				if (this.cargaDefensa + this.cantidadRecargaDefensa <= this.cargaMaximaDefensa) {
					this.cargaDefensa += this.cantidadRecargaDefensa;
				}
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	/**
	 * Método que edeternmina si un jugador puede o no puede atacar, con la
	 * cantidad definida dentro de los atributos
	 *
	 * @return retorna verdadero si puede atacar, de lo coontrario retorna falso
	 */
	public boolean puedeAtacar() {
		if (this.cargaAtaque - this.costoAtaque >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Método que edeternmina si un jugador puede o no defenderse, con la
	 * cantidad definida dentro de los atributos
	 *
	 * @return retorna verdadero si puede defenderse, de lo coontrario retorna
	 * falso
	 */
	public boolean puedeDefenderse() {
		if (this.cargaDefensa - this.costoDefensa >= 0) {
			return true;
		}
		return false;
	}

	/**
	 * Método que deternmina si un jugador puede o no recargar, con la cantidad
	 * definida dentro de los atributos.
	 *
	 * @return falso si no puede recargar ni defensa i ataque y verdadero si
	 * puede recargar al menos una de las dos
	 */
	public boolean puedeRecargar() {
		if (this.cargaAtaque + this.cantidadRecargaAtaque <= this.cargaMaximaAtaque || this.cargaDefensa + this.cantidadRecargaDefensa <= this.cargaMaximaDefensa) {
			return true;
		}
		return false;
	}

	public int getVida() {
		return vida;
	}

	public void setVida(int vida) {
		this.vida = vida;
	}

	public String getNombre() {
		return nombre;
	}

	public int getDanoAtaque() {
		return danoAtaque;
	}
	
	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}            

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Jugador other = (Jugador) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return true;
	}
        
	

	public int getVidaMaxima() {
		return vidaMaxima;
	}

	public int getCargaAtaque() {
		return cargaAtaque;
	}

	public int getCargaDefensa() {
		return cargaDefensa;
	}

	public int getCargaMaximaAtaque() {
		return cargaMaximaAtaque;
	}

	public int getCargaMaximaDefensa() {
		return cargaMaximaDefensa;
	}

	public void setCargaAtaque(int cargaAtaque) {
		this.cargaAtaque = cargaAtaque;
	}
	

	
	@Override
	public String toString() {
		return "Jugador{" + "nombre=" + nombre + ", danoAtaque=" + danoAtaque + ", vida=" + vida + ", vidaMaxima=" + vidaMaxima + ", cargaAtaque=" + cargaAtaque + ", cargaDefensa=" + cargaDefensa + ", cargaMaximaAtaque=" + cargaMaximaAtaque + ", cargaMaximaDefensa=" + cargaMaximaDefensa + ", costoAtaque=" + costoAtaque + ", costoDefensa=" + costoDefensa + ", cantidadRecargaAtaque=" + cantidadRecargaAtaque + ", cantidadRecargaDefensa=" + cantidadRecargaDefensa + '}';
	}



}
