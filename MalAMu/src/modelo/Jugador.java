package modelo;

/**
 *
 * @author Henry Salazar, David Villamizar, Juan Espinosa
 */
public class Jugador {
    
    private String nombre;
    private int vida;
    private int cargaAtaque;
    private int cargaDefensa;
    private int cargaMaximaAtaque;
    private int cargaMaximaDefensa;

    public Jugador(String nombre, int vida, int cargaAtaque, int cargaDefensa, int cargaMaximaAtaque, int cargaMaximaDefensa) {
        this.nombre = nombre;
        this.vida = vida;
        this.cargaAtaque = cargaAtaque;
        this.cargaDefensa = cargaDefensa;
        this.cargaMaximaAtaque = cargaMaximaAtaque;
        this.cargaMaximaDefensa = cargaMaximaDefensa;
    }

    
    
    public void recibirDano(int cantidad){
        this.vida-=cantidad;
    }
    
    /*
    * 
    */
    public boolean gastarCargaAtaque(int cantidad){
        if(puedeAtacar()){
            this.cargaAtaque-= cantidad;
            return true;
        }
        return false;
    }
    
    /*
    * @param
    */
    public boolean gastarCargaDefensa(int cantidad){
        if(puedeDefenderse()){
            this.cargaDefensa -= cantidad;
            return false;
        }
        return false;
    }
    public boolean recargar(int cantidadAtaque, int cantidadDefensa){
        if(puedeRecargar()){
            if(this.cargaAtaque + cantidadAtaque <= this.cargaMaximaAtaque ){
                this.cargaAtaque += cantidadAtaque;
            }else{
                return false;
            }
            
            this.cargaDefensa += cantidadDefensa;
            return true;
        }
        return false;
    }
    public boolean puedeAtacar(){
        //TODO
        return false;
    }
    public boolean puedeDefenderse(){
        //TODO
        return false;
    }
    public boolean puedeRecargar(){
        //TODO
        return false;
    }
    
}
