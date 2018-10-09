/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import helpers.DaoStatus;
import hibernate.HibernateUtil;
import hibernate.Person;
import hibernate.User;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.persistence.Tuple;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "personDao") 
@XmlSeeAlso( { Person.class, User.class})
public class PersonDao extends DAO {
    private List<Person> people;
    String param;
    
    public PersonDao() {
        people = new CopyOnWriteArrayList<Person>(); 
        param = null;
    }
    
    public PersonDao(String name) {   
        people = new CopyOnWriteArrayList<Person>();   
        param = name;  
    }
    
    public PersonDao(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            people = new CopyOnWriteArrayList<Person>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getPeople() {   
        try {    
            people = getPeopleList(param, false);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.people;  
    }
    
    public void setPeople(List<Person> people) {   
        this.people = people;     
    }
    
    
    public List<Person> getPeopleList(String param, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Person" + activeQuery;
            Query query = ses.createQuery(queryString, Person.class);
            people = query.list();

            for (Person p : people) {
                p.setUsers(null);
            }


        } catch (HibernateException e) {
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return people;
    }
    
    public Person getPerson (int id) throws Exception {
        Person person = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Person where id = :id";
            Query query = ses.createQuery(queryString, Person.class);
            query.setParameter("id", id);
            person = (Person) query.uniqueResult();
            
            person.setUsers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return person;
    }
    
    public Person getPersonByEmail (String email, int userId) throws Exception {
        //si el id es 0, esta operación se usa para verificar al guardar
        //si no, se usa para verificar al modificar
        Person person = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String idQuery = "";
            if (userId != 0) {
                idQuery = " and u.id != :userId";
            }
                                                             //|
            tra = ses.beginTransaction();         //right join V porque también se usa para obtener a la persona recien agregada y crear su usuario
            String queryString = "SELECT p FROM User u right join u.person p where p.email = :email" + idQuery;
            Query query = ses.createQuery(queryString, Person.class);
            query.setParameter("email", email);
            if (userId != 0) query.setParameter("userId", userId);
            person = (Person) query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return person;
    }
    
    public Person getPersonByDui (String dui, int userId) throws Exception {
        //si el id es 0, esta operación se usa para verificar al guardar
        //si no, se usa para verificar al modificar
        Person person = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String idQuery = "";
            if (userId != 0) {
                idQuery = " and u.id != :userId";
            }
            
            tra = ses.beginTransaction();
            String queryString = "SELECT p FROM User u join u.person p where p.dui = :dui" + idQuery;
            Query query = ses.createQuery(queryString, Person.class);
            query.setParameter("dui", dui);
            if (userId != 0) query.setParameter("userId", userId);
            person = (Person) query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return person;
    }
    
    public Person get(int id) throws Exception {
        Person person = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            person = (Person) ses.get(Person.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return person;
    }
    
}
