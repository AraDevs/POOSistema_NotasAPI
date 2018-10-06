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
@XmlRootElement( name = "grade" )
public class Grade {
    private int id;
    private double grade;
    private String observations;
    private RegisteredCourse registeredCourse;
    private Evaluation evaluation;
    private Boolean state;

    public Grade() {
    }

    public Grade(int id, double grade, String observations, RegisteredCourse registeredCourse, Evaluation evaluation, Boolean state) {
        this.id = id;
        this.grade = grade;
        this.observations = observations;
        this.registeredCourse = registeredCourse;
        this.evaluation = evaluation;
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
    public double getGrade() {
        return grade;
    }

    public void setGrade(double grade) {
        this.grade = grade;
    }

    @XmlElement
    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    @XmlElement
    public RegisteredCourse getRegisteredCourse() {
        return registeredCourse;
    }

    public void setRegisteredCourse(RegisteredCourse registeredCourse) {
        this.registeredCourse = registeredCourse;
    }

    @XmlElement
    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    @XmlElement
    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }
    
    
}
