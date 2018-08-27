/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement( name = "faculty" )
public class Faculty {
    private int id;
    private String name;
    private Boolean state;

    public Faculty() {
    }

    public Faculty(int id, String name, Boolean state) {
        this.id = id;
        this.name = name;
        this.state = state;
    }
    
    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
    
    
}
