package com.Compulynx.student_test_db.FileHandling;

import com.Compulynx.student_test_db.Student.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/excel")
public class ExcelController {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ExcelService excelService;

    private static final String SAVE_DIR = "C:/var/log/docs";
    private static final String SAVE_DIR2 = System.getProperty("user.home") + "/Downloads/excel/";
    private static final String DIR = System.getProperty("user.home") + "/var/log/applications/API/dataprocessing/";
    @CrossOrigin
    @GetMapping(value = "/generate/{userRequest}/{fileName}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sseExcel (@PathVariable int userRequest, @PathVariable String fileName)
    {
        System.out.println("Endpoint called with: " + userRequest + ", " + fileName);

        SseEmitter emitter = new SseEmitter(300000L);

        emitter.onCompletion(() -> {
            System.out.println("SSE emitter completed");
        });

        // Add timeout callback
        emitter.onTimeout(() -> {
            System.out.println("SSE emitter timed out");
        });

        // Add error callback
        emitter.onError((ex) -> {
            System.out.println("SSE emitter error: " + ex.getMessage());
        });
        Executor executor = Executors.newSingleThreadExecutor();

        CompletableFuture.runAsync(() -> {
            try {
                String filePath = excelService.generateExcel(userRequest, emitter, fileName);
                emitter.send(SseEmitter.event().name("complete").data(filePath));
                emitter.complete();  // ✅ Only complete if success
            } catch (Exception e) {
                System.out.println("❌ Exception in generateExcel(): " + e.getMessage());
                try {
                    emitter.send(SseEmitter.event().name("error").data(e.getMessage()));
                } catch (IOException ignored) {}
                emitter.completeWithError(e);
                System.out.println("Failed ");
            }
        }, executor);
        return emitter;
    }
    @CrossOrigin
    @GetMapping("/download/{fileName}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(null); // Return 400 Bad Request
        }

        File file = new File(DIR, fileName);

        if (!file.exists()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        try {
            byte[] fileBytes = Files.readAllBytes(file.toPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", file.getName());

            return new ResponseEntity<>(fileBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @CrossOrigin
    @GetMapping("/list")
    public ResponseEntity<java.util.List<String>> listExcelFiles() {
        File directory = new File(DIR);

        if (!directory.exists() || !directory.isDirectory()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.List.of()); // Return empty list
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return ResponseEntity.ok(java.util.List.of()); // Return empty list if unable to list files
        }

        List<String> excelFiles = Arrays.stream(files)
                .filter(file -> file.isFile() && file.getName().endsWith(".xlsx"))
                .map(File::getName)
                .collect(Collectors.toList());

        return ResponseEntity.ok(excelFiles);
    }

    @Async
    public CompletableFuture<String> generateExcelAsync(int userRequest, SseEmitter emitter, String fileName) {
        return CompletableFuture.supplyAsync(() -> excelService.generateExcel(userRequest, emitter, fileName));
    }


}
