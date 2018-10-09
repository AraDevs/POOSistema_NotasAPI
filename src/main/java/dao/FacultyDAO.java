/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import helpers.DaoStatus;
import hibernate.Faculty;
import hibernate.HibernateUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "facultyDao") 
@XmlSeeAlso( {Faculty.class} )
public class FacultyDAO extends DAO {
    
    private List<Faculty> faculties;
    String param;
    
    public FacultyDAO() {
        faculties = new CopyOnWriteArrayList<Faculty>();        
        param = null;
    }
    
    public FacultyDAO(String name) {        
        faculties = new CopyOnWriteArrayList<Faculty>();        
        param = name;        
    }
    
    public FacultyDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            faculties = new CopyOnWriteArrayList<Faculty>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getFaculties() {        
        try {            
            faculties = getFacultyList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.faculties;        
    }
    
    public void setFaculties(List<Faculty> faculties) {        
        this.faculties = faculties;        
    }
    
    public List<Faculty> getFacultyList(String param, boolean active) throws Exception {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Faculty" + activeQuery;
            Query query = ses.createQuery(queryString, Faculty.class);
            faculties = query.list();
            
            for (Faculty f : faculties) {
                f.setCareers(null);
                f.setCourses(null);
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
        
        return faculties;
    }
    
    public Faculty get(int id) throws Exception {
        Faculty faculty = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            faculty = (Faculty) ses.get(Faculty.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return faculty;
    }
    
    public Faculty getFaculty(int id) throws Exception {
        Faculty faculty = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Faculty where id = :id";
            Query query = ses.createQuery(queryString, Faculty.class);
            query.setParameter("id", id);
            faculty = (Faculty) query.uniqueResult();
            
            faculty.setCareers(null);
            faculty.setCourses(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return faculty;
    }
}
