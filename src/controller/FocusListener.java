/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javafx.scene.control.TextField;
import mandelbrot.WritableGrid;

/**
 *
 * @author Tomirio
 */
public class FocusListener extends FocusAdapter{
    
    WritableGrid grid;

    public FocusListener(WritableGrid grid){
        super();
        this.grid = grid;
    }
  @Override
  public void focusLost(FocusEvent evt) {
    final TextField textField = (TextField) evt.getSource();
    switch (textField.getId()){
        case "maxIterationsField":
            if (Integer.parseInt(textField.getText()) > 9500){
                textField.setText("9500");
                
            }
            //image opnieuw berekenen zonder in te zoomen
            break;
        case "blockSizeField":
            int blockSize = Integer.parseInt(textField.getText());
            if (grid.gridW % blockSize == 0 && grid.gridH % blockSize == 0 ){
                //Blocksize is valid, we kunnen inzoomen
            }
            else{
                //blocksize is niet valid, we vragen opnieuw focus
            }
            break;
    }

  }
    
}
