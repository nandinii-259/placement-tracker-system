package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.exception.ResourceNotFoundException;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.repository.StudentRepository;
import org.springframework.stereotype.Service;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
    }
    public Student updateStudentProfile(Long id, String fullName, String branch, java.math.BigDecimal cgpa) {
        Student student = getStudentById(id);
        student.setFullName(fullName);
        student.setBranch(branch);
        if (cgpa != null) {
            student.setCgpa(cgpa);
        }
        return studentRepository.save(student);
    }
    public Student getStudentByEmail(String email) {
        return studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for this account."));
    }
}