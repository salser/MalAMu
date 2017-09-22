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
            // Inicio ronda
            List<Jugada> jugadas = new ArrayList();
            Scanner in = new Scanner(System.in);
            // Recibir jugadas
            for (int i = 0; i < jugadores.size(); i++) {
                System.out.println("Turno de " + jugadores.get(i).getNombre());
                System.out.println("Indique 1 para atacar 2 para defender 3 para cargar");
                int jugada = in.nextInt();
                switch (jugada) {
                    case 1:
                        System.out.println("hacia que jugador desea atacar");
                        for (int j = 0; j < jugadores.size(); j++) {
                            System.out.println((j + 1) + ". " + jugadores.get(j).getNombre() + ".");
                        }
                        int indObjetivo = in.nextInt() - 1;
                        if (jugadores.get(i).puedeAtacar()) {
                            jugadas.add(new Jugada(jugadores.get(i), jugadores.get(indObjetivo), TipoAccion.ATAQUE));
                        }
                        break;
                    case 2:
                        if (jugadores.get(i).puedeDefenderse()) {
                            jugadas.add(new Jugada(jugadores.get(i), null, TipoAccion.DEFENSA));
                        }
                        break;
                    case 3:
                        if (jugadores.get(i).puedeRecargar()) {
                            jugadas.add(new Jugada(jugadores.get(i), null, TipoAccion.RECARGA));
                        }
                        break;
                    default:
                        System.out.println("Turno de " + jugadores.get(i).getNombre() + " otravez por que no dio donde era!");
                        i--;
                        break;

                }
            }

            // Ejecutar jugadas de recarga
            for (Jugada jugada : jugadas) {
                if (jugada.getTipo() == TipoAccion.RECARGA) {
                    Jugador jugador = jugada.getJugador();
                    jugador.recargar();
                }
            }

            // Ejecutar jugadas de defensa
            List<Jugador> defienden = new ArrayList<>();
            for (Jugada jugada : jugadas) {
                if (jugada.getTipo() == TipoAccion.DEFENSA) {
                    defienden.add(jugada.getJugador());
                }
            }

            // Imprimir jugadores que se defendieron
            System.out.println("-------------------");
            System.out.println("   Se defienden");
            System.out.println("-------------------");
            for (Jugador jugador : defienden) {
                System.out.println(jugador.getNombre());
            }
            System.out.println("-------------------");

            // Ejecutar jugadas de ataque
            for (Jugada jugada : jugadas) {
                if (jugada.getTipo() == TipoAccion.ATAQUE) {
                    if (!defienden.contains(jugada.getObjetivo())) {
                        // Si el objetivo no se esta defendiendo
                        // Hacer daño
                        jugada.getObjetivo().recibirDano(jugada.getJugador().getDanoAtaque());
                    }
                }
            }

            // Terminar juego si hay muertos
            for (int i = 0; i < jugadores.size(); i++) {
                if (jugadores.get(i).getVida() <= 0) {
                    seAcabo = true;
                }
            }

            // Imprimir nuevo estado de los jugadores
            for (Jugador jugador : jugadores) {
                System.out.println(jugador);
            }
        }
    }

}
