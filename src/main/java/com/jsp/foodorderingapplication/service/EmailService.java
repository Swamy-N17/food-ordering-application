package com.jsp.foodorderingapplication.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${BREVO_API_KEY}")
    private String brevoApiKey;

    @Value("${FOODHUB_MAIL_USERNAME}")
    private String senderEmail;

    public void sendPasswordResetEmail(String email, String resetLink) {

        String subject = "FoodHub - Password Reset";

        String textContent =
                "Hello,\n\n"
                + "We received a request to reset your FoodHub password.\n\n"
                + "Click the link below to reset your password:\n\n"
                + resetLink
                + "\n\n"
                + "This link is valid for 15 minutes.\n\n"
                + "If you did not request a password reset, "
                + "you can safely ignore this email.\n\n"
                + "Regards,\n"
                + "FoodHub Team";

        String jsonBody = """
                {
                  "sender": {
                    "name": "FoodHub",
                    "email": "%s"
                  },
                  "to": [
                    {
                      "email": "%s"
                    }
                  ],
                  "subject": "%s",
                  "textContent": "%s"
                }
                """.formatted(
                        escapeJson(senderEmail),
                        escapeJson(email),
                        escapeJson(subject),
                        escapeJson(textContent)
                );

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.brevo.com/v3/smtp/email"))
                    .header("accept", "application/json")
                    .header("api-key", brevoApiKey)
                    .header("content-type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException(
                        "Brevo email sending failed. HTTP status: "
                                + response.statusCode()
                                + ", response: "
                                + response.body()
                );
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to send password reset email", e);
        }
    }

    private String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }
}