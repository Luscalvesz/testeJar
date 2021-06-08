package br.com.bustch.telasApp;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.json.JSONObject;

public class Slack {

    private static final HttpClient client = HttpClient.newHttpClient();
<<<<<<< HEAD
    private static final String URL = "https://hooks.slack.com/services/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";
=======
    private static final String URL = "https://hooks.slack.com/services/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";
>>>>>>> 148cf4f6796a8f3e4286e55bb0beb97192ac311d


    public static void sendMessage(JSONObject content) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder(
                URI.create(URL))
                .header("accept", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(content.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(String.format("Status: %s", response.statusCode()));
        System.out.println(String.format("Response: %s", response.body()));
    }
}
