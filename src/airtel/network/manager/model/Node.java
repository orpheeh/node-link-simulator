/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package airtel.network.manager.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Orphe
 */
public class Node {
    
    String name;
    
    List<Link> links = new ArrayList<Link>();
    
    private State state;
    
    public enum State { OFF, ON };
    
    public Node(String name){
        this.name = name;
        this.state = State.ON;
    }
    
    public State getState(){
        return state;
    }
    
    public void setState(State state){
        this.state = state;

        if(this.state == State.OFF){
            for(Link l : links){
                l.setState(Link.State.OFF);
            }
        } else if(this.state == State.ON){
            for(Link l : links){
                if(l.getOtherNode(this).getState() == State.ON) {
                    l.setState(Link.State.ON);
                }
            }
        }
    }
    
    public void addLink(Link link){
       links.add(link);
    }
    
    public void removeLink(Link link){
        links.remove(link);
    }
    
    public Link[] getAllLink(){
        return links.toArray(new Link[0]);
    }
    
    public int linkTo(Node ...nodes){
        int count = 0;
        for(Node n : nodes){
            if(this.hasLink(n) == null && n != this){
                Link link = new Link(this, n);
                count++;
            }
        }
        return count;
    }
    
    public String getName(){
        return name;
    }
    
    public Link hasLink(Node node){
        for(Link l : links){
            if(l.getExtremity1() == node || l.getExtremity2() == node){
                return l;
            }
        }
        return null;
    }
   
    @Override
    public String toString(){
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append(name + " -> [");
        for(int i = 0; i < links.size(); i++){
            Node otherNode = links.get(i).getOtherNode(this);
            if(otherNode != null){
                stringBuilder.append(otherNode.name + "; ");
            }
            stringBuilder.append(" || ");
        }
        stringBuilder.append("] ");
        return stringBuilder.toString();
    }       
}

