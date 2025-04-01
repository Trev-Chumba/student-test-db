package com.Compulynx.student_test_db.Student;

import com.Compulynx.student_test_db.Users.AuthService;
import com.Compulynx.student_test_db.Users.UsersWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AuthService authService;

    @CrossOrigin
    @GetMapping(value = "/all")
    public ResponseEntity<?> getStudents() {
        try {
            List<Student> students = studentRepository.findAll();
            return ResponseEntity.ok(students);
        } catch (Exception e) {
            System.err.println("Error fetching students: " + e.getMessage());

            // Return proper error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching students.");
        }

    }

    @CrossOrigin
    @PostMapping(path = "{studentId}")
    public ResponseEntity<?> getStudent(@PathVariable("studentId") Long studentId) {
        if (studentId != null) {
            try {
                Object student = studentRepository.findById(studentId);
                return ResponseEntity.ok(student);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e + "\"}");
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin
    @PostMapping(value = "/delete")
    public ResponseEntity<?> deleteStudent(@RequestBody StudentWrapper studentWrapper) {
        Long studentId = studentWrapper.getStudentId();

        if (studentId != null) {
            try {
                studentRepository.deleteById(studentId);
                return ResponseEntity.ok().body("{\"message\": \"" + "deleted successfully" + "\"}");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("{\"error\": \"" + e + "\"}");
            }
        } else {
            return ResponseEntity.badRequest().build();
        }
    }

    @CrossOrigin
    @PostMapping(value = "/update")
    public ResponseEntity<String> updateStudent(@RequestBody StudentWrapper studentWrapper) {
        Long studentId = studentWrapper.getStudentId();
        if (studentId != null) {
            Optional<Student> optionalStudent = studentRepository.findById(studentId);

            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();

                student.setFirstName(studentWrapper.getFirstName());
                student.setLastName(studentWrapper.getLastName());
                student.setStudentClass(studentWrapper.getStudentClass());
                student.setScore(studentWrapper.getScore());
                student.setStatus(studentWrapper.getStatus());
                student.setPhotoPath(studentWrapper.getPhotoPath());

                studentRepository.save(student);

                return ResponseEntity.ok("Student updated successfully!");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }
        }

        return ResponseEntity.badRequest().body("Student ID is required");
    }
}


