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
@XmlSeeAlso( { Course.class, Grade.class})
public class CourseDAO extends DAO {
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
            courses = getCourseList(param, false);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.courses;  
    }
    
    public void setCourse(List<Course> courses) {   
        this.courses = courses;     
    } 
    
    public List<Course> getCourseList(String param, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where c.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Course c join fetch c.faculty" + activeQuery;
            Query query = ses.createQuery(queryString, Course.class);
            courses = query.list();
            
            for (Course c : courses) {
                c.setCareerCourses(null);
                c.setCourses(null);
                c.setEvaluations(null);
                c.setCourseTeachers(null);
                
                c.getFaculty().setCareers(null);
                c.getFaculty().setCourses(null);
                
                //Manejo de prerrequisito
                if (c.getCourse() != null) {
                    //Creando versión simplificada del prerrequisito
                    Course prerrequisite = new Course(
                            c.getCourse().getName(), 
                            c.getCourse().getCourseCode(), 
                            c.getCourse().getSemester(), 
                            c.getCourse().getUv()
                    );
                    
                    c.setCourse(prerrequisite);
                }
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
        
    }
    
    public Course getCourse(int courseId) throws Exception {
        Course course = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Course c join fetch c.faculty where c.id = :courseId";
            Query query = ses.createQuery(queryString, Course.class);
            query.setParameter("courseId", courseId);
            course = (Course) query.uniqueResult();
            
            course.setCareerCourses(null);
            course.setCourses(null);
            course.setEvaluations(null);
            course.setCourseTeachers(null);
            
            course.getFaculty().setCareers(null);
            course.getFaculty().setCourses(null);

            //Manejo de prerrequisito
            if (course.getCourse() != null) {
                //Creando versión simplificada del prerrequisito
                Course prerrequisite = new Course(
                        course.getCourse().getName(), 
                        course.getCourse().getCourseCode(), 
                        course.getCourse().getSemester(), 
                        course.getCourse().getUv()
                );

                course.setCourse(prerrequisite);
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
        
        return course;
    }
    
    public Course getCourseByRegisteredCourse(int regCourseId) throws Exception {
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
    
    public Course get(int id) throws Exception {
        Course course = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            course = (Course) ses.get(Course.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return course;
    }
}
