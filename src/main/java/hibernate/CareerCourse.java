package hibernate;
// Generated 10-06-2018 09:26:51 AM by Hibernate Tools 4.3.1


import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import static javax.persistence.GenerationType.IDENTITY;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * CareerCourse generated by hbm2java
 */
@Entity
@Table(name="career_course"
    ,catalog="gradecheck"
)
public class CareerCourse  implements java.io.Serializable {


     private Integer id;
     @JsonManagedReference
     private Career career;
     @JsonManagedReference
     private Course course;
     private int plan;
     private String pensumYear;
     private String pensumSemester;
     private Boolean state;

    public CareerCourse() {
    }

	
    public CareerCourse(Career career, Course course, int plan) {
        this.career = career;
        this.course = course;
        this.plan = plan;
    }
    public CareerCourse(Career career, Course course, int plan, String pensumYear, String pensumSemester, Boolean state) {
       this.career = career;
       this.course = course;
       this.plan = plan;
       this.pensumYear = pensumYear;
       this.pensumSemester = pensumSemester;
       this.state = state;
    }
   
     @Id @GeneratedValue(strategy=IDENTITY)

    
    @Column(name="id", unique=true, nullable=false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="career_id", nullable=false)
    public Career getCareer() {
        return this.career;
    }
    
    public void setCareer(Career career) {
        this.career = career;
    }

@ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="course_id", nullable=false)
    public Course getCourse() {
        return this.course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }

    
    @Column(name="plan", nullable=false)
    public int getPlan() {
        return this.plan;
    }
    
    public void setPlan(int plan) {
        this.plan = plan;
    }

    
    @Column(name="pensum_year", length=2)
    public String getPensumYear() {
        return this.pensumYear;
    }
    
    public void setPensumYear(String pensumYear) {
        this.pensumYear = pensumYear;
    }

    
    @Column(name="pensum_semester", length=2)
    public String getPensumSemester() {
        return this.pensumSemester;
    }
    
    public void setPensumSemester(String pensumSemester) {
        this.pensumSemester = pensumSemester;
    }

    
    @Column(name="state")
    public Boolean getState() {
        return this.state;
    }
    
    public void setState(Boolean state) {
        this.state = state;
    }




}

