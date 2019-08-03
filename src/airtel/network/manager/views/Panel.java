/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airtel.network.manager.views;

import airtel.network.manager.model.Link;
import airtel.network.manager.model.Node;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JPanel;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Orphe
 */
public class Panel extends JPanel implements MouseListener, MouseMotionListener {
    
    private boolean drawNode;
    private String nextNodeName;
    
    private List<Node> nodes;
    private List<NodePosition> nodePositions;
    public static int NODE_RADIUS = 10;
    private int clickCount = 0;
    private NodePosition[] nodePair = new NodePosition[2];
    private List<AddLinkListener> addLinkListeners;
    private List<NodeListener> nodeListeners;
    private int mousePos[] = new int[2];
    
    
    public Panel(List<Node> nodes, int width, int height){
        super();
        this.nodes = nodes;
        this.drawNode = false;
        this.nextNodeName = "";
        this.nodePositions = new ArrayList<NodePosition>();
        this.addLinkListeners = new ArrayList<AddLinkListener>();
        this.nodeListeners = new ArrayList<NodeListener>();
        this.setPreferredSize(new Dimension(width, height));
        this.setMinimumSize(new Dimension(width, height));
        this.setMaximumSize(new Dimension(width, height));
        
        this.setComponentPopupMenu(createPopupMenu());
        
    }
    
    private JPopupMenu createPopupMenu(){
        JPopupMenu popupMenu = new JPopupMenu();
        
        //JMenuItem delete
        JMenuItem deleteMenuItem = new JMenuItem("Supprimer");
        popupMenu.add(deleteMenuItem);
        
        popupMenu.addPopupMenuListener(new PopupMenuListener(){
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                //Si il y'a collision entre la souris un noeud
                //Ne rien faire
                //Sinon rendre deleteMenuItem Inactif
                deleteMenuItem.setEnabled(false);
                for(NodePosition np : nodePositions){
                    if(detectedCollision(mousePos[0], mousePos[1], np)){
                        deleteMenuItem.setEnabled(true);
                        deleteMenuItem.addActionListener(new ActionListener(){
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                //Supprimer le noeud
                                System.out.println("Supprimer le noeud:" );
                                System.out.println(np.node);

                                nodePositions.remove(np);
                                nodes.remove(np.node);
                                repaint();
                            }
                        });
                    }
                }
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
            
        });
        
        return popupMenu;
    }
    
    public void addLinkListener(AddLinkListener all){
        addLinkListeners.add(all);
    }
    
    public List<NodePosition> getNodePosition(){
        return nodePositions;
    }
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for(NodePosition nodePosition : nodePositions){
           for(Link l : nodePosition.node.getAllLink()){
               /*if(l.getType() == Link.Type.OPTIC_FIBER){
                   g2d.setStroke(new BasicStroke(8));
               } else if(l.getType() == Link.Type.HERTZ){
                      float[] dashPattern = { 30, 10, 10, 10 };
                        g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER, 10,
                            dashPattern, 0));
               }*/
               NodePosition np1 = getNodePosition(l.getExtremity1());
               NodePosition np2 = getNodePosition(l.getExtremity2());
               if(np1 != null && np2 != null){
                g2d.setColor(l.getState().equals(Link.State.OFF) ? Color.red : Color.green);
                g2d.drawLine(np1.x, np1.y, np2.x, np2.y);
               }
           }
        }
        
        for(NodePosition nodePosition : nodePositions){
            int nodex = nodePosition.x - NODE_RADIUS;
            int nodey = nodePosition.y - NODE_RADIUS;
            g2d.setColor(nodePosition.node.getState() == Node.State.ON ? Color.blue : Color.red);
            g2d.fillOval(nodex, nodey, 2*NODE_RADIUS, 2*NODE_RADIUS);
            
            FontMetrics fm = g2d.getFontMetrics();
            String str = nodePosition.node.getName();

            //Dessiner le drapeau
            int fw = 20 + fm.stringWidth(str);
            int fh = 20 + fm.getHeight();
            int p = 20 + fh;
            
            g2d.fillRect(nodex - fw, nodey - p, fw, fh);
            g2d.fillRect(nodex, nodey - p, 5, p);
            
            g2d.setColor(Color.white);
            g2d.drawString(str, nodex - fw + 10, nodey - p + 10 + fm.getAscent());
        }
    }

    public void setDrawNode( boolean b){
        drawNode = b;
    }
    
    
    public void setNextNodeName(String name){
        this.nextNodeName = name;
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
            
        if(drawNode){
            //Ajouter un noeud
            //Position de la souris

            //Le nouveau noeud
            Node node =  new Node(nextNodeName);
            nodes.add(node);
            nodePositions.add(new NodePosition(x, y, node));
            for(NodeListener nl : nodeListeners){
                nl.update(node);
            }
        } else {
            for(NodePosition np : nodePositions){
                if(detectedCollision(x, y, np)){
                    ++clickCount;
                    if(clickCount <= 2){
                        nodePair[clickCount-1] = np;
                    } else {
                        clickCount = 0;
                        if(nodePair[0].node.linkTo(nodePair[1].node) == 1){
                            for(AddLinkListener all : addLinkListeners){
                                all.update(nodePair[0].node, nodePair[1].node);
                            }
                        }
                        nodePair = new NodePosition[2];
                        
                        for(Node node : nodes){
                            System.out.println(node);
                        }
                    }
                }
            }
        }
        this.repaint();
    }

    public void link(int nodeIndex1, int nodeIndex2){
        Node node1 = nodePositions.get(nodeIndex1).node;
        Node node2 = nodePositions.get(nodeIndex2).node;

        if(node1.linkTo(node2) == 1){
            for(AddLinkListener all : addLinkListeners){
                all.update(node1, node2);
            }
        }
    }
    
    public void updateNode(Node node){
        for(NodeListener nl : nodeListeners){
            nl.update(node);
        }
    }
    
    public void addNodeListener(NodeListener listener){
        nodeListeners.add(listener);
    }
    
    public boolean detectedCollision(int x, int y, NodePosition np){
        return (x - np.x) * (x - np.x) + (y - np.y)*(y - np.y) <= NODE_RADIUS * NODE_RADIUS;
    }
    
    public NodePosition getNodePosition(Node node){
        for(NodePosition np : nodePositions){
            if(np.node == node){
                return np;
            }
        }
        return null;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        mousePos[0] = e.getX();
        mousePos[1] = e.getY();
    }
}

class NodePosition {
    public int x;
    public int y;
    public Node node;
    
    public NodePosition(int x, int y, Node node){
        this.x = x;
        this.y = y;
        this.node = node;
    }
}

interface AddLinkListener {
    void update(Node node1, Node node2);
}

interface NodeListener {
    void update(Node node);
}
