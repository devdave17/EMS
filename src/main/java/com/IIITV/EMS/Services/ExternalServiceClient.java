package com.IIITV.EMS.Services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ExternalServiceClient {

    private final HttpClient httpClient;
    private final String baseUrl;

    public ExternalServiceClient(
            @Value("${ems.external-service.url}") String baseUrl) {

        this.baseUrl = baseUrl.replaceAll("/+$", "");

        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(2))
                .build();
    }

    public String getServiceStatus() {
        return executeWithRetry(baseUrl + "/health");
    }

    private String executeWithRetry(String url) {
        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .timeout(Duration.ofSeconds(3))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

                int status = response.statusCode();

                if (status >= 200 && status < 300) {
                    return response.body();
                }

                // 4xx errors must not be retried.
                if (status >= 400 && status < 500) {
                    throw new IllegalStateException(
                            "External service rejected request: HTTP " + status
                    );
                }

                // Retry only 5xx responses.
                if (status < 500 || attempt == maxAttempts) {
                    throw new IllegalStateException(
                            "External service failed: HTTP " + status
                    );
                }

            } catch (IOException ex) {
                if (attempt == maxAttempts) {
                    throw new IllegalStateException(
                            "External service unavailable after retries", ex
                    );
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException(
                        "External service request interrupted", ex
                );
            }

            // Exponential backoff + jitter: approximately 200ms, 400ms...
            long exponentialDelay = 200L * (1L << (attempt - 1));
            long jitter = ThreadLocalRandom.current()
                    .nextLong(0, 101);

            try {
                Thread.sleep(exponentialDelay + jitter);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                throw new IllegalStateException("Retry interrupted", ex);
            }
        }

        throw new IllegalStateException("External service request failed");
    }
}