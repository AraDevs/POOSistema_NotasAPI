/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aaaaa;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement( name = "course" )
public class Course {
    private int id;
    private String name;
    private String courseCode;
    private String semester;
    private Boolean inter;
    private Boolean laboratory;
    private int uv;
    private Course prerequisite;
    private Boolean state;

    public Course() {
    }

    public Course(int id, String name, String courseCode, String semester, Boolean inter, Boolean laboratory, int uv, Course prerequisite, Boolean state) {
        this.id = id;
        this.name = name;
        this.courseCode = courseCode;
        this.semester = semester;
        this.inter = inter;
        this.laboratory = laboratory;
        this.uv = uv;
        this.prerequisite = prerequisite;
        this.state = state;
    }

    @XmlElement
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlElement
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement
    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    @XmlElement
    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    @XmlElement
    public Boolean getInter() {
        return inter;
    }

    public void setInter(Boolean inter) {
        this.inter = inter;
    }

    @XmlElement
    public Boolean getLaboratory() {
        return laboratory;
    }

    public void setLaboratory(Boolean laboratory) {
        this.laboratory = laboratory;
    }

    @XmlElement
    public int getUv() {
        return uv;
    }

    public void setUv(int uv) {
        this.uv = uv;
    }

    @XmlElement
    public Course getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(Course prerequisite) {
        this.prerequisite = prerequisite;
    }

    @XmlElement
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
    
    
}
