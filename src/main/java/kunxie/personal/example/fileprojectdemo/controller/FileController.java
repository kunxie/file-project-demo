package kunxie.personal.example.fileprojectdemo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/files")
public class FileController {

  // Directory where files will be uploaded
  private static final Path UPLOAD_DIR = Path.of("uploads");

  public FileController() {
    try {
      if (Files.notExists(UPLOAD_DIR)) {
        Files.createDirectories(UPLOAD_DIR); // Create the directory if it doesn't exist
      }
    } catch (Exception e) {
      throw new RuntimeException("Failed to create upload directory: " + UPLOAD_DIR, e);
    }
  }

  @PostMapping("/upload")
  public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
    var filename = file.getOriginalFilename();

    // Basic validation for uploaded file
    if (file.isEmpty()) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file uploaded");
    }
    if (filename == null || !filename.endsWith(".zip")) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Only zip files are allowed");
    }

    try {
      // Resolve the file path and save the file
      var destination = UPLOAD_DIR.resolve(filename);
      Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

      return ResponseEntity.ok("File uploaded successfully: " + destination.toAbsolutePath());
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("Failed to upload file: " + e.getMessage());
    }
  }
}
