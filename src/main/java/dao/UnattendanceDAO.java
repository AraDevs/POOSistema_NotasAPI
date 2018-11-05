/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.HibernateUtil;
import hibernate.Unattendance;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement (name = "unattendanceDao")
public class UnattendanceDAO extends DAO {
    private List<Unattendance> unattendances;
    String param;
    
    public UnattendanceDAO() {
        unattendances = new CopyOnWriteArrayList<Unattendance>();        
        param = null;
    }
    
    public UnattendanceDAO(String name) {        
        unattendances = new CopyOnWriteArrayList<Unattendance>();        
        param = name;        
    }
    
    public UnattendanceDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            unattendances = new CopyOnWriteArrayList<Unattendance>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getUnattendances() {        
        try {            
            //unattendances = getUnattendanceList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.unattendances;        
    }
    
    public void setUnattendances(List<Unattendance> unattendances) {        
        this.unattendances = unattendances;        
    }
    
    public List<Unattendance> getUnattendancesByRegCourse(int registeredCourseId) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Unattendance u join fetch u.registeredCourse rc where rc.id = :registeredCourseId";
            Query query = ses.createQuery(queryString, Unattendance.class);
            query.setParameter("registeredCourseId", registeredCourseId);
            unattendances = query.list();
            
            for (Unattendance u : unattendances) {
                u.setRegisteredCourse(null);
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
        
        return unattendances;
    }
    
    public Unattendance getUnattendanceByRegCourseAndDate(int registeredCourseId, Date date) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        Unattendance unattendance = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Unattendance u join fetch u.registeredCourse rc where rc.id = :registeredCourseId "
                    + "and u.unattendanceDate = :date";
            Query query = ses.createQuery(queryString, Unattendance.class);
            query.setParameter("registeredCourseId", registeredCourseId);
            query.setParameter("date", date);
            unattendance = (Unattendance)query.uniqueResult();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return unattendance;
    }
    
    public List<Unattendance> getUnattendancesByStudent(int studentId) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Unattendance u join fetch u.registeredCourse rc join fetch rc.student s "
                    + "where s.id = :studentId";
            Query query = ses.createQuery(queryString, Unattendance.class);
            query.setParameter("studentId", studentId);
            unattendances = query.list();
            
            for (Unattendance u : unattendances) {
                u.getRegisteredCourse().setStudent(null);
                u.getRegisteredCourse().setGrades(null);
                u.getRegisteredCourse().setUnattendances(null);
                
                u.getRegisteredCourse().getCourseTeacher().getCourse().setCourse(null);
                u.getRegisteredCourse().getCourseTeacher().getCourse().setCareerCourses(null);
                u.getRegisteredCourse().getCourseTeacher().getCourse().setCourseTeachers(null);
                u.getRegisteredCourse().getCourseTeacher().getCourse().setCourses(null);
                u.getRegisteredCourse().getCourseTeacher().getCourse().setEvaluations(null);
                u.getRegisteredCourse().getCourseTeacher().getCourse().setFaculty(null);
                
                u.getRegisteredCourse().getCourseTeacher().setRegisteredCourses(null);
                u.getRegisteredCourse().getCourseTeacher().getEmployee().setRole(null);
                u.getRegisteredCourse().getCourseTeacher().getEmployee().setCourseTeachers(null);
                u.getRegisteredCourse().getCourseTeacher().getEmployee().getUser().setEmployees(null);
                u.getRegisteredCourse().getCourseTeacher().getEmployee().getUser().setStudents(null);
                u.getRegisteredCourse().getCourseTeacher().getEmployee().getUser().getPerson().setUsers(null);
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
        
        return unattendances;
    }
    
    public Unattendance getUnattendance(int id) throws Exception {
        Unattendance unattendance = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            unattendance = (Unattendance) ses.get(Unattendance.class, id);
            
            Hibernate.initialize(unattendance.getRegisteredCourse());
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return unattendance;
    }
}
