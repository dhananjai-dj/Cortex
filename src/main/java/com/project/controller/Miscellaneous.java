package com.project.controller;

import com.project.dto.InjectRequest;
import com.project.dto.KbResult;
import com.project.dto.SearchRequest;
import com.project.service.DeleteService;
import com.project.service.InjectorService;
import com.project.service.SearchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class Miscellaneous {

    private final InjectorService injectorService;
    private final SearchService searchService;
    private final DeleteService deleteService;

    public Miscellaneous(InjectorService injectorService, SearchService searchService, DeleteService deleteService) {
        this.searchService = searchService;
        this.deleteService = deleteService;
        this.injectorService = injectorService;

    }

    @PostMapping("/inject")
    public String inject(@RequestBody InjectRequest request) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("author", "Dhananjai");
        metadata.put("microservice", "cortex");

        injectorService.injectData(request.summary(), metadata);
        return "Submitted for ingestion";
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteDocuments(@RequestBody DeleteRequest deleteRequest) {
        deleteService.deleteData(deleteRequest.ids());
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @PostMapping("/search")
    public List<KbResult> search(@RequestBody SearchRequest request) {
        return searchService.searchData(request);
    }


    public record DeleteRequest(List<String> ids) {
    }
}