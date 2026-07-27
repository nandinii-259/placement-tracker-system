package com.placementtracker.placement_tracker_backend.controller;

import com.placementtracker.placement_tracker_backend.dto.RegisterRequestDto;
import com.placementtracker.placement_tracker_backend.dto.StudentResponseDto;
import com.placementtracker.placement_tracker_backend.entity.Student;
import com.placementtracker.placement_tracker_backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<StudentResponseDto> register(@Valid @RequestBody RegisterRequestDto requestDto) {
        Student student = userService.registerStudent(
                requestDto.getEmail(),
                requestDto.getPassword(),
                requestDto.getFullName(),
                requestDto.getBranch(),
                requestDto.getCgpa(),
                requestDto.getGraduationYear()
        );

        StudentResponseDto responseDto = new StudentResponseDto(
                student.getId(),
                student.getUser().getEmail(),
                student.getFullName(),
                student.getBranch(),
                student.getCgpa(),
                student.getGraduationYear()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}