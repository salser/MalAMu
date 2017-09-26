/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import comunicacion.ServiciosComunicacion;
import java.awt.Dialog;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import malamu.Cliente;
import modelo.Jugada;
import modelo.Jugador;
import modelo.TipoAccion;

/**
 *
 * @author davlad
 */
public class FXMLDocumentController implements Initializable {

	protected Cliente cliente;
	protected List<Jugador> jugadores;

	@FXML
	protected TableView<Jugador> tblPartida;
	@FXML
	protected TableColumn<Jugador, String> tblColNombre;
	@FXML
	protected TableColumn<Jugador, String> tblColVida;
	@FXML
	protected Label lblVida;
	@FXML
	protected Label lblCargaAtaque;
	@FXML
	protected Label lblCargaDefensa;
	@FXML
	protected Button btnAtacar;
	@FXML
	protected Button btnDefender;
	@FXML
	protected Button btnRecargar;

	@FXML
	private void atacar(ActionEvent event) {
		Jugador jugador = cliente.getJugador();
		
		if (!tblPartida.getSelectionModel().isEmpty()) {
			int indObjetivo = tblPartida.getSelectionModel().getSelectedIndex();
			Jugada jugada = new Jugada(jugador, indObjetivo, TipoAccion.ATAQUE);

			btnAtacar.setDisable(true);
			btnDefender.setDisable(true);
			btnRecargar.setDisable(true);

			cliente.enviarJugada(jugada);
			jugadores = cliente.recibirResultados();

			// Actualizar lista de jugadores
			actualizarTabla();

			// Actualizar vista jugador
			actualizarCamposJugador();
			
			btnAtacar.setDisable(false);
			btnDefender.setDisable(false);
			btnRecargar.setDisable(false);
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("El ataque no tiene objetivo");
			alert.setHeaderText("El ataque no tiene objetivo");
			alert.setContentText("Seleccione un jugador e intente de nuevo.");
			alert.showAndWait();
		}

	}

	@FXML
	private void defender(ActionEvent event) throws InterruptedException {
		Jugada jugada = new Jugada(cliente.getJugador(), -1, TipoAccion.DEFENSA);

		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
		
		cliente.enviarJugada(jugada);
		jugadores = cliente.recibirResultados();

		// Actualizar lista de jugadores
		actualizarTabla();

		// Actualizar vista jugador
		actualizarCamposJugador();
		
		btnAtacar.setDisable(false);
		btnDefender.setDisable(false);
		btnRecargar.setDisable(false);
	}

	@FXML
	private void recargar(ActionEvent event) {
		Jugada jugada = new Jugada(cliente.getJugador(), -1, TipoAccion.RECARGA);
		
		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
		
		cliente.enviarJugada(jugada);
		jugadores = cliente.recibirResultados();

		// Actualizar lista de jugadores
		actualizarTabla();

		// Actualizar vista jugador
		actualizarCamposJugador();
		
		btnAtacar.setDisable(false);
		btnDefender.setDisable(false);
		btnRecargar.setDisable(false);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
	}

	public void initData(Cliente cliente, List<Jugador> jugadores) {
		this.cliente = cliente;
		this.jugadores = jugadores;

		// Actualizar lista de jugadores
		actualizarTabla();

		// Actualizar vista jugador
		actualizarCamposJugador();
	}

	private void actualizarCamposJugador() {
		Jugador jugador = cliente.getJugador();
		lblVida.setText("" + jugador.getVida() + "/" + jugador.getVidaMaxima());
		lblCargaAtaque.setText("" + jugador.getCargaAtaque() + "/" + jugador.getCargaMaximaAtaque());
		lblCargaDefensa.setText("" + jugador.getCargaDefensa() + "/" + jugador.getCargaMaximaDefensa());
		if (!this.cliente.getJugador().puedeAtacar()) {
			btnAtacar.setDisable(true);
		}
		if (!this.cliente.getJugador().puedeDefenderse()) {
			btnDefender.setDisable(true);
		}
		if (!this.cliente.getJugador().puedeRecargar()) {
			btnRecargar.setDisable(true);
		}
	}

	private void actualizarTabla() {
		tblColNombre.setMinWidth(100);
		tblColVida.setMinWidth(100);
		tblColNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
		tblColVida.setCellValueFactory(new PropertyValueFactory<>("Vida"));
		tblPartida.getItems().setAll(jugadores);
	}
}
