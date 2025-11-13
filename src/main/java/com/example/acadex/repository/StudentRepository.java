package com.example.acadex.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.acadex.model.Student;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
