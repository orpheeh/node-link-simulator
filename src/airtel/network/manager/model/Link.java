/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airtel.network.manager.model;

/**
 *
 * @author Orphe
 */
public class Link {
    
    public enum State { OFF, ON }
    
    private Node extremity1;
    private Node extremity2;
    private State state;
    
    public Link(Node ext1, Node ext2){
        this.extremity1 = ext1;
        this.extremity2 = ext2;
        this.state = State.ON;
        
        if(this.extremity1 != null){
            this.extremity1.addLink(this);
            if(this.extremity1.getState() == Node.State.OFF){
                this.state = State.OFF;
            }
        }
        
        if(this.extremity2 != null){
            this.extremity2.addLink(this);
            if(this.extremity2.getState() == Node.State.OFF){
                this.state = State.OFF;
            }
        }
    }
    
    public Node getOtherNode(Node node){
        if(this.extremity1 == node){
            return this.extremity2;
        }
        if(this.extremity2 == node){
            return this.extremity1;
        }
        return null;
    }
    
    public void setState(State newState){
        this.state = newState;
    }
    
    public State getState(){
        return this.state;
    }
    
    public Link(Node ext1){
       this(ext1, null);
    }
    
    public Link(){
        this(null, null);
    }
    
    
    public Node getExtremity1() {
        return extremity1;
    }

    public Node getExtremity2() {
        return extremity2;
    }
}
