/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.CourseTeacher;
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
@XmlRootElement ( name = "courseTeacherDao") 
@XmlSeeAlso( { CourseTeacher.class})
public class CourseTeacherDAO {
    private List<CourseTeacher> courseTeachers;
    String param;
    
    public CourseTeacherDAO() {
        courseTeachers = new CopyOnWriteArrayList<CourseTeacher>();        
        param = null;
    }
    
    public CourseTeacherDAO(String name) {        
        courseTeachers = new CopyOnWriteArrayList<CourseTeacher>();        
        param = name;        
    }
    
    public CourseTeacherDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            courseTeachers = new CopyOnWriteArrayList<CourseTeacher>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getCourseTeachers() {        
        try {            
            courseTeachers = getCourseTeacherList(param);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.courseTeachers;        
    }
    
    public void setCourseTeachers(List<CourseTeacher> courseTeachers) {        
        this.courseTeachers = courseTeachers;        
    }
    
    public List<CourseTeacher> getCourseTeacherList(String param) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM CourseTeacher";
            Query query = ses.createQuery(queryString, CourseTeacher.class);
            courseTeachers = query.list();
            
            for (CourseTeacher rc : courseTeachers) {
                
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
        
        return courseTeachers;
    }
    
    public CourseTeacher getCourseTeacherByRegCrs (int regCourseId) {
        CourseTeacher crsTchr = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM CourseTeacher ct join fetch ct.employee e join fetch ct.course c "
                    + "join fetch ct.registeredCourses rc where rc.id = :regCourseId";
            Query query = ses.createQuery(queryString, CourseTeacher.class);
            query.setParameter("regCourseId", regCourseId);
            crsTchr = (CourseTeacher) query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return crsTchr;
    }
    
    public String getCourseTeacherTendency (int regCourseId) {
        
        String response = "";
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            CourseTeacher crsTchr = getCourseTeacherByRegCrs(regCourseId);
            tra = ses.beginTransaction();
            
            //Aprobados
            String queryString = "SELECT count(ct) FROM CourseTeacher ct join ct.registeredCourses rc "
                    + "where ct.employee.id = :employeeId and ct.course.id = :courseId and rc.courseState = 'Aprobada'";
            Query query = ses.createQuery(queryString, Long.class);
            query.setParameter("employeeId", crsTchr.getEmployee().getId());
            query.setParameter("courseId", crsTchr.getCourse().getId());
            Long passed = (Long)query.uniqueResult();
            
            //Reprobados
            queryString = "SELECT count(ct) FROM CourseTeacher ct join ct.registeredCourses rc "
                    + "where ct.employee.id = :employeeId and ct.course.id = :courseId and rc.courseState = 'Reprobada'";
            query = ses.createQuery(queryString, Long.class);
            query.setParameter("employeeId", crsTchr.getEmployee().getId());
            query.setParameter("courseId", crsTchr.getCourse().getId());
            Long failed = (Long)query.uniqueResult();
            
            //Retirados
            queryString = "SELECT count(ct) FROM CourseTeacher ct join ct.registeredCourses rc "
                    + "where ct.employee.id = :employeeId and ct.course.id = :courseId and rc.courseState = 'Retirada'";
            query = ses.createQuery(queryString, Long.class);
            query.setParameter("employeeId", crsTchr.getEmployee().getId());
            query.setParameter("courseId", crsTchr.getCourse().getId());
            Long retired = (Long)query.uniqueResult();
            
            //Concatenando resultados en json
            response = "{\"passed\":\""+passed+"\",\"failed\":\""+failed+"\",\"retired\":\""+retired+"\"}";
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return response;
    }
}
