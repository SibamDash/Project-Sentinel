package com.sentinel.backend.controller;

import com.sentinel.backend.entity.TestResult;
import com.sentinel.backend.repository.TestResultRepository;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/results")
@CrossOrigin(origins = "http://localhost:3000") 
public class TestResultController {

    private final TestResultRepository repository;

    public TestResultController(TestResultRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public TestResult saveTestResult(@RequestBody TestResult result) {
        result.setTimestamp(LocalDateTime.now()); // Auto-set time
        return repository.save(result);
    }

    @GetMapping
    public List<TestResult> getAllResults() {
        return repository.findAll();
    }
}