/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.CareerCourse;
import hibernate.CareerStudent;
import hibernate.Course;
import hibernate.HibernateUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement (name = "careerCourseDao")
public class CareerCourseDAO {
    private List<CareerCourse> careerCourses;
    String param;
    
    public CareerCourseDAO() {
        careerCourses = new CopyOnWriteArrayList<CareerCourse>();        
        param = null;
    }
    
    public CareerCourseDAO(String name) {        
        careerCourses = new CopyOnWriteArrayList<CareerCourse>();        
        param = name;        
    }
    
    public CareerCourseDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            careerCourses = new CopyOnWriteArrayList<CareerCourse>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getCareerCourses() {        
        try {            
            //careerCourses = getCareerCourseList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.careerCourses;        
    }
    
    public void setCareerCourses(List<CareerCourse> careerCourses) {        
        this.careerCourses = careerCourses;        
    }
    
    public List<CareerCourse> getCareerCourseList(int careerId, boolean active) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " and cc.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM CareerCourse cc join fetch cc.career c "
                    + "join fetch cc.course where c.id = :careerId" + activeQuery;
            Query query = ses.createQuery(queryString, CareerCourse.class);
            query.setParameter("careerId", careerId);
            careerCourses = query.list();
            
            for (CareerCourse cc : careerCourses) {
                cc.setCareer(null);
                
                cc.getCourse().setCareerCourses(null);
                cc.getCourse().setEvaluations(null);
                cc.getCourse().setCourseTeachers(null);
                cc.getCourse().setFaculty(null);
                cc.getCourse().setCourses(null);
                
                //Manejo de prerrequisito
                if (cc.getCourse().getCourse() != null) {
                    //Creando versión simplificada del prerrequisito
                    Course prerrequisite = new Course(
                            cc.getCourse().getCourse().getName(), 
                            cc.getCourse().getCourse().getCourseCode(), 
                            cc.getCourse().getCourse().getSemester(), 
                            cc.getCourse().getCourse().getUv()
                    );
                    
                    cc.getCourse().setCourse(prerrequisite);
                }
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
        
        return careerCourses;
    }
    
    public CareerCourse getCareerCourse(int id) {
        CareerCourse careerCourse = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM CareerCourse cc join fetch cc.course where cc.id = :id";
            Query query = ses.createQuery(queryString, CareerCourse.class);
            query.setParameter("id", id);
            careerCourse = (CareerCourse) query.uniqueResult();
            
            careerCourse.setCareer(null);

            careerCourse.getCourse().setCareerCourses(null);
            careerCourse.getCourse().setEvaluations(null);
            careerCourse.getCourse().setCourseTeachers(null);
            careerCourse.getCourse().setFaculty(null);
            careerCourse.getCourse().setCourses(null);
            careerCourse.getCourse().setCourse(null);
                
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return careerCourse;
    }
    
    public List<CareerCourse> getCareerCourseByCareerPlan(int careerId, int plan) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM CareerCourse cc join fetch cc.career c "
                    + "join fetch cc.course where c.id = :careerId and cc.plan = :plan and cc.state = true";
            Query query = ses.createQuery(queryString, CareerCourse.class);
            query.setParameter("careerId", careerId);
            query.setParameter("plan", plan);
            careerCourses = query.list();
            
            for (CareerCourse cc : careerCourses) {
                cc.setCareer(null);
                
                cc.getCourse().setCareerCourses(null);
                cc.getCourse().setEvaluations(null);
                cc.getCourse().setCourseTeachers(null);
                cc.getCourse().setFaculty(null);
                cc.getCourse().setCourses(null);
                
                //Manejo de prerrequisito
                if (cc.getCourse().getCourse() != null) {
                    //Creando versión simplificada del prerrequisito
                    Course prerrequisite = new Course(
                            cc.getCourse().getCourse().getName(), 
                            cc.getCourse().getCourse().getCourseCode(), 
                            cc.getCourse().getCourse().getSemester(), 
                            cc.getCourse().getCourse().getUv()
                    );
                    
                    cc.getCourse().setCourse(prerrequisite);
                }
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
        
        return careerCourses;
    }
    
    public int getPlan(CareerStudent careerStudent) {
        int plan = 0;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "SELECT MAX(plan) FROM career_course cc "
                    + "inner join career c on cc.career_id = c.id "
                    + "where c.id = :careerId and plan <= :incomeYear and cc.state = true";
            Query query = ses.createSQLQuery(queryString);
            query.setParameter("careerId", careerStudent.getCareer().getId());
            query.setParameter("incomeYear", careerStudent.getIncomeYear());
            plan = (Integer) query.uniqueResult();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return plan;
    }
}