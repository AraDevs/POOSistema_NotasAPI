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
public class RegisteredCourseDAO extends DAO {
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
            //regCourses = getRegisteredCourseListWithGrades(Integer.parseInt(param), false);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.regCourses;  
    }
    
    public void setRegisteredCourse(List<RegisteredCourse> regCourses) {   
        this.regCourses = regCourses;     
    } 
    
    public List<RegisteredCourse> getRegisteredCourseList(int studentId, boolean approved) throws Exception  {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String approvedQuery = "";
            if (approved) {
                approvedQuery = " and rc.courseState = 'Aprobada'";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM RegisteredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.course c where rc.student.id = :studentId" + approvedQuery;
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("studentId", studentId);
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
    
    public List<RegisteredCourse> getRegisteredCourseListWithGrades(int id, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String activeQuery = "";
            if (active) {
                activeQuery = " and rc.courseState = 'En curso'";
            }
            
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT rc FROM RegisteredCourse rc join fetch rc.grades g "
                    + "join fetch g.evaluation e join fetch rc.courseTeacher ct join fetch ct.course c "
                    + "join fetch rc.student s where rc.student.id = :id" + activeQuery;
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("id", id);
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
    
    public List<RegisteredCourse> getRegisteredCourseWithCourse(int id, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String activeQuery = "";
            if (active) {
                activeQuery = " and rc.courseState = 'En curso'";
            }
            
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT rc FROM RegisteredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.course c join fetch rc.student s where rc.student.id = :id" + activeQuery;
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
    
    public List<RegisteredCourse> getRegisteredCourseWithCourseAndTeacher(int studentId, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String activeQuery = "";
            if (active) {
                activeQuery = " and rc.courseState = 'En curso'";
            }
            
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT rc FROM RegisteredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.course c join fetch rc.student s join fetch ct.employee e "
                    + "join fetch e.user u join fetch u.person p where rc.student.id = :studentId" + activeQuery;
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("studentId", studentId);
            regCourses = query.list();
            
            for (RegisteredCourse rc : regCourses) {
                rc.setStudent(null);
                rc.setUnattendances(null);
                rc.setGrades(null);
                
                rc.getCourseTeacher().setRegisteredCourses(null);
                
                rc.getCourseTeacher().getEmployee().setRole(null);
                rc.getCourseTeacher().getEmployee().setCourseTeachers(null);
                rc.getCourseTeacher().getEmployee().getUser().setEmployees(null);
                rc.getCourseTeacher().getEmployee().getUser().setStudents(null);
                rc.getCourseTeacher().getEmployee().getUser().getPerson().setUsers(null);
                
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
    
    public List<RegisteredCourse> getRegisteredCourseByCourseTeacher(int courseTeacherId, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String activeQuery = "";
            if (active) {
                activeQuery = " and rc.courseState = 'En curso'";
            }
            
            tra = ses.beginTransaction();
            String queryString = "SELECT DISTINCT rc FROM RegisteredCourse rc join fetch rc.student s "
                    + "join fetch s.user u join fetch u.person where rc.courseTeacher.id = :courseTeacherId" + activeQuery;
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("courseTeacherId", courseTeacherId);
            regCourses = query.list();
            
            for (RegisteredCourse rc : regCourses) {
                rc.setUnattendances(null);
                rc.setGrades(null);
                
                rc.getCourseTeacher().setEmployee(null);
                rc.getCourseTeacher().setRegisteredCourses(null);
                rc.getCourseTeacher().setCourse(null);
                
                rc.getStudent().setCareerStudents(null);
                rc.getStudent().setRegisteredCourses(null);
                rc.getStudent().getUser().setEmployees(null);
                rc.getStudent().getUser().setStudents(null);
                rc.getStudent().getUser().getPerson().setUsers(null);
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
    
    public RegisteredCourse getRegisteredCourse(int id) throws Exception {
        RegisteredCourse regCourse = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM RegisteredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.course c join fetch ct.employee e "
                    + "join fetch e.user u join fetch u.person p where rc.id = :id";
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("id", id);
            regCourse = (RegisteredCourse) query.uniqueResult();
            
            regCourse.setStudent(null);
            regCourse.setUnattendances(null);
            regCourse.setGrades(null);

            regCourse.getCourseTeacher().setRegisteredCourses(null);

            regCourse.getCourseTeacher().getEmployee().setRole(null);
            regCourse.getCourseTeacher().getEmployee().setCourseTeachers(null);
            regCourse.getCourseTeacher().getEmployee().getUser().setEmployees(null);
            regCourse.getCourseTeacher().getEmployee().getUser().setStudents(null);
            regCourse.getCourseTeacher().getEmployee().getUser().getPerson().setUsers(null);

            regCourse.getCourseTeacher().getCourse().setCareerCourses(null);
            regCourse.getCourseTeacher().getCourse().setCourse(null);
            regCourse.getCourseTeacher().getCourse().setCourses(null);
            regCourse.getCourseTeacher().getCourse().setCourseTeachers(null);
            regCourse.getCourseTeacher().getCourse().setEvaluations(null);
            regCourse.getCourseTeacher().getCourse().setFaculty(null);
            
            
        } catch (HibernateException e) {
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return regCourse;
        
    }
    
    public RegisteredCourse getRegisteredCourseNiceWay(int id) throws Exception {
        RegisteredCourse regCourse = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM RegisteredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.course c join fetch ct.employee e "
                    + "join fetch e.user u join fetch u.person p join fetch rc.student s where rc.id = :id";
            Query query = ses.createQuery(queryString, RegisteredCourse.class);
            query.setParameter("id", id);
            regCourse = (RegisteredCourse) query.uniqueResult();
            
        } catch (HibernateException e) {
            e.printStackTrace();
            if(tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return regCourse;
        
    }
    
    public RegisteredCourse get(int id) throws Exception {
        RegisteredCourse registeredCourse = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            registeredCourse = (RegisteredCourse) ses.get(RegisteredCourse.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return registeredCourse;
    }
}
