/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

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
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "facultyDao") 
@XmlSeeAlso( {Faculty.class} )
public class FacultyDAO {
    
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
            faculties = getFacultyList(param);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.faculties;        
    }
    
    public void setFaculties(List<Faculty> faculties) {        
        this.faculties = faculties;        
    }
    
    public List<Faculty> getFacultyList(String param) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Faculty";
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
}
