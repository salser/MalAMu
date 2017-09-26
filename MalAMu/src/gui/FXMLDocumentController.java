/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
	private void atacar(ActionEvent event) {
		Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.ATAQUE);
		//TODO Enviar jugada
		System.out.println("ATAQUE!" + lblVida.getText());
	}

	@FXML
	private void defender(ActionEvent event) {
		System.out.println("DEFENSA!");
	}

	@FXML
	private void recargar(ActionEvent event) {
		System.out.println("RECARGA!");
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		tblPartida.getItems().setAll();
	}

}
