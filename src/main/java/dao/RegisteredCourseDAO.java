/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.Course;
import hibernate.CourseTeacher;
import hibernate.Evaluation;
import hibernate.Grade;
import hibernate.HibernateUtil;
import hibernate.Person;
import hibernate.RegisteredCourse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.persistence.criteria.CriteriaBuilder;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "registeredCourseDao") 
@XmlSeeAlso( { RegisteredCourse.class, Grade.class, Evaluation.class, CourseTeacher.class, Course.class})
public class RegisteredCourseDAO {
    private List<RegisteredCourse> regCourses;
    String param;
    
    public RegisteredCourseDAO() {
        regCourses = new CopyOnWriteArrayList<RegisteredCourse>(); 
        param = null;
    }
    
    public RegisteredCourseDAO(String name) {   
        regCourses = new CopyOnWriteArrayList<RegisteredCourse>();   
        param = name;  
    }
    
    public RegisteredCourseDAO(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            regCourses = new CopyOnWriteArrayList<RegisteredCourse>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getRegisteredCourse() {   
        try {    
            regCourses = getRegisteredCourseList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.regCourses;  
    }
    
    public void setRegisteredCourse(List<RegisteredCourse> regCourses) {   
        this.regCourses = regCourses;     
    } 
    
    public List<RegisteredCourse> getRegisteredCourseList(String param) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT rc FROM RegisteredCourse rc join fetch rc.grades g "
                    + "join fetch g.evaluation e join fetch rc.courseTeacher ct join fetch ct.course c "
                    + "join fetch rc.student s where rc.student.id = :id";
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("id", Integer.parseInt(param));
            regCourses = query.list();
            
            for (RegisteredCourse rc : regCourses) {
                rc.setStudent(null);
                rc.setUnattendances(null);
                for (Object g : rc.getGrades()) {
                    ((Grade) g).setCorrections(null);
                    ((Grade) g).setRegisteredCourse(null);
                    ((Grade) g).getEvaluation().setCourse(null);
                    ((Grade) g).getEvaluation().setGrades(null);
                }
                
                rc.getCourseTeacher().setEmployee(null);
                rc.getCourseTeacher().setRegisteredCourses(null);
                
                rc.getCourseTeacher().getCourse().setCareerCourses(null);
                rc.getCourseTeacher().getCourse().setCourse(null);
                rc.getCourseTeacher().getCourse().setCourses(null);
                rc.getCourseTeacher().getCourse().setCourseTeachers(null);
                rc.getCourseTeacher().getCourse().setEvaluations(null);
                rc.getCourseTeacher().getCourse().setFaculty(null);
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return regCourses;
        
    }
    
    public List<RegisteredCourse> getRegisteredCourseWithCourse(int id) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT rc FROM RegisteredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.course c join fetch rc.student s where rc.student.id = :id";
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("id", id);
            regCourses = query.list();
            
            for (RegisteredCourse rc : regCourses) {
                rc.setStudent(null);
                rc.setUnattendances(null);
                rc.setGrades(null);
                
                rc.getCourseTeacher().setEmployee(null);
                rc.getCourseTeacher().setRegisteredCourses(null);
                
                rc.getCourseTeacher().getCourse().setCareerCourses(null);
                rc.getCourseTeacher().getCourse().setCourse(null);
                rc.getCourseTeacher().getCourse().setCourses(null);
                rc.getCourseTeacher().getCourse().setCourseTeachers(null);
                rc.getCourseTeacher().getCourse().setEvaluations(null);
                rc.getCourseTeacher().getCourse().setFaculty(null);
            }
            
        } catch (HibernateException e) {
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return regCourses;
        
    }
}
