package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.StudentResponseDto;
import com.placementtracker.placement_tracker_backend.dto.StudentUpdateRequestDto;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDto> getStudentById(@PathVariable Long id) {
        Student student = studentService.getStudentById(id);
        return ResponseEntity.ok(toResponseDto(student));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDto> updateStudentProfile(@PathVariable Long id,
                                                                   @Valid @RequestBody StudentUpdateRequestDto requestDto) {
        Student updatedStudent = studentService.updateStudentProfile(
                id, requestDto.getFullName(), requestDto.getBranch(), requestDto.getCgpa());

        return ResponseEntity.ok(toResponseDto(updatedStudent));
    }

    private StudentResponseDto toResponseDto(Student student) {
        return new StudentResponseDto(
                student.getId(),
                student.getUser().getEmail(),
                student.getFullName(),
                student.getBranch(),
                student.getCgpa(),
                student.getGraduationYear()
        );
    }
}