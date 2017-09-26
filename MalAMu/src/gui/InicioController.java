/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import malamu.Cliente;
import modelo.Jugador;

/**
 *
 * @author davlad
 */
public class InicioController implements Initializable {
	
	@FXML
	private TextField tfNombre;
	
	@FXML
	private TextField tfIp;
	
	@FXML
	private Button btnJugar;

	@FXML
	private void jugar(ActionEvent event){
		Cliente cliente = null;
		try {
			String nombre = tfNombre.getText();
			InetAddress direccion = InetAddress.getByName(tfIp.getText());
			System.out.println(direccion);
			cliente = new Cliente(direccion, new Jugador(nombre));
			try {
				cliente.iniciarPartida();
				// Cambiar a ventana de confirmación
				try {
					// Cargar vista
					FXMLLoader loader = new FXMLLoader();
					loader.setLocation(getClass().getResource("/gui/FXMLConfirmacion.fxml"));
					
					// Cargar raiz
					Parent root = loader.load();
					FXMLConfirmacionController controller = loader.<FXMLConfirmacionController>getController();
					
					System.out.println("Cliente Inicio: " + cliente);
					// Enviar datos al controlador
					controller.initData(cliente);
					
					// Cerrar ventana actual
					((Stage)btnJugar.getScene().getWindow()).close();
					
					// Crear nueva escena
					Scene scene = new Scene(root);
					
					// Crear ventana
					Stage stage = new Stage();
					stage.setScene(scene);
					stage.show();
				} catch (IOException ex) {
					Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
				}
			} catch (IOException ex1) {
				Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex1);
			}
		} catch (UnknownHostException ex) {
			Logger.getLogger(InicioController.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {}
	
}
