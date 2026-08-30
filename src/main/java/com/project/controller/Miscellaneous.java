package com.project.controller;

import com.project.service.DeleteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/misc")
public class Miscellaneous {

    private final DeleteService deleteService;

    public Miscellaneous(DeleteService deleteService) {
        this.deleteService = deleteService;

    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteDocuments(@RequestBody DeleteRequest deleteRequest) {
        deleteService.deleteData(deleteRequest.ids());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    public record DeleteRequest(List<String> ids) {
    }
}