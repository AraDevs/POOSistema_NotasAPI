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
public class EvaluationDAO extends DAO {
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
    
    public Evaluation getEvaluation(int id) throws Exception {
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
    
    public Evaluation getEvaluationWithCourse(int id) throws Exception {
        Evaluation evaluation = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Evaluation e join fetch e.course where e.id = :id";
            Query query = ses.createQuery(queryString, Evaluation.class);
            query.setParameter("id", id);
            evaluation = (Evaluation) query.uniqueResult();
            
            evaluation.getCourse().setCareerCourses(null);
            evaluation.getCourse().setCourse(null);
            evaluation.getCourse().setCourseTeachers(null);
            evaluation.getCourse().setCourses(null);
            evaluation.getCourse().setEvaluations(null);
            evaluation.getCourse().setFaculty(null);
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
    
    public List<Evaluation> getEvaluationByCourse(int courseId, boolean active) throws Exception {
        
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
    
    public List<Evaluation> getEvaluationsByRegCourseWithGrade(int regCourseId, boolean laboratory) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            String laboratoryQuery = " and e.laboratory = false";
            if (laboratory) {
                laboratoryQuery = " and e.laboratory = true";
            }
            
            //Obteniendo materia para obtener su id
            Course course =  new CourseDAO().getCourseByRegisteredCourse(regCourseId);
            
            tra = ses.beginTransaction();
            String queryString = "FROM Evaluation e where e.course.id = :courseId and e.state = true" + laboratoryQuery; //el manejo de estado es por las evaluaciones de periodos pasados
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
    
    public Evaluation getEvaluationWithGrade(int regCourseId, int evaluationId) throws Exception {
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
    
    public Boolean isPercentageConsistent(Evaluation evaluation) throws Exception {
        
        Boolean response = true;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            //Si es modificación, no se debe tomar en cuenta su porcentaje anterior
            String minusQuery = "";
            if (evaluation.getId() != null) {
                minusQuery = " and id != :id";
            }
            
            tra = ses.beginTransaction();
            String queryString = "SELECT SUM(percentage) + :percentage FROM Evaluation where state = true and course.id = :courseId "
                    + "and period = :period and laboratory = :laboratory" + minusQuery;
            Query query = ses.createQuery(queryString, Long.class);
            query.setParameter("percentage", new Long(evaluation.getPercentage()));
            query.setParameter("courseId", evaluation.getCourse().getId());
            query.setParameter("period", evaluation.getPeriod());
            query.setParameter("laboratory", evaluation.getLaboratory());
            if (evaluation.getId() != null) {
                query.setParameter("id", evaluation.getId());
            }
            Long percentageTotal = (Long) query.uniqueResult();
            
            if (percentageTotal > 100) response = false;
            
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
    
    public Evaluation get(int id) throws Exception {
        Evaluation evaluation = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            evaluation = (Evaluation) ses.get(Evaluation.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return evaluation;
    }
}
