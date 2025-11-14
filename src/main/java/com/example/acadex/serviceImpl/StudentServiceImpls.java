package com.example.acadex.serviceImpl;

import org.springframework.stereotype.Service;
import java.util.List;
import com.example.acadex.model.Student;
import com.example.acadex.repository.StudentRepository;
import com.example.acadex.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class StudentServiceImpls implements StudentService {

    @Autowired
    private StudentRepository repo;

    @Override
    public List<Student> getAllStudents() {
        return repo.findAll();
    }

    @Override
    public Student getStudentById(Long id) {
        return repo.findById(id).orElse(null);
    }

    @Override
    public Student saveStudent(Student student) {
        return repo.save(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        return null;
    }

    @Override
    public void deleteStudent(Long id) {
        repo.deleteById(id);
    }
}
