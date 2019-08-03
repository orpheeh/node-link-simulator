/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airtel.network.manager.views;

import airtel.network.manager.model.Link;
import airtel.network.manager.model.Node;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/**
 *
 * @author Orphe
 */
public class Frame {
    
    private static final String separator = "#";
    
    private List<Node> nodes = new ArrayList<>();
    
    public void createAndShowGui(){
        JFrame frame = new JFrame("Airtel Network Area");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        Panel panel = new Panel(nodes, 400, 400);
        panel.addMouseListener(panel);
        panel.addMouseMotionListener(panel);
        panel.setBackground(Color.black);

        frame.setJMenuBar(createMenuBar(panel));
        
        JRadioButton buttonDrawNode = new JRadioButton("Noeud");
        JRadioButton buttonDrawLink = new JRadioButton("Lien");
        
        ButtonGroup buttonDrawGroup = new ButtonGroup();
        
        buttonDrawGroup.add(buttonDrawNode);
        buttonDrawGroup.add(buttonDrawLink);
        
        
        JTextField nextNodeNameTextField = new JTextField();
        nextNodeNameTextField.setToolTipText("Nom du noeud");
        nextNodeNameTextField.addActionListener((e) -> {
            panel.setNextNodeName(nextNodeNameTextField.getText());
        });
        
        updateDrawState(panel, buttonDrawNode);
        
        JPanel headContainer = new JPanel();
        BoxLayout headBox = new BoxLayout(headContainer, BoxLayout.X_AXIS);
        headContainer.setLayout(headBox);
        
        headContainer.add(nextNodeNameTextField);
        headContainer.add(buttonDrawNode);
        headContainer.add(buttonDrawLink);
        
        //Link controller panel
        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.white);
        
        GridLayout gridLayout = new GridLayout(0, 5, 5, 5);
        controlPanel.setLayout(gridLayout);
        
        panel.addLinkListener((Node node1, Node node2) -> {
            JCheckBox checkBox = new JCheckBox(node1.getName() + " - " + node2.getName());
            checkBox.setSelected(node1.getState() == Node.State.ON && node2.getState() == Node.State.ON);
            checkBox.addItemListener((a) -> {
                Link l = node1.hasLink(node2);
                Link.State state = Link.State.ON;
                if(l != null){
                    if(checkBox.isSelected() == false){
                        state = Link.State.OFF;
                    }
                    l.setState(state);
                    panel.repaint();
                }
                
            });
            controlPanel.add(checkBox);
            controlPanel.repaint();
            frame.pack();
        });
        
        //Node control panel
        JPanel nodeControlPanel = new JPanel();
        
        nodeControlPanel.setLayout(gridLayout);
        
        panel.addNodeListener((node) -> {
           JCheckBox checkBox = new JCheckBox(node.getName());
           checkBox.setSelected(node.getState() == Node.State.ON);
           checkBox.addItemListener((item)->{
               if(checkBox.isSelected() == false){
                   node.setState(Node.State.OFF);
               } else {
                   node.setState(Node.State.ON);
               }
               panel.repaint();
           });
           
           nodeControlPanel.add(checkBox);
           nodeControlPanel.repaint();
           frame.pack();
        });
        
        JPanel mainControlPanel = new JPanel();
        mainControlPanel.setLayout(new BorderLayout());
        mainControlPanel.add(controlPanel, BorderLayout.WEST);
        mainControlPanel.add(nodeControlPanel, BorderLayout.EAST);

        
        frame.add(headContainer, BorderLayout.NORTH);
        frame.add(panel);
        frame.add(mainControlPanel, BorderLayout.SOUTH);
        frame.pack();
        frame.setVisible(true);
    }
    
    public static JMenuBar createMenuBar(Panel panel){
        JMenuBar menuBar = new JMenuBar();
        
        JMenu fileMenu = new JMenu("Fichier");
        JMenuItem openItem = new JMenuItem("Ouvrir");
        JMenuItem saveItem = new JMenuItem("Enregistrer");
        
        fileMenu.add(openItem);
        fileMenu.add(saveItem);
        
        openItem.addActionListener((action) -> {
           //Open and read file 
           openAndInitializePanel(panel);
        });
        
        saveItem.addActionListener((action) -> {
           //Create and write file 
           createSaveFile(panel);
        });
        
        menuBar.add(fileMenu);
        
        return menuBar;
    }
    
    public static void createSaveFile(Panel panel){
        List<NodePosition> nodePositions = panel.getNodePosition();
        
        try {
            PrintWriter pw = new PrintWriter(new FileOutputStream("file.sav"));
            //Nombre de noeud
            pw.println(nodePositions.size());
            //Nom x y (pour chaque noeud)
            for(int i = 0; i < nodePositions.size(); i++){
                NodePosition np = nodePositions.get(i); 
                pw.println(np.node.getName() + separator + np.x + separator + np.y + separator + np.node.getState().ordinal());
            }
            for(int i = 0; i < nodePositions.size(); i++){
                NodePosition np = nodePositions.get(i);
                for(int j = 0; j < np.node.getAllLink().length; j++){
                    int index2 = getNodePositionIndex(nodePositions, np.node.getAllLink()[j].getOtherNode(np.node));
                    if(index2 >= 0){
                        pw.println(i + " " + index2);
                    }
                }
            }
            pw.flush();
            pw.close();
        } catch(Exception ex){
            JOptionPane.showMessageDialog(panel, "Une erreur c'est produite lors de l'enregistrement");
        }
    }
    
    public static void openAndInitializePanel(Panel panel){
        List<NodePosition> nodePositions = panel.getNodePosition();
        
        try {
            BufferedReader br = new BufferedReader(new FileReader("file.sav"));
            
            int nbNodePosition = Integer.parseInt(br.readLine());
            
            for(int i = 0; i < nbNodePosition; i++){
                String str = br.readLine();
                String name = str.split(separator)[0];
                int x = Integer.parseInt(str.split(separator)[1]);
                int y = Integer.parseInt(str.split(separator)[2]);
                int ordinal = Integer.parseInt(str.split(separator)[3]);
                Node.State state = Node.State.values()[ordinal];
                NodePosition nodePosition = new NodePosition(x, y, new Node(name));
                nodePosition.node.setState(state);
                nodePositions.add(nodePosition);
                panel.updateNode(nodePosition.node);
            }
            
            String str = br.readLine();
            while(str != null){
                int nodeIndex1 = Integer.parseInt(str.split(" ")[0]);
                int nodeIndex2 = Integer.parseInt(str.split(" ")[1]);
                panel.link(nodeIndex1, nodeIndex2);
                str = br.readLine();
            }
            
            br.close();
            
        } catch(Exception ex){
            JOptionPane.showMessageDialog(panel, "Une erreur c'est produite lors de l'initialisation");
        }

    }
    
    public static int getNodePositionIndex(List<NodePosition> nps, Node node){
        for(int i = 0; i < nps.size(); i++){
            if(nps.get(i).node == node){
                return i;
            }
        }
        return -1;
    }
    
    public void updateDrawState(Panel panel, JRadioButton button){
             button.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                panel.setDrawNode(e.getStateChange() == 1);
            }   
        });   
    }
}
