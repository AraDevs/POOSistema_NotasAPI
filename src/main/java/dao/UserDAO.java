/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.HibernateUtil;
import hibernate.User;
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
@XmlRootElement ( name = "userDao") 
@XmlSeeAlso( { User.class})
public class UserDAO {
    
    private List<User> users;
    String param;
    
    public UserDAO() {
        users = new CopyOnWriteArrayList<User>(); 
        param = null;
    }
    
    public UserDAO(String name) {   
        users = new CopyOnWriteArrayList<User>();   
        param = name;  
    }
    
    public UserDAO(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            users = new CopyOnWriteArrayList<User>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getUser() {   
        try {    
            //users = getUserList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.users;  
    }
    
    public void setUser(List<User> users) {   
        this.users = users;     
    } 
    
    /**
     * 
     * @param username
     * @param pass
     * @return El id del usuario logeado si la autenticacion es exitosa, 0 si la autenticacion falla
     */
    public int login(String username, String pass) {  
        int response = 0;
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "from User where username = :username and pass = :pass";
            Query query = ses.createQuery(queryString);
            query.setParameter("username", username);
            query.setParameter("pass", pass);
            response = ((User) query.uniqueResult()).getId();
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return response;
    }
}
