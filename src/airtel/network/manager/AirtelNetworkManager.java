/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airtel.network.manager;

import airtel.network.manager.model.Node;
import airtel.network.manager.views.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Orphe
 */
public class AirtelNetworkManager {

    public static void testConsole(){
        Node mouila  = new Node("Mouila");
        Node mouila2 = new Node("Mouila 2");
        Node mouila3 = new Node("Mouila 3");
        Node mouila4 = new Node("Mouila 4");
        Node mouila5 = new Node("Mouila 5");
        Node mouila6 = new Node("Mouila 6");
        Node mouilaAirport = new Node("Mouila Airport");

        mouila.linkTo(mouila2, mouila3, mouila4, mouila5);
        mouila2.linkTo(mouilaAirport);
        mouila3.linkTo(mouila6);
        
        System.out.println(mouila);
        System.out.println(mouila2);
        System.out.println(mouila3);
        System.out.println(mouila4);
        System.out.println(mouila5);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
          
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            SwingUtilities.invokeLater(() -> {
              
                Frame frame = new Frame();
                frame.createAndShowGui();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
}
