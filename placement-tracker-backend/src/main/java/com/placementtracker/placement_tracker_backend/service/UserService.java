package com.placementtracker.placement_tracker_backend.service;

import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.entity.User;
import com.placementtracker.placement_tracker_backend.exception.BusinessRuleException;
import com.placementtracker.placement_tracker_backend.exception.ResourceNotFoundException;
import com.placementtracker.placement_tracker_backend.repository.StudentRepository;
import com.placementtracker.placement_tracker_backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, StudentRepository studentRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public Student registerStudent(String email, String rawPassword, String fullName,
                                   String branch, BigDecimal cgpa, Integer graduationYear) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessRuleException("An account with this email already exists.");
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(User.Role.STUDENT);
        User savedUser = userRepository.save(user);

        Student student = new Student();
        student.setUser(savedUser);
        student.setFullName(fullName);
        student.setBranch(branch);
        student.setCgpa(cgpa);
        student.setGraduationYear(graduationYear);

        return studentRepository.save(student);
    }
}