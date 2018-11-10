/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import hibernate.Student;
import hibernate.User;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement(name = "student")
public class StudentTokenDTO {
    private Integer id;
    private User user;
    private String token;

    public StudentTokenDTO() {
    }

    public StudentTokenDTO(Integer id, User user, String token) {
        this.id = id;
        this.user = user;
        this.token = token;
    }
    
    public StudentTokenDTO(Student student, String token) {
        this.id = student.getId();
        this.user = student.getUser();
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
}
