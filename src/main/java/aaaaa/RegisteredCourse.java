/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaaaa;

import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement( name = "registeredCourse" )
public class RegisteredCourse {
    private int id;
    private Student student;
    private CourseTeacher courseTeacher;
    private String courseState;
    private int courseYear;
    private String semester;
    private Boolean state;
    private List<Grade> gradeList;

    public RegisteredCourse() {
    }

    public RegisteredCourse(int id, Student student, CourseTeacher courseTeacher, String courseState, int courseYear, String semester, Boolean state, List<Grade> gradeList) {
        this.id = id;
        this.student = student;
        this.courseTeacher = courseTeacher;
        this.courseState = courseState;
        this.courseYear = courseYear;
        this.semester = semester;
        this.state = state;
        this.gradeList = gradeList;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @XmlElement
    public CourseTeacher getCourseTeacher() {
        return courseTeacher;
    }

    public void setCourseTeacher(CourseTeacher courseTeacher) {
        this.courseTeacher = courseTeacher;
    }

    @XmlElement
    public String getCourseState() {
        return courseState;
    }

    public void setCourseState(String courseState) {
        this.courseState = courseState;
    }

    @XmlElement
    public int getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(int courseYear) {
        this.courseYear = courseYear;
    }

    @XmlElement
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @XmlElement
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    @XmlElement
    public List<Grade> getGradeList() {
        return gradeList;
    }

    public void setGradeList(List<Grade> gradeList) {
        this.gradeList = gradeList;
    }
    
    
}
