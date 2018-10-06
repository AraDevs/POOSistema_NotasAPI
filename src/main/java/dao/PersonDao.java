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
public class PersonDao {
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
            people = getPeopleList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.people;  
    }
    
    public void setPeople(List<Person> people) {   
        this.people = people;     
    }
    
    
    public List<Person> getPeopleList(String param) {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            /*tra = ses.beginTransaction();
            String queryString = "FROM Person p INNER JOIN FETCH p.users";
            Query query = ses.createQuery(queryString, Person.class);
            people = query.list();

            for (Person p : people) {
                for (Object u : p.getUsers()) {
                    ((User) u).setEmployees(null);
                    ((User) u).setStudents(null);
                    ((User) u).setPerson(null);
                }
            }*/
            
            tra = ses.beginTransaction();
            String queryString = ""
                    + "FROM Person p join fetch p.users";
            Query query = ses.createQuery(queryString, Person.class);
            people = query.list();
            
            
            /*for (Person p : result) {
                PersonDTO dto = new PersonDTO(
                        p.getId(), 
                        p.getName(), 
                        p.getSurname(), 
                        p.getPhone(),
                        p.getDui(),
                        p.getEmail(), 
                        p.getAddress(), 
                        p.getState(), 
                        new HashSet<UserDTO>());
                
                for (User u : p.getUsers()) {
                    
                }
            }*/


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
    
    public int add(Person person) {
        int response;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            ses.save(person);
            ses.getTransaction().commit();
            response = DaoStatus.OK;
        }
        catch (ConstraintViolationException e) {
            response = DaoStatus.CONSTRAINT_VIOLATION;
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        }
        catch (Exception e) {
            response = DaoStatus.ERROR;
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        }
        finally {
            //ses.flush(); //Ver porqué esto se chingaba
            ses.close();
        }
        return response;
    }
}
