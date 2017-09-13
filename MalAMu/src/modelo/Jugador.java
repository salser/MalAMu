package modelo;

/**
 * Define los atributos de un jugador, desde su nombre hasta la cantidad máxima de cargas de defensa y ataque que pueda tener
 * @author Henry Salazar, David Villamizar, Juan Espinosa
 */
public class Jugador {
    /**
     * nombre: define e indica cual es el nombre del usuario que esta jugando
     */
    private String nombre;
    /**
     * vida: indica cuantas vidas le quedan a un jugador en un juego empezando desde 3 según 
     *       los parámetros establecidos
     */
    private int vida;
    /**
     * cargaAtaque: Indica la carga de ataque que tiene un jugador
     *              de esto depende si puede recargar o atacar.
     */
    private int cargaAtaque;
    /**
     * cargaDefensa: Indica la carga de defensa que tiene un jugador
     *               de esto depende si puede recargar o defenderse de otros ataques.
     */
    private int cargaDefensa;
    /**
     * cargaMáximaAtaque: Indica cual es el máximo de cargas de ataque que un jugador puede
     *              tener.
     */
    private int cargaMaximaAtaque;
    /**
     * cargaMáximaDefensa: Indica cual es el máximo de cargas de defensa que un jugador puede
     *              tener.
     */
    private int cargaMaximaDefensa;

    /**
     * Constructor de un jugador
     * @param nombre
     * @param vida
     * @param cargaAtaque
     * @param cargaDefensa
     * @param cargaMaximaAtaque
     * @param cargaMaximaDefensa 
     */
    public Jugador(String nombre, int vida, int cargaAtaque, int cargaDefensa, int cargaMaximaAtaque, int cargaMaximaDefensa) {
        this.nombre = nombre;
        this.vida = vida;
        this.cargaAtaque = cargaAtaque;
        this.cargaDefensa = cargaDefensa;
        this.cargaMaximaAtaque = cargaMaximaAtaque;
        this.cargaMaximaDefensa = cargaMaximaDefensa;
    }
    
    /**
     * Esta método ejecuta un daño especifico a un jugador especifico, es decir el
     * jugador especificado pierde la cantidad de daño especificado
     * @param cantidad indica la cantidad de daño que se le va a hacer a un jugador
     */
    public void recibirDano(int cantidad){
        this.vida-=cantidad;
    }
    
    /**
     * El jugador en su turno efectua un ataque, es decir que se disminuye en la cantidad
     * especificada la carga de ataque, es valido aclarar que solo si puede atacar gasta carga y 
     * efectua el ataque, de lo contrario no.
     * @param cantidad que quiere gastar de ataque un jugador en su turno
     * @return verdadero si puede atacar y falso si no lo puede hacer
     */
    public boolean gastarCargaAtaque(int cantidad){
        if(puedeAtacar(cantidad)){
            this.cargaAtaque-= cantidad;
            return true;
        }
        return false;
    }
    
    /**
     * El jugador en su turno se defiende, es decir que se disminuye en la cantidad
     * especificada la carga de defensa, es valido aclarar que solo si puede defender gasta carga y 
     * se defiende, de lo contrario no.
     * @param cantidad que quiere gastar de defensa un jugador en su turno
     * @return verdadero si puede defenderse y falso si no lo puede hacer
     */
    public boolean gastarCargaDefensa(int cantidad){
        if(puedeDefenderse(cantidad)){
            this.cargaDefensa -= cantidad;
            return true;
        }
        return false;
    }
    
    /**
     * El jugador en su turno escoge recargar, es decir que se aumenta en la cantidad
     * especificada en ataque y defensa la carga de ataque y defensa respectivamente,
     * es valido aclarar que solo si puede recargar regarga, de lo contrario no.
     * @param cantidadAtaque: indica la cantidad que quiere recargar de ataque el jugador
     * @param cantidadDefensa: indica la cantidad que quiere recargar de defensa el jugador
     * @return verdadero si el jugador puede cargar alguno de sus atributos y false si no puede recargar ninguno de los dos atributos
     */
    public boolean recargar(int cantidadAtaque, int cantidadDefensa){
        if(puedeRecargar(cantidadAtaque,cantidadDefensa)){
            if(this.cargaAtaque + cantidadAtaque <= this.cargaMaximaAtaque ){
                this.cargaAtaque += cantidadAtaque;
                if(this.cargaDefensa + cantidadDefensa <= this.cargaMaximaDefensa){
                    this.cargaDefensa += cantidadDefensa;
                }
                return true;
            }else{
                return false;
            }
        }
        return false;
    }
    
    /**
     * Método que edeternmina si un jugador puede o no puede atacar, 
     * con la cantidad definida dentro de los atributos
     * @param cantidad: cantidad que se le envia a a función para saber si puede o no atacar
     * @return retorna verdadero si puede atacar, de lo coontrario  retorna falso
     */
    public boolean puedeAtacar(int cantidad){
        if(this.cargaAtaque - cantidad >= 0){
            return true;
        }
        return false;
    }
    
    /**
     * Método que edeternmina si un jugador puede o no 
     * defenderse, con la cantidad definida dentro de los atributos
     * @param cantidad: cantidad que se le envia a a función para saber si puede o no defenderse la cantidad que solicita
     * @return retorna verdadero si puede defenderse, de lo coontrario  retorna falso
     */
    public boolean puedeDefenderse(int cantidad){
        if(this.cargaDefensa - cantidad >= 0){
            return true;
        }
        return false;
    }
    
    /**
     * Método que deternmina si un jugador puede o no 
     * recargar, con la cantidad definida dentro de los atributos
     * @param cantidadAtaque: cantidad de ataque que el jugador quiere recargar
     * @param cantidadDefensa: cantidad de defensa que el jugador quiere recargar
     * @return falso si no puede recargar ni defensa i ataque y verdadero si puede recargar al menos una de las dos
     */
    public boolean puedeRecargar(int cantidadAtaque, int cantidadDefensa){
        if(this.cargaAtaque + cantidadAtaque <= this.cargaMaximaAtaque || this.cargaDefensa + cantidadDefensa <= this.cargaMaximaDefensa){
            return true;
        }
        return false;
    }

    public int getVida() {
        return vida;
    }

    public String getNombre() {
        return nombre;
    }
    
    
    
}
