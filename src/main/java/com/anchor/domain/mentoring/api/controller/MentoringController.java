package com.anchor.domain.mentoring.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/mentorings")
@RestController
public class MentoringController {

  @PostMapping
  public ResponseEntity<Object> createMentoring() {
    return null;
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> editMentoring() {
    return null;
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteMentoring() {
    return null;
  }

}
