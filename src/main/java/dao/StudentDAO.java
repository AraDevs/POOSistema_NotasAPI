/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

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
public class StudentDAO {
    
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
    
    public Student getStudentByUser(int id, int...fetchOptions) {  
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
}
