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
import javafx.scene.control.Button;

/**
 *
 * @author davlad
 */
public class InicioController implements Initializable {
	
	@FXML
	private Button btnJugar;

	@FXML
	private void jugar(ActionEvent event){
		System.out.println("jugar");
	}
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		
	}
	
}
