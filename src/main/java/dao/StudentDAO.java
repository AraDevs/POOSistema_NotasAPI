/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import hibernate.CareerStudent;
import hibernate.HibernateUtil;
import hibernate.Person;
import hibernate.Student;
import hibernate.User;
import java.util.ArrayList;
import java.util.HashSet;
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
@XmlRootElement ( name = "studentDao") 
@XmlSeeAlso( { Student.class, User.class, Person.class})
public class StudentDAO extends DAO {
    
    private List<Student> students;
    String param;
    
    //Constantes que ayudan a decidir qué relaciones serán fetcheadas, y de acuerdo a que id se realizará la
    //búsqueda
    public static final int STUDENT_ONLY = 0;
    public static final int USER = 1;
    public static final int PERSON = 2; //Abarca a User
    
    public StudentDAO() {
        students = new CopyOnWriteArrayList<Student>(); 
        param = null;
    }
    
    public StudentDAO(String name) {   
        students = new CopyOnWriteArrayList<Student>();   
        param = name;  
    }
    
    public StudentDAO(boolean charge) {   
        if(charge == false) {    
            //don't load data   
        }
        else {    
            students = new CopyOnWriteArrayList<Student>();
            param = null;  
        }
    }
    
    @XmlElement  
    public List getStudent() {   
        try {    
            //students = getStudentList(param);   
        } 
        catch (Exception e) {        
            e.printStackTrace();   
        }      
        return this.students;  
    }
    
    public void setStudent(List<Student> students) {   
        this.students = students;     
    }
    
    public List<Student> getStudentList (String param, boolean active) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            String activeQuery = "";
            if (active) {
                activeQuery = " where s.state = true and u.state = true and p.state = true";
            }
            
            tra = ses.beginTransaction();
            String queryString = "FROM Student s join fetch s.user u join fetch u.person p" + activeQuery;
            Query query = ses.createQuery(queryString, Student.class);
            students = query.list();
            
            for (Student s : students) {
                s.setCareerStudents(null);
                s.setRegisteredCourses(null);
                s.getUser().setEmployees(null);
                s.getUser().setStudents(null);
                s.getUser().getPerson().setUsers(null);
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
        
        return students;
    }
    
    public List<Student> getStudentsByCourseTeacher (int courseTeacherId) throws Exception {
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Student s join fetch s.user u join fetch u.person p "
                    + "join fetch s.registeredCourses rc join fetch rc.courseTeacher ct "
                    + "where rc.courseState = 'En curso' and ct.id = :courseTeacherId";
            Query query = ses.createQuery(queryString, Student.class);
            query.setParameter("courseTeacherId", courseTeacherId);
            students = query.list();
            
            for (Student s : students) {
                s.setCareerStudents(null);
                s.setRegisteredCourses(null);
                s.getUser().setEmployees(null);
                s.getUser().setStudents(null);
                s.getUser().getPerson().setUsers(null);
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
        
        return students;
    }
    
    public Student getStudent(int id) throws Exception {
        Student student = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Student s join fetch s.user u join fetch u.person where s.id = :id";
            Query query = ses.createQuery(queryString, Student.class);
            query.setParameter("id", id);
            student = (Student) query.uniqueResult();
            
            student.setCareerStudents(null);
            student.setRegisteredCourses(null);
            student.getUser().setEmployees(null);
            student.getUser().setStudents(null);
            student.getUser().getPerson().setUsers(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return student;
    }
    
    public Student getStudentWithCareerStudent(int id) throws Exception {
        Student student = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Student s left join fetch s.careerStudents where s.id = :id";
            Query query = ses.createQuery(queryString, Student.class);
            query.setParameter("id", id);
            student = (Student) query.uniqueResult();
            
            student.setRegisteredCourses(null);
            student.setUser(null);
            
            for (Object cs : student.getCareerStudents()) {
                ((CareerStudent) cs).setCareer(null);
                ((CareerStudent) cs).setStudent(null);
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
        
        return student;
    }
    
    public Student getStudentWithCareers(int id) throws Exception {
        Student student = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "FROM Student s left join fetch s.careerStudents cs where s.id = :id";
            Query query = ses.createQuery(queryString, Student.class);
            query.setParameter("id", id);
            student = (Student) query.uniqueResult();
            
            student.setRegisteredCourses(null);
            student.setUser(null);
            
            for (Object cs : student.getCareerStudents()) {
                Hibernate.initialize(((CareerStudent) cs).getCareer());
                ((CareerStudent) cs).getCareer().setCareerCourses(null);
                ((CareerStudent) cs).getCareer().setCareerStudents(null);
                ((CareerStudent) cs).getCareer().setCareerType(null);
                ((CareerStudent) cs).getCareer().setFaculty(null);
                ((CareerStudent) cs).setStudent(null);
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
        
        return student;
    }
    
    public Student getStudentByUser(int id, int...fetchOptions) throws Exception {  
        Student student = null;
        
        //Convirtiendo fetchOptions en una lista
        ArrayList<Integer> fetchOptionsList = new ArrayList<Integer>();
        for (int i: fetchOptions) fetchOptionsList.add(i);
        
        
        String fetchQuery = "";
        
        //Evaluando los fetch necesarios
        if (fetchOptionsList.contains(PERSON)) {
            fetchQuery += " inner join fetch u.person";
        }
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            tra = ses.beginTransaction();
            String queryString = "from Student s inner join fetch s.user u" + fetchQuery + " where u.id = :id";
            Query query = ses.createQuery(queryString);
            query.setParameter("id", id);
            student = (Student) query.uniqueResult();
            
            //Nullificando relaciones innecesarias
            if (!(fetchOptionsList.contains(USER) || fetchOptionsList.contains(PERSON))) {
                student.setUser(new User());
            }
            else {
                student.getUser().setEmployees(null);
                student.getUser().setStudents(null);
                student.getUser().setPass(null);
                if (!fetchOptionsList.contains(PERSON)) {
                    student.getUser().setPerson(null);
                }
                else {
                    student.getUser().getPerson().setUsers(null);
                }
            }
            
            student.setCareerStudents(null);
            student.setRegisteredCourses(null);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            //ses.flush();
            ses.close();
        }
        
        return student;
    }
    
    public Student get(int id) throws Exception {
        Student student = null;
        
        SessionFactory sesFact = HibernateUtil.getSessionFactory();
        Session ses = sesFact.openSession();
        Transaction tra = null;
        
        try {
            
            tra = ses.beginTransaction();
            student = (Student) ses.get(Student.class, id);
            
        } catch (Exception e) {
            e.printStackTrace();
            if (tra != null) {
                tra.rollback();
            }
        } finally {
            ses.flush();
            ses.close();
        }
        
        return student;
    }
}
