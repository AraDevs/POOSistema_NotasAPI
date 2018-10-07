package dtoaaaaaa;
// Generated 10-06-2018 08:41:00 AM by Hibernate Tools 4.3.1


import java.util.HashSet;
import java.util.Set;

/**
 * Employee generated by hbm2java
 */
public class EmployeeDTO  implements java.io.Serializable {


     private Integer id;
     private RoleDTO role;
     private UserDTO user;
     private Boolean state;
     private Set<CourseTeacherDTO> courseTeachers = new HashSet<CourseTeacherDTO>(0);

    public EmployeeDTO() {
    }

	
    public EmployeeDTO(RoleDTO role, UserDTO user) {
        this.role = role;
        this.user = user;
    }
    public EmployeeDTO(RoleDTO role, UserDTO user, Boolean state, Set<CourseTeacherDTO> courseTeachers) {
       this.role = role;
       this.user = user;
       this.state = state;
       this.courseTeachers = courseTeachers;
    }
   
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    public RoleDTO getRole() {
        return this.role;
    }
    
    public void setRole(RoleDTO role) {
        this.role = role;
    }
    public UserDTO getUser() {
        return this.user;
    }
    
    public void setUser(UserDTO user) {
        this.user = user;
    }
    public Boolean getState() {
        return this.state;
    }
    
    public void setState(Boolean state) {
        this.state = state;
    }
    public Set<CourseTeacherDTO> getCourseTeachers() {
        return this.courseTeachers;
    }
    
    public void setCourseTeachers(Set<CourseTeacherDTO> courseTeachers) {
        this.courseTeachers = courseTeachers;
    }




}

