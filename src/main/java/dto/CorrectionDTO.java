/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import hibernate.Correction;
import hibernate.Grade;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement (name = "correction")
public class CorrectionDTO implements java.io.Serializable{
    private Integer id;
    private Grade grade;
    private String description;
    private String filePath;
    private String correctionState;
    private Boolean state;
    //El nombre del maestro al que es dirigida la corrección o del estudiante que la solicita,
    //dependiendo del caso
    private String owner;
    //Parámetro opcional usado únicamente en el método getCorrectionDTOByGradeWithStudentName
    private int registeredCourseId;

    public CorrectionDTO() {
    }

    public CorrectionDTO(Integer id, Grade grade, String description, String filePath, String correctionState, Boolean state, String owner) {
        this.id = id;
        this.grade = grade;
        this.description = description;
        this.filePath = filePath;
        this.correctionState = correctionState;
        this.state = state;
        this.owner = owner;
    }
    
    public CorrectionDTO(Correction correction, String owner) {
        this.id = correction.getId();
        this.grade = correction.getGrade();
        this.description = correction.getDescription();
        this.filePath = correction.getFilePath();
        this.correctionState = correction.getCorrectionState();
        this.state = correction.getState();
        this.owner = owner;
        
        //Nullificando registeredCourse, ya que este DTO fue hecho para evitar
        //enviar esa información en rutas específicas
        this.grade.setRegisteredCourse(null);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getCorrectionState() {
        return correctionState;
    }

    public void setCorrectionState(String correctionState) {
        this.correctionState = correctionState;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getRegisteredCourseId() {
        return registeredCourseId;
    }

    public void setRegisteredCourseId(int registeredCourseId) {
        this.registeredCourseId = registeredCourseId;
    }
    
    
    
}
