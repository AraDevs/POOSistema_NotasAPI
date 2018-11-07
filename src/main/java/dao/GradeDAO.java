/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.Grade;
import hibernate.HibernateUtil;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
@XmlRootElement ( name = "gradeDao") 
@XmlSeeAlso( { Grade.class})
public class GradeDAO extends DAO {
    private List<Grade> grades;
    String param;
    
    public GradeDAO() {
        grades = new CopyOnWriteArrayList<Grade>();        
        param = null;
    }
    
    public GradeDAO(String name) {        
        grades = new CopyOnWriteArrayList<Grade>();        
        param = name;        
    }
    
    public GradeDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            grades = new CopyOnWriteArrayList<Grade>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getGrades() {        
        try {            
            grades = getGradeList(param);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.grades;        
    }
    
    public void setGrades(List<Grade> grades) {        
        this.grades = grades;        
    }
    
    public List<Grade> getGradeList(String param) throws Exception {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Grade";
            Query query = ses.createQuery(queryString, Grade.class);
            grades = query.list();
            
            for (Grade rc : grades) {
                
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
        
        return grades;
    }
    
    public Grade getGrade(int regCourseId, int evalId) throws Exception {
        Grade grade = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Grade g where g.registeredCourse.id = :regCourseId "
                    + "and g.evaluation.id = :evalId";
            Query query = ses.createQuery(queryString, Grade.class);
            query.setParameter("regCourseId", regCourseId);
            query.setParameter("evalId", evalId);
            grade = (Grade) query.uniqueResult();
            
            if (grade != null) {
                grade.setCorrections(null);
                grade.setEvaluation(null);
                grade.setRegisteredCourse(null);
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
        
        return grade;
    }
    
    public Grade getGradeNiceWay(int regCourseId, int evalId) throws Exception {
        Grade grade = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Grade g where g.registeredCourse.id = :regCourseId "
                    + "and g.evaluation.id = :evalId";
            Query query = ses.createQuery(queryString, Grade.class);
            query.setParameter("regCourseId", regCourseId);
            query.setParameter("evalId", evalId);
            grade = (Grade) query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return grade;
    }
    
    public boolean hasAllGrades (int registeredCourseId, boolean laboratory) {
        boolean response = false;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String laboratoryQuery = " and e.laboratory = false";
            if (laboratory) {
                laboratoryQuery = " and e.laboratory = true";
            }
            
            //Periodo 1
            tra = ses.beginTransaction();
            String queryString = "SELECT SUM(e.percentage) FROM Grade g join g.evaluation e "
                    + "where e.period = '1' and g.registeredCourse.id = :regCourseId " + laboratoryQuery;
            Query query = ses.createQuery(queryString, Long.class);
            query.setParameter("regCourseId", registeredCourseId);
            Long percentageTotal1 = (Long) query.uniqueResult();
            
            //Periodo 2
            queryString = "SELECT SUM(e.percentage) FROM Grade g join g.evaluation e "
                    + "where e.period = '2' and g.registeredCourse.id = :regCourseId " + laboratoryQuery;
            query = ses.createQuery(queryString, Long.class);
            query.setParameter("regCourseId", registeredCourseId);
            Long percentageTotal2 = (Long) query.uniqueResult();
            
            //Periodo 3
            queryString = "SELECT SUM(e.percentage) FROM Grade g join g.evaluation e "
                    + "where e.period = '3' and g.registeredCourse.id = :regCourseId " + laboratoryQuery;
            query = ses.createQuery(queryString, Long.class);
            query.setParameter("regCourseId", registeredCourseId);
            Long percentageTotal3 = (Long) query.uniqueResult();
            
            if (percentageTotal1 == 100 && percentageTotal2 == 100 && percentageTotal3 == 100) response = true;
            
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
    
    public Grade get(int id) throws Exception {
        Grade grade = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            grade = (Grade) ses.get(Grade.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return grade;
    }
    
    public Grade getNiceWay(int id) throws Exception {
        Grade grade = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Grade g join fetch g.registeredCourse where g.id = :gradeId ";
            Query query = ses.createQuery(queryString, Grade.class);
            query.setParameter("gradeId", id);
            grade = (Grade) query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return grade;
    }
}
