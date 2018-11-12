/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import dto.CorrectionDTO;
import hibernate.Correction;
import hibernate.HibernateUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.xml.bind.annotation.XmlElement;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

/**
 *
 * @author kevin
 */
public class CorrectionDAO extends DAO {
    private List<Correction> corrections;
    String param;
    
    public CorrectionDAO() {
        corrections = new CopyOnWriteArrayList<Correction>();        
        param = null;
    }
    
    public CorrectionDAO(String name) {        
        corrections = new CopyOnWriteArrayList<Correction>();        
        param = name;        
    }
    
    public CorrectionDAO(boolean charge) {        
        if (charge == false) {
            //don't load data   
        } else {            
            corrections = new CopyOnWriteArrayList<Correction>();
            param = null;            
        }
    }
    
    @XmlElement    
    public List getCorrections() {        
        try {            
            //corrections = getCorrectionList(param, false);            
        } catch (Exception e) {            
            e.printStackTrace();            
        }        
        return this.corrections;        
    }
    
    public void setCorrections(List<Correction> corrections) {        
        this.corrections = corrections;        
    }
    
    public List<CorrectionDTO> getCorrectionsDTOByStudent(int studentId) {
        
        List<CorrectionDTO> correctionDTOs = new ArrayList<CorrectionDTO>();
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Correction c join fetch c.grade g join fetch g.evaluation ev "
                    + "join fetch ev.course c join fetch g.registeredCourse rc join fetch rc.student s "
                    + "join fetch rc.courseTeacher ct join fetch ct.employee em join fetch em.user u "
                    + "join fetch u.person where rc.courseState = 'En curso' and s.id = :studentId";
            Query query = ses.createQuery(queryString, Correction.class);
            query.setParameter("studentId", studentId);
            corrections = query.list();
            
            for (Correction c : corrections) {
                c.getGrade().setCorrections(null);
                
                c.getGrade().getEvaluation().getCourse().setCourse(null);
                c.getGrade().getEvaluation().getCourse().setCareerCourses(null);
                c.getGrade().getEvaluation().getCourse().setCourseTeachers(null);
                c.getGrade().getEvaluation().getCourse().setCourses(null);
                c.getGrade().getEvaluation().getCourse().setEvaluations(null);
                c.getGrade().getEvaluation().getCourse().setFaculty(null);
                c.getGrade().getEvaluation().setGrades(null);
                
                String teacherName = 
                        c.getGrade().getRegisteredCourse().getCourseTeacher().getEmployee().getUser().getPerson().getName() + " " +
                        c.getGrade().getRegisteredCourse().getCourseTeacher().getEmployee().getUser().getPerson().getSurname();
                
                c.getGrade().setRegisteredCourse(null);
                
                correctionDTOs.add(new CorrectionDTO(c, teacherName));
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
        
        return correctionDTOs;
    }
    
    public List<CorrectionDTO> getCorrectionsDTOByTeacher(int employeeId) {
        
        List<CorrectionDTO> correctionDTOs = new ArrayList<CorrectionDTO>();
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Correction c join fetch c.grade g join fetch g.evaluation ev "
                    + "join fetch ev.course c join fetch g.registeredCourse rc join fetch rc.student s "
                    + "join fetch s.user u join fetch u.person join fetch rc.courseTeacher ct "
                    + "join fetch ct.employee em where rc.courseState = 'En curso' and em.id = :employeeId";
            Query query = ses.createQuery(queryString, Correction.class);
            query.setParameter("employeeId", employeeId);
            corrections = query.list();
            
            for (Correction c : corrections) {
                c.getGrade().setCorrections(null);
                
                c.getGrade().getEvaluation().getCourse().setCourse(null);
                c.getGrade().getEvaluation().getCourse().setCareerCourses(null);
                c.getGrade().getEvaluation().getCourse().setCourseTeachers(null);
                c.getGrade().getEvaluation().getCourse().setCourses(null);
                c.getGrade().getEvaluation().getCourse().setEvaluations(null);
                c.getGrade().getEvaluation().getCourse().setFaculty(null);
                c.getGrade().getEvaluation().setGrades(null);
                
                String studentName = 
                        c.getGrade().getRegisteredCourse().getStudent().getUser().getPerson().getName() + " " +
                        c.getGrade().getRegisteredCourse().getStudent().getUser().getPerson().getSurname();
                
                int registeredCourseId = c.getGrade().getRegisteredCourse().getId();
                
                c.getGrade().setRegisteredCourse(null);
                
                CorrectionDTO corDTO = new CorrectionDTO(c, studentName);
                corDTO.setRegisteredCourseId(registeredCourseId);
                
                correctionDTOs.add(corDTO);
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
        
        return correctionDTOs;
    }
    
    public Correction getCorrectionByGrade(int gradeId) {
        
        Correction correction = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Correction c join fetch c.grade g where g.id = :gradeId";
            Query query = ses.createQuery(queryString, Correction.class);
            query.setParameter("gradeId", gradeId);
            correction = (Correction) query.uniqueResult();
           
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return correction;
    }
    
    public CorrectionDTO getCorrectionDTOByGradeWithTeacherName(int gradeId) {
        
        CorrectionDTO correctionDTO = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Correction c join fetch c.grade g join fetch g.evaluation ev "
                    + "join fetch ev.course c join fetch g.registeredCourse rc join fetch rc.courseTeacher ct "
                    + "join fetch ct.employee e join fetch e.user u join fetch u.person where g.id = :gradeId";
            Query query = ses.createQuery(queryString, Correction.class);
            query.setParameter("gradeId", gradeId);
            Correction correction = (Correction) query.uniqueResult();
            
            correction.getGrade().setCorrections(null);

            correction.getGrade().getEvaluation().getCourse().setCourse(null);
            correction.getGrade().getEvaluation().getCourse().setCareerCourses(null);
            correction.getGrade().getEvaluation().getCourse().setCourseTeachers(null);
            correction.getGrade().getEvaluation().getCourse().setCourses(null);
            correction.getGrade().getEvaluation().getCourse().setEvaluations(null);
            correction.getGrade().getEvaluation().getCourse().setFaculty(null);
            correction.getGrade().getEvaluation().setGrades(null);

            String employeeName = 
                    correction.getGrade().getRegisteredCourse().getCourseTeacher().getEmployee().getUser().getPerson().getName() + " " +
                    correction.getGrade().getRegisteredCourse().getCourseTeacher().getEmployee().getUser().getPerson().getSurname();

            correction.getGrade().setRegisteredCourse(null);

            correctionDTO = new CorrectionDTO(correction, employeeName);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return correctionDTO;
    }
    
    public CorrectionDTO getCorrectionDTOByGradeWithStudentName(int gradeId) {
        
        CorrectionDTO correctionDTO = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Correction c join fetch c.grade g join fetch g.evaluation ev "
                    + "join fetch ev.course c join fetch g.registeredCourse rc join fetch rc.student s "
                    + "join fetch s.user u join fetch u.person where g.id = :gradeId";
            Query query = ses.createQuery(queryString, Correction.class);
            query.setParameter("gradeId", gradeId);
            Correction correction = (Correction) query.uniqueResult();
            
            correction.getGrade().setCorrections(null);

            correction.getGrade().getEvaluation().getCourse().setCourse(null);
            correction.getGrade().getEvaluation().getCourse().setCareerCourses(null);
            correction.getGrade().getEvaluation().getCourse().setCourseTeachers(null);
            correction.getGrade().getEvaluation().getCourse().setCourses(null);
            correction.getGrade().getEvaluation().getCourse().setEvaluations(null);
            correction.getGrade().getEvaluation().getCourse().setFaculty(null);
            correction.getGrade().getEvaluation().setGrades(null);

            String studentName = 
                    correction.getGrade().getRegisteredCourse().getStudent().getUser().getPerson().getName() + " " +
                    correction.getGrade().getRegisteredCourse().getStudent().getUser().getPerson().getSurname();
            
            correction.getGrade().setRegisteredCourse(null);

            correctionDTO = new CorrectionDTO(correction, studentName);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return correctionDTO;
    }
    
    public Correction getCorrectionNiceWay(int id) throws Exception {
        Correction correction = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            String queryString = "FROM Correction c join fetch c.grade g join fetch g.registeredCourse rc where c.id = :correctionId";
            Query query = ses.createQuery(queryString, Correction.class);
            query.setParameter("correctionId", id);
            correction = (Correction) query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return correction;
    }
    
    public Correction get(int id) throws Exception {
        Correction correction = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            correction = (Correction) ses.get(Correction.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return correction;
    }
}
