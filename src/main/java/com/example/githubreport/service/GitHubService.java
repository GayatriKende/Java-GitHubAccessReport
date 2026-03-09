package com.example.githubreport.service;

import com.example.githubreport.cache.CacheStore;
import com.example.githubreport.model.UserRepoAccess;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

@Service
public class GitHubService {

    @Value("${github.token}")
    private String token;

    @Value("${github.org}")
    private String org;

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public List<UserRepoAccess> generateAccessReport() throws Exception {

        if (CacheStore.contains("report")) {
            return (List<UserRepoAccess>) CacheStore.get("report");
        }

        Map<String, List<String>> userRepoMap = new ConcurrentHashMap<>();

        List<String> repos = getRepositories();

        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (String repo : repos) {

            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {

                try {

                    List<String> users = getCollaborators(repo);

                    for (String user : users) {

                        userRepoMap
                                .computeIfAbsent(user, k -> new ArrayList<>())
                                .add(repo);
                    }

                } catch (Exception e) {
                    System.out.println("Error processing repo: " + repo);
                    e.printStackTrace();
                }

            }, executor);

            futures.add(future);
        }

        CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        ).join();

        List<UserRepoAccess> report = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : userRepoMap.entrySet()) {

            report.add(new UserRepoAccess(
                    entry.getKey(),
                    entry.getValue()
            ));
        }

        CacheStore.put("report", report);

        return report;
    }

    private List<String> getRepositories() throws Exception {

        String url = "https://api.github.com/users/" + org + "/repos";

        String response = callGitHub(url);

        return extractValues(response, "name");
    }

    private List<String> getCollaborators(String repo) throws Exception {

        String url = "https://api.github.com/repos/" + org + "/" + repo + "/collaborators";

        String response = callGitHub(url);

        return extractValues(response, "login");
    }

    private String callGitHub(String apiUrl) throws Exception {

        URL url = new URL(apiUrl);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Accept", "application/vnd.github+json");

        int responseCode = conn.getResponseCode();

        if (responseCode != 200) {
            throw new RuntimeException("GitHub API failed: " + responseCode);
        }

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream())
        );

        String line;
        StringBuilder response = new StringBuilder();

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        conn.disconnect();

        return response.toString();
    }

    private List<String> extractValues(String json, String key) {

        List<String> list = new ArrayList<>();

        String[] parts = json.split("\"" + key + "\":\"");

        for (int i = 1; i < parts.length; i++) {

            String value = parts[i].split("\"")[0];

            list.add(value);
        }

        return list;
    }
}