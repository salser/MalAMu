/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import comunicacion.ServiciosComunicacion;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import malamu.Cliente;
import modelo.Jugador;

/**
 *
 * @author davlad
 */
public class FXMLConfirmacionController implements Initializable {

	protected Cliente cliente;
	protected List<Jugador> jugadores;

	@FXML
	protected Button btnSi;

	@FXML
	protected Button btnNo;

	@Override
	public void initialize(URL location, ResourceBundle resources) {}

	@FXML
	public void jugar() {
		try {
			cliente.responderConfirmacion(true);
			// Cambiar a ventana de juego
			try {
				// Cargar vista
				FXMLLoader loader = new FXMLLoader();
				loader.setLocation(getClass().getResource("/gui/FXMLDocument.fxml"));

				// Cargar raiz
				Parent root = loader.load();
				FXMLDocumentController controller = loader.<FXMLDocumentController>getController();

				// Recibir estado inicial desde el servidor
				jugadores = cliente.recibirResultados();
				
				// Enviar datos al controlador
				controller.initData(cliente, jugadores);

				// Cerrar ventana actual
				((Stage) btnSi.getScene().getWindow()).close();

				// Crear nueva escena
				Scene scene = new Scene(root);

				// Crear ventana
				Stage stage = new Stage();
				stage.setScene(scene);
				stage.show();
			} catch (IOException ex) {
				Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
			}
		} catch (IOException ex) {
			Logger.getLogger(FXMLConfirmacionController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	@FXML
	public void salir() {
		try {
			cliente.responderConfirmacion(false);
		} catch (IOException ex) {
			Logger.getLogger(FXMLConfirmacionController.class.getName()).log(Level.SEVERE, null, ex);
		}
		((Stage) btnSi.getScene().getWindow()).close();
	}

	public void initData(Cliente cliente) {
		this.cliente = cliente;
	}

}
