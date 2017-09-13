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
        Jugador j1 = new Jugador("Henry", 3, 1, 2, 3, 3);
        Jugador j2 = new Jugador("David", 3, 1, 2, 3, 3);
        Jugador j3 = new Jugador("Juan", 3, 1, 2, 3, 3);
        jugadores.add(j1);
        jugadores.add(j2);
        jugadores.add(j3);
        boolean seAcabo = false;
        while (!seAcabo) {
            List<Jugada> jugadas = new ArrayList();
            Scanner in = new Scanner(System.in);
            for (int i = 0; i < jugadores.size(); i++) {
                System.out.println("Turno de " + jugadores.get(i).getNombre());
                System.out.println("Indique 1 para atacar 2 para defender 3 para cargar");
                int jugada = in.nextInt();
                switch (jugada) {
                    case 1:
                        System.out.println("hacia que jugador desea atacar");
                        for (int j = 0; j < jugadores.size(); j++) {
                            System.out.println(j + ". " + jugadores.get(j).getNombre() + ".");
                        }
                        int posJ = in.nextInt();
                        if (jugadores.get(i).puedeAtacar(1)) {
                            jugadas.add(new Jugada(jugadores.get(i), j3, TipoAccion.ATAQUE));
                        }
                        break;
                    case 2:
                        if (jugadores.get(i).puedeDefenderse(1)) {
                            jugadas.add(new Jugada(jugadores.get(i), j3, TipoAccion.DEFENSA));
                        }
                        break;
                    case 3:
                        if (jugadores.get(i).puedeRecargar(1, 1)) {
                            jugadas.add(new Jugada(jugadores.get(i), j3, TipoAccion.RECARGA));
                        }
                        break;
                    default:
                        System.out.println("Turno de " + jugadores.get(i).getNombre() + " otravez por que no dio donde era!");
                        System.out.println("Indique 1 para atacar 2 para defender 3 para cargar");
                        jugada = in.nextInt();
                        i--;
                        break;

                }
            }
            for (Jugada j : jugadas) {
                if(j.getTipo() == TipoAccion.RECARGA){
                    Jugador ju = j.getJugador();
                    ju.recargar(1, 2);
                }
            }
            List<Jugador> defienden = new ArrayList<>();
            for (Jugada j : jugadas) {
                if(j.getTipo() == TipoAccion.DEFENSA){
                    defienden.add(j.getJugador());
                }
            }
            for (Jugada j : jugadas) {
                if(j.getTipo() == TipoAccion.ATAQUE){
                    for (Jugador def : defienden) {
                        if(def.equals(j.getObjetivo())){
                            
                        }
                    }
                }
            }
            for (int i = 0; i < jugadores.size(); i++) {
                if (jugadores.get(i).getVida() <= 0) {
                    seAcabo = true;
                }
            }
        }
    }

}
