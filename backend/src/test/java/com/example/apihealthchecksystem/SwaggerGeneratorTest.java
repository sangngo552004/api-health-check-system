package com.example.apihealthchecksystem;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class SwaggerGeneratorTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void generateSwagger() throws Exception {
    String swaggerJson =
        restTemplate.getForObject("http://localhost:" + port + "/v3/api-docs", String.class);
    assertNotNull(swaggerJson);
    assertTrue(swaggerJson.contains("openapi"));

    // Chuẩn hóa port ngẫu nhiên thành 8080 để dễ so sánh
    String normalizedSwagger =
        swaggerJson.replaceAll("\"http://localhost:\\d+\"", "\"http://localhost:8080\"");

    Path docsPath = Paths.get("..", "docs", "api", "openapi.json");
    boolean shouldGenerate = Boolean.getBoolean("generateSwagger");

    com.fasterxml.jackson.databind.ObjectMapper mapper =
        new com.fasterxml.jackson.databind.ObjectMapper();
    com.fasterxml.jackson.databind.JsonNode generatedNode = mapper.readTree(normalizedSwagger);

    if (shouldGenerate) {
      if (!Files.exists(docsPath.getParent())) {
        Files.createDirectories(docsPath.getParent());
      }
      // Ghi file với format đẹp (pretty print)
      Files.writeString(
          docsPath, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(generatedNode));
      System.out.println("Swagger documentation generated at: " + docsPath.toAbsolutePath());
    } else {
      if (!Files.exists(docsPath)) {
        throw new AssertionError(
            "Swagger file does not exist at "
                + docsPath.toAbsolutePath()
                + ". Run with -DgenerateSwagger=true to generate it.");
      }
      String existingContent = Files.readString(docsPath);
      String normalizedExisting =
          existingContent.replaceAll("\"http://localhost:\\d+\"", "\"http://localhost:8080\"");
      com.fasterxml.jackson.databind.JsonNode existingNode = mapper.readTree(normalizedExisting);

      // So sánh cấu trúc JSON (bỏ qua khác biệt về khoảng trắng, xuống dòng)
      if (!generatedNode.equals(existingNode)) {
        throw new AssertionError(
            "Swagger documentation is out of date. "
                + "Please run 'mvn test -Dtest=SwaggerGeneratorTest -DgenerateSwagger=true' "
                + "to update it, then commit the changes.");
      }
    }
  }
}
