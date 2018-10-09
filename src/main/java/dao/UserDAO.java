/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.HibernateUtil;
import hibernate.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.Hibernate;
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
public class UserDAO extends DAO {
    
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
    
    public List<User> getUserList(String param, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where u.state = true and p.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM User u join fetch u.person p" + activeQuery;
            Query query = ses.createQuery(queryString, User.class);
            users = query.list();
            
            for (User u : users) {
                u.setEmployees(null);
                u.setStudents(null);
                u.setPass(null);
                
                u.getPerson().setUsers(null);
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
        
        return users;
    }
    
    public List<User> getUsersNiceWay(String param, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where u.state = true and p.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM User u join fetch u.person p" + activeQuery;
            Query query = ses.createQuery(queryString, User.class);
            users = query.list();
            
            for (User u : users) {
                Hibernate.initialize(u.getStudents());
                Hibernate.initialize(u.getEmployees());
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
        
        return users;
    }
    
    //Obtiene la lista de usuarios que no tienen registro de estudiante/empleado
    public List<User> getDetachedUsers() throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM User u join fetch u.person";
            Query query = ses.createQuery(queryString, User.class);
            users = query.list();
            
            List<User> detachedUsers = new ArrayList<User>();
            
            //Obteniendo solamente los usuarios 'sueltos'
            for (User u : users) {
                if (u.getEmployees().isEmpty() && u.getStudents().isEmpty()) {
                    u.setEmployees(null);
                    u.setStudents(null);
                    u.setPass(null);

                    u.getPerson().setUsers(null);
                    
                    detachedUsers.add(u);
                }
                else {
                    
                }
            }
            
            users = detachedUsers;
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return users;
    }
    
    public User getUser (int id) throws Exception {
        User user = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM User u join fetch u.person where u.id = :id";
            Query query = ses.createQuery(queryString, User.class);
            query.setParameter("id", id);
            user = (User) query.uniqueResult();
            
            user.setEmployees(null);
            user.setStudents(null);
            user.setPass(null);

            user.getPerson().setUsers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return user;
    }
    
    /**
     * 
     * @param username
     * @param pass
     * @return El id del usuario logeado si la autenticacion es exitosa, 0 si la autenticacion falla
     */
    public int login(String username, String pass) throws Exception {  
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
    
    public int getYearIndex() throws Exception {
        int id = 0;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            //Obteniendo máximo id de este año
            String queryString = "SELECT MAX(id) FROM User u where username like concat('%',:year,'%')";
            Query query = ses.createQuery(queryString, Integer.class);
            query.setParameter("year", Calendar.getInstance().get(Calendar.YEAR));
            Integer registerId = (Integer) query.uniqueResult();
            
            //Obteniendo número de username del id obtenido
            queryString = "SELECT username FROM User u where id = :id";
            query = ses.createQuery(queryString, String.class);
            query.setParameter("id", registerId);
            String username = (String) query.uniqueResult();
            
            id = Integer.parseInt(username.substring(6, 10));
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return id;
    }
    
    public User get(int id) throws Exception {
        User user = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            user = (User) ses.get(User.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return user;
    }
}
