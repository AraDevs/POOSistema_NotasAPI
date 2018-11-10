/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import hibernate.Employee;
import hibernate.Role;
import hibernate.User;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author kevin
 */
@XmlRootElement( name = "employee")
public class EmployeeTokenDTO {
    private Integer id;
    private Role role;
    private User user;
    private String token;

    public EmployeeTokenDTO() {
    }

    public EmployeeTokenDTO(Integer id, Role role, User user, String token) {
        this.id = id;
        this.role = role;
        this.user = user;
        this.token = token;
    }
    
    public EmployeeTokenDTO(Employee employee, String token) {
        this.id = employee.getId();
        this.role = employee.getRole();
        this.user = employee.getUser();
        this.token = token;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
