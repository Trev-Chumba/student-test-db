package com.Compulynx.student_test_db.Student;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;

import java.time.LocalDate;
import java.util.Date;

public class StudentWrapper {


    private Long studentId;
    private String firstName;
    private String lastName;
//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate DOB;
    private String studentClass;
    private Long score;
    private Integer status;
    private String photoPath;

    public StudentWrapper(String firstName, String lastName, LocalDate DOB, String studentClass, Long score, Integer status, String photoPath) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.DOB = DOB;
        this.studentClass = studentClass;
        this.score = score;
        this.status = status;
        this.photoPath = photoPath;
    }

    public Long getStudentId() {
        return studentId;
    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getDOB() {
        return DOB;
    }

    public void setDOB(LocalDate DOB) {
        this.DOB = DOB;
    }

    public String getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(String studentClass) {
        this.studentClass = studentClass;
    }

    public Long getScore() {
        return score;
    }

    public void setScore(Long score) {
        this.score = score;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }




}
