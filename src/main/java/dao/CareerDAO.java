/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.Career;
import hibernate.HibernateUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "careerDao") 
@XmlSeeAlso( {Career.class} )
public class CareerDAO {
    private List<Career> careers;
    String param;
    
    public CareerDAO() {
        careers = new CopyOnWriteArrayList<Career>();        
        param = null;
    }
    
    public CareerDAO(String name) {        
        careers = new CopyOnWriteArrayList<Career>();        
        param = name;        
    }
    
    public CareerDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            careers = new CopyOnWriteArrayList<Career>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getCareers() {        
        try {            
            careers = getCareerList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.careers;        
    }
    
    public void setCareers(List<Career> careers) {        
        this.careers = careers;        
    }
    
    public List<Career> getCareerList(String param, boolean active) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where c.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Career c join fetch c.faculty join fetch c.careerType" + activeQuery;
            Query query = ses.createQuery(queryString, Career.class);
            careers = query.list();
            
            for (Career c : careers) {
                //voy aquí xd
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return careers;
    }
}
