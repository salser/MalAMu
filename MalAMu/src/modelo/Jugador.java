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

    public Jugador(String nombre, int vida, int cargaAtaque, int cargaDefensa) {
        this.nombre = nombre;
        this.vida = vida;
        this.cargaAtaque = cargaAtaque;
        this.cargaDefensa = cargaDefensa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public int getCargaAtaque() {
        return cargaAtaque;
    }

    public void setCargaAtaque(int cargaAtaque) {
        this.cargaAtaque = cargaAtaque;
    }

    public int getCargaDefensa() {
        return cargaDefensa;
    }

    public void setCargaDefensa(int cargaDefensa) {
        this.cargaDefensa = cargaDefensa;
    }
    public void recibirDano(int cantidad){
        //TODO
    }
    public void gastarCargaAtaque(int cantidad){
        //TODO
    }
    public void gastarCargaDefensa(int cantidad){
        //TODO
    }
    public void recargar(int cantidad){
        //TODO
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
