package com.Compulynx.student_test_db.Student;

import com.Compulynx.student_test_db.Users.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AuthService authService;



    @CrossOrigin
    @GetMapping(value = "/all", produces = "application/json")
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
    @GetMapping(path = "/{studentId}", produces = "application/json")
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
    @PostMapping(value = "/delete", produces = "application/json")
    public ResponseEntity<Object> deleteStudent(@RequestBody StudentWrapper studentWrapper) {
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
    @PostMapping(value = "/update", produces = "application/json")
    public ResponseEntity<?> updateStudent(@RequestBody StudentWrapper studentWrapper) {
        Long studentId = studentWrapper.getStudentId();
        if (studentId != null) {
            Optional<Student> optionalStudent = studentRepository.findById(studentId);

            if (optionalStudent.isPresent()) {
                Student student = optionalStudent.get();
                System.out.println(studentWrapper.getStudentClass());
                student.setFirstName(studentWrapper.getFirstName());
                student.setLastName(studentWrapper.getLastName());
                student.setStudentClass(studentWrapper.getStudentClass());
                student.setScore(studentWrapper.getScore());
                student.setStatus(studentWrapper.getStatus());
                if (studentWrapper.getDOB() != null) {
                  student.setDOB(studentWrapper.getDOB().plusDays(1));
                }
//                student.setPhotoPath(studentWrapper.getPhotoPath());

                studentRepository.save(student);
                System.out.println("Done");
                return ResponseEntity.ok(Map.of("message", "Student updated successfully!"));

            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }
        }

        return ResponseEntity.badRequest().body("Student ID is required");
    }

    @CrossOrigin
    @PostMapping(value = "/create", produces = "application/json")
    public ResponseEntity<Object> createStudents(@RequestBody StudentWrapper[] studentWrapper) {

        if (studentWrapper != null) {
            for (StudentWrapper wrapper: studentWrapper) {
                Student student = new Student();

                student.setFirstName(wrapper.getFirstName());
                student.setLastName(wrapper.getLastName());
                student.setDOB(wrapper.getDOB());
                student.setStudentClass(wrapper.getStudentClass());
                student.setScore(wrapper.getScore() + 10);
                student.setStatus(wrapper.getStatus());
                student.setPhotoPath(wrapper.getPhotoPath());

                studentRepository.save(student);
            }

            // Return response as JSON
            Map<String, String> response = new HashMap<>();
            response.put("message", "created successfully");

            return ResponseEntity.ok(response);

        } else {
            Map<String, String> response = new HashMap<>();
            response.put("message", "No students");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @CrossOrigin
    @PostMapping(value = "/upload",  consumes = "multipart/form-data")
    public ResponseEntity<Object> uploadPhoto(@RequestParam("image")  MultipartFile file,  @RequestParam("studentId") Long studentId) {
        if (file != null) {
            String SAVE_DIR2 = System.getProperty("user.home") + "/Downloads/excel/" + file.getOriginalFilename();
            String DIR = System.getProperty("user.home") + "/var/log/applications/API/StudentPhotos/" + file.getOriginalFilename();
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(DIR);
                fileOutputStream.write(file.getBytes());
                fileOutputStream.close();

                Optional<Student> optionalStudent = studentRepository.findById(studentId);
                if (optionalStudent.isPresent()) {
                    Student student = optionalStudent.get();
                    student.setPhotoPath(DIR);
                    studentRepository.save(student);
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(Map.of("error", "Student not found"));
                }

                Map<String, String> response = new HashMap<>();
                response.put("message", "created successfully");
                System.out.println("saved to: " + DIR);
                return ResponseEntity.ok(response);
            } catch (IOException e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Failed to save file: " + e.getMessage());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "file not found");

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}


