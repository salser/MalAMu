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
        List<Jugador> jugadores =  new ArrayList();
        Jugador j1 = new Jugador("Henry", 3, 1, 2, 3, 3);
        Jugador j2 = new Jugador("David", 3, 1, 2, 3, 3);
        Jugador j3 = new Jugador("Juan", 3, 1, 2, 3, 3);
        jugadores.add(j1);
        jugadores.add(j2);
        jugadores.add(j3);
        boolean seAcabo = false;
        while(!seAcabo){
            List<Jugada> jugadas = new ArrayList();
            System.out.println("Indique 1 para atacar 2 para defender 3 para cargar");
            Scanner in = new Scanner(System.in);
            for(int i = 0; i < jugadores.size(); i++){
                System.out.println("Turno de "+jugadores.get(i).getNombre());
                int jugada = in.nextInt();
                switch(jugada){
                    case 1: 
                        System.out.println("hacia que jugador desea atacar");
                        int posJ = in.nextInt();
                        if(jugadores.get(i).puedeAtacar(1)){
                            jugadas.add(new Jugada(jugadores.get(i), j3, TipoAccion.ATAQUE));
                        }
                }
            }
            
            for(int i = 0; i < jugadores.size(); i++){
                if(jugadores.get(i).getVida() <= 0){
                    seAcabo = true;
                }
            }
        }
    }
    
}
