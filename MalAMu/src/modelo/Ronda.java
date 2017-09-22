/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modelo;

import java.util.List;

/**
 *
 * Clase que modela una ronda, es decir, la jugada de cada jugador del juego decidió en un determinado momento.
 * @author Espinosa, Salazar y Villamizar
 */
public class Ronda {
    List<Jugada> jugadas;

    public Ronda(List<Jugada> jugadas) {
        this.jugadas = jugadas;
    }
    
    
}
