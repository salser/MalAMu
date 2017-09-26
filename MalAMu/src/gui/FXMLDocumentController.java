/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import comunicacion.ServiciosComunicacion;
import java.awt.Dialog;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import malamu.Cliente;
import modelo.Jugada;
import modelo.Jugador;
import modelo.TipoAccion;

/**
 *
 * @author davlad
 */
public class FXMLDocumentController implements Initializable {

	Cliente cliente;

	@FXML
	private TableView<Jugador> tblPartida;
	@FXML
	private Label lblVida;
	@FXML
	private Label lblCargaAtaque;
	@FXML
	private Label lblCargaDefensa;
	@FXML
	private Button btnAtacar;
	@FXML
	private Button btnDefender;
	@FXML
	private Button btnRecargar;

	@FXML
	private void atacar(ActionEvent event) {
		Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.ATAQUE);
		cliente.enviarJugada(jugada);
		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
		cliente.recibirResultados();
		
	}

	@FXML
	private void defender(ActionEvent event) {
		System.out.println("DEFENSA!!!!");
		/*
		Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.DEFENSA);
		cliente.enviarJugada(jugada);
		*/
		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
		List<Jugador> jugadores = cliente.recibirResultados();
	}

	@FXML
	private void recargar(ActionEvent event) {
		System.out.println("entriiioo");
		System.out.println("RECARGA!");
		Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.RECARGA);
		cliente.enviarJugada(jugada);
		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tblPartida.getItems().setAll();
		try {
			this.cliente = new Cliente(InetAddress.getByName("127.0.0.1"), new Jugador (""));
		} catch (UnknownHostException ex) {
			Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
