package com.example.acadex.service;

import java.util.List;
import com.example.acadex.model.Student;

public interface StudentService {
    List<Student> getAllStudents();
    Student getStudentById(Long id);
    Student saveStudent(Student student);
    Student updateStudent(Long id, Student student);
    void deleteStudent(Long id);
}
