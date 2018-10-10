/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.CareerStudent;
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
@XmlRootElement ( name = "careerStudentDao") 
public class CareerStudentDAO extends DAO {
    private List<CareerStudent> careerStudents;
    String param;
    
    public CareerStudentDAO() {
        careerStudents = new CopyOnWriteArrayList<CareerStudent>();        
        param = null;
    }
    
    public CareerStudentDAO(String name) {        
        careerStudents = new CopyOnWriteArrayList<CareerStudent>();        
        param = name;        
    }
    
    public CareerStudentDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            careerStudents = new CopyOnWriteArrayList<CareerStudent>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getCareerStudents() {        
        try {            
            //careerStudents = getCareerStudentList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.careerStudents;        
    }
    
    public void setCareerStudents(List<CareerStudent> careerStudents) {        
        this.careerStudents = careerStudents;        
    }
    
    public List<CareerStudent> getCareerStudentList(int studentId) throws Exception {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM CareerStudent cs join fetch cs.student s "
                    + "join fetch cs.career c join fetch c.faculty join fetch c.careerType "
                    + "where s.id = :studentId";
            Query query = ses.createQuery(queryString, CareerStudent.class);
            query.setParameter("studentId", studentId);
            careerStudents = query.list();
            
            for (CareerStudent cs : careerStudents) {
                cs.setStudent(null);
                
                cs.getCareer().setCareerCourses(null);
                cs.getCareer().setCareerStudents(null);
                
                cs.getCareer().getFaculty().setCareers(null);
                cs.getCareer().getFaculty().setCourses(null);
                cs.getCareer().getCareerType().setCareers(null);
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
        
        return careerStudents;
    }
    
    public CareerStudent getCareerStudent(int id) throws Exception {
        CareerStudent careerStudent = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM CareerStudent cs join fetch cs.student s "
                    + "join fetch cs.career c where cs.id = :id";
            Query query = ses.createQuery(queryString, CareerStudent.class);
            query.setParameter("id", id);
            careerStudent = (CareerStudent) query.uniqueResult();
            
            careerStudent.setStudent(null);

            careerStudent.getCareer().setCareerCourses(null);
            careerStudent.getCareer().setCareerStudents(null);
            careerStudent.getCareer().setFaculty(null);
            careerStudent.getCareer().setCareerType(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return careerStudent;
    }
    
    public CareerStudent getCurrentCareerStudentByStudent(int studentId) throws Exception {
        CareerStudent careerStudent = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM CareerStudent where career_state = 'En curso' "
                    + "and student.id = :studentId";
            Query query = ses.createQuery(queryString, CareerStudent.class);
            query.setParameter("studentId", studentId);
            careerStudent = (CareerStudent) query.uniqueResult();
            
            careerStudent.setStudent(null);
            careerStudent.getCareer().setCareerCourses(null);
            careerStudent.getCareer().setCareerStudents(null);
            careerStudent.getCareer().setCareerType(null);
            careerStudent.getCareer().setFaculty(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return careerStudent;
    }
    
    public CareerStudent get(int id) throws Exception {
        CareerStudent careerStudents = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            careerStudents = (CareerStudent) ses.get(CareerStudent.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return careerStudents;
    }
}
