/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package presentacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import modelo.Jugada;
import modelo.Jugador;
import modelo.TipoAccion;

/**
 *
 * @author Henry Steven Salazar
 */
public class MalAMu {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        List<Jugador> jugadores = new ArrayList();
        Jugador j1 = new Jugador("Henry");
        Jugador j2 = new Jugador("David");
        Jugador j3 = new Jugador("Juan");

        jugadores.add(j1);
        jugadores.add(j2);
        jugadores.add(j3);

        boolean seAcabo = false;
        while (!seAcabo) {
            
        }
    }

}
