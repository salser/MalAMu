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

		Jugador jug = tblPartida.getSelectionModel().getSelectedItem();
		if (jug != null) {
			/*Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.ATAQUE);
			cliente.enviarJugada(jugada);*/
			btnAtacar.setDisable(true);
			btnDefender.setDisable(true);
			btnRecargar.setDisable(true);
			//List<Jugador> jugadores = cliente.recibirResultados();
			btnAtacar.setDisable(false);
			btnDefender.setDisable(false);
			btnRecargar.setDisable(false);
			List<Jugador> jugadores = new ArrayList<>();
			Jugador j1 = new Jugador("Henry");
			j1.setVida(3);
			jugadores.add(j1);
			Jugador j2 = new Jugador("David");
			j2.setVida(5);
			jugadores.add(j2);
			Jugador j3 = new Jugador("Juan");
			j3.setVida(2);
			jugadores.add(j3);
			actualizarTabla(jugadores);
			System.out.println(jug.getNombre());
		} else {
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No puede atacar");
			alert.setHeaderText("No puede atacar, seleccione una file y vuelva a oprimir");
			alert.setContentText("Tampoco puede atacar si no tienes carga en ataque");
			alert.showAndWait();
		}

	}

	@FXML
	private void defender(ActionEvent event) throws InterruptedException {
		System.out.println("DEFENSA!!!!");
		/*
		Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.DEFENSA);
		cliente.enviarJugada(jugada);
		 */
		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
		//List<Jugador> jugadores = cliente.recibirResultados();
		btnAtacar.setDisable(false);
		btnDefender.setDisable(false);
		btnRecargar.setDisable(false);
		List<Jugador> jugadores = new ArrayList<>();
		Jugador j1 = new Jugador("Henry");
		j1.setVida(3);
		jugadores.add(j1);
		Jugador j2 = new Jugador("David");
		j2.setVida(5);
		jugadores.add(j2);
		Jugador j3 = new Jugador("Juan");
		j3.setVida(2);
		j3.setCargaAtaque(0);
		jugadores.add(j3);

		actualizarTabla(jugadores);
	}

	@FXML
	private void recargar(ActionEvent event) {
		System.out.println("RECARGA!");

		/*Jugada jugada = new Jugada(cliente.getJugador(), null, TipoAccion.RECARGA);
		cliente.enviarJugada(jugada);*/
		btnAtacar.setDisable(true);
		btnDefender.setDisable(true);
		btnRecargar.setDisable(true);
		List<Jugador> jugadores = cliente.recibirResultados();
		btnAtacar.setDisable(false);
		btnDefender.setDisable(false);
		btnRecargar.setDisable(false);
		actualizarTabla(jugadores);
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {}

	protected void inicializar() {
		System.out.println("Cliente Document: " + cliente);
		lblVida.setText("" + cliente.getJugador().getVida() + "/" + cliente.getJugador().getVidaMaxima());
		lblCargaAtaque.setText("" + cliente.getJugador().getCargaAtaque() + "/" + cliente.getJugador().getCargaMaximaAtaque());
//		lblCargaDefensa.setText("" + cliente.getJugador().getCargaDefensa() + "/" + cliente.getJugador().getCargaMaximaDefensa());
		if (!this.cliente.getJugador().puedeAtacar()) {
			btnAtacar.setDisable(true);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No puede atacar");
			alert.setHeaderText("No tiene carga de ataque disponible, por favor carga");
			alert.showAndWait();
		}
		if (!this.cliente.getJugador().puedeDefenderse()) {
			btnDefender.setDisable(true);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No puede defenderse");
			alert.setHeaderText("No tiene carga de defensa disponible, por favor carga");
			alert.showAndWait();
		}
		if (!this.cliente.getJugador().puedeRecargar()) {
			btnRecargar.setDisable(true);
			Alert alert = new Alert(Alert.AlertType.INFORMATION);
			alert.setTitle("No puede recargar");
			alert.setHeaderText("Los límites de carga se alcanzaron");
			alert.showAndWait();
		}
	}

	public void initData(Cliente cliente, List<Jugador> jugadores) {
		this.cliente = cliente;
		this.jugadores = jugadores;
		tblPartida.setEditable(false);
		tblPartida.getItems().setAll();
		inicializar();
	}

	private void actualizarTabla(List<Jugador> jugadores) {
		final ObservableList<Jugador> data = FXCollections.observableArrayList();

		tblColNombre.setMinWidth(100);
		tblColVida.setMinWidth(100);
		tblColNombre.setCellValueFactory(new PropertyValueFactory<>("Nombre"));
		tblColVida.setCellValueFactory(new PropertyValueFactory<>("Vida"));
		tblPartida.getItems().setAll(jugadores);
	}
}
