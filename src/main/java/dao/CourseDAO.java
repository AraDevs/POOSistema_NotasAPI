/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.Course;
import hibernate.Grade;
import hibernate.HibernateUtil;
import hibernate.RegisteredCourse;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
@XmlRootElement ( name = "courseDao") 
@XmlSeeAlso( { Course.class})
public class CourseDAO {
    private List<Course> courses;
    String param;
    
    public CourseDAO() {
        courses = new CopyOnWriteArrayList<Course>(); 
        param = null;
    }
    
    public CourseDAO(String name) {   
        courses = new CopyOnWriteArrayList<Course>();   
        param = name;  
    }
    
    public CourseDAO(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            courses = new CopyOnWriteArrayList<Course>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getCourse() {   
        try {    
            //courses = getCourseList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.courses;  
    }
    
    public void setCourse(List<Course> courses) {   
        this.courses = courses;     
    } 
    /*
    public List<Course> getCourseList(String param) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Course c join fetch rc.grades g"
                    + "join fetch g.evaluations e join fetch rc.courseTeacher ct join fetch ct.course c";
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            courses = query.list();
            
            for (RegisteredCourse rc : courses) {
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
        
        return courses;
        
    }*/
    
    public Course getCourseByRegisteredCourse(int regCourseId) {
        Course course = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Course c join fetch c.courseTeachers ct "
                    + "join fetch ct.registeredCourses rc where rc.id = :regCourseId";
            Query query = ses.createQuery(queryString, Course.class);
            query.setParameter("regCourseId", regCourseId);
            course = (Course) query.uniqueResult();
            
            course.setCareerCourses(null);
            course.setCourse(null);
            course.setCourses(null);
            course.setEvaluations(null);
            course.setFaculty(null);
            course.setCourseTeachers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return course;
    }
}
