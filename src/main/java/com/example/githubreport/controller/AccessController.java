package com.example.githubreport.controller;

import com.example.githubreport.model.UserRepoAccess;
import com.example.githubreport.service.GitHubService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccessController {

    @Autowired
    private GitHubService gitHubService;


    @GetMapping("/access-report")
    public List<UserRepoAccess> viewReport() throws Exception {
        return gitHubService.generateAccessReport();
    }

    @GetMapping("/access-report/download")
    public ResponseEntity<String> downloadReport() throws Exception {

        List<UserRepoAccess> report = gitHubService.generateAccessReport();

        StringBuilder csv = new StringBuilder();

        csv.append("User,Repositories\n");

        for (UserRepoAccess user : report) {

            csv.append(user.getUsername())
                    .append(",")
                    .append(String.join("|", user.getRepositories()))
                    .append("\n");
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=github_access_report.csv")
                .body(csv.toString());
    }
}