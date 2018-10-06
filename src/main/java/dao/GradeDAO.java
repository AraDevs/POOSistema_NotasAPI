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
public class GradeDAO {
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
    
    public List<Grade> getGradeList(String param) {
        
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
    
    public Grade getGrade(int regCourseId, int evalId) {
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
}
