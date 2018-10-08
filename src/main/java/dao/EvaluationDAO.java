/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.Course;
import hibernate.Evaluation;
import hibernate.Grade;
import hibernate.HibernateUtil;
import java.util.HashSet;
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
@XmlRootElement ( name = "evaluationDao") 
@XmlSeeAlso( {Evaluation.class, Grade.class} )
public class EvaluationDAO {
    private List<Evaluation> evaluations;
    String param;
    
    public EvaluationDAO() {
        evaluations = new CopyOnWriteArrayList<Evaluation>();        
        param = null;
    }
    
    public EvaluationDAO(String name) {        
        evaluations = new CopyOnWriteArrayList<Evaluation>();        
        param = name;        
    }
    
    public EvaluationDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            evaluations = new CopyOnWriteArrayList<Evaluation>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getEvaluations() {        
        try {            
            //evaluations = getEvaluationList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.evaluations;        
    }
    
    public void setEvaluations(List<Evaluation> evaluations) {        
        this.evaluations = evaluations;        
    }
    
    public Evaluation getEvaluation(int id) {
        Evaluation evaluation = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Evaluation where id = :id";
            Query query = ses.createQuery(queryString, Evaluation.class);
            query.setParameter("id", id);
            evaluation = (Evaluation) query.uniqueResult();
            
            evaluation.setCourse(null);
            evaluation.setGrades(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return evaluation;
    }
    
    public List<Evaluation> getEvaluationByCourse(int courseId, boolean active) {
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " and state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Evaluation e where e.course.id = :courseId" + activeQuery; //Recordar que state = false significa que la evaluación es de un ciclo pasado
            Query query = ses.createQuery(queryString, Evaluation.class);
            query.setParameter("courseId", courseId);
            evaluations = query.list();
            
            for (Evaluation e : evaluations) {
                e.setCourse(null);
                e.setGrades(null);
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
        
        return evaluations;
    }
    
    public List<Evaluation> getEvaluationsByRegCourseWithGrade(int regCourseId) {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            //Obteniendo materia para obtener su id
            Course course =  new CourseDAO().getCourseByRegisteredCourse(regCourseId);
            
            tra = ses.beginTransaction();
            String queryString = "FROM Evaluation e where e.course.id = :courseId and e.state = true"; //el manejo de estado es por las evaluaciones de periodos pasados
            Query query = ses.createQuery(queryString, Evaluation.class);
            query.setParameter("courseId", course.getId());
            evaluations = query.list();
            
            for (Evaluation e : evaluations) {
                e.setCourse(null);
                
                //Obteniendo notas
                Grade grade = new GradeDAO().getGrade(regCourseId, e.getId());
                HashSet<Grade> grades = new HashSet<Grade>();
                if (grade != null) {
                    grades.add(grade);
                }
                else { //Si no ha sido evaluado, se añadirá una nota de 0
                    grade = new Grade();
                    grade.setGrade(0);
                    grade.setState(false);
                    grades.add(grade);
                }
                e.setGrades(grades);
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
        
        return evaluations;
    }
    
    public Evaluation getEvaluationWithGrade(int regCourseId, int evaluationId) {
        Evaluation evaluation = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Evaluation where id = :evaluationId and state = true"; //el manejo de estado es por las evaluaciones de periodos pasados
            Query query = ses.createQuery(queryString, Evaluation.class);
            query.setParameter("evaluationId", evaluationId);
            evaluation = (Evaluation) query.uniqueResult();
            
            evaluation.setCourse(null);

            //Obteniendo notas
            Grade grade = new GradeDAO().getGrade(regCourseId, evaluation.getId());
            HashSet<Grade> grades = new HashSet<Grade>();
            if (grade != null) {
                grades.add(grade);
            }
            else { //Si no ha sido evaluado, se añadirá una nota de 0
                grade = new Grade();
                grade.setGrade(0);
                grade.setState(false);
                grades.add(grade);
            }
            evaluation.setGrades(grades);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return evaluation;
    }
}
