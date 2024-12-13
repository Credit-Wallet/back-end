package vn.edu.iuh.controller;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.iuh.response.ApiResponse;
import vn.edu.iuh.response.UploadResponse;
import vn.edu.iuh.service.FileStorageService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/uploads")
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/files/{fileName}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        try {
            Path filePath = fileStorageService.loadFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {

                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping()
    public ApiResponse<?> uploadFile(
            @RequestParam(name = "file", required = false) MultipartFile file
    ) throws IOException {
        if (file.getSize() > 1024 * 1024 * 10) {
            return ApiResponse.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .message("File size must be less than 10MB")
                    .build();
        }
        // nén ảnh cho kích thước nhỏ hơn
        InputStream originalImage = file.getInputStream(); // Đọc file gốc
        ByteArrayOutputStream compressedImageOutputStream = new ByteArrayOutputStream();
        Thumbnails.of(originalImage)
                .scale(1.0) // Không thay đổi kích thước (giữ nguyên)
                .outputQuality(0.8) // Giảm chất lượng ảnh xuống 80%
                .toOutputStream(compressedImageOutputStream);

        // Tạo MultipartFile mới từ ảnh đã nén
        byte[] compressedImageBytes = compressedImageOutputStream.toByteArray();
        MultipartFile compressedFile = new MockMultipartFile(
                file.getName(), // Tên trường
                file.getOriginalFilename(), // Tên file gốc
                file.getContentType(), // Loại file
                compressedImageBytes // Nội dung file đã nén
        );

        String fileName = fileStorageService.storeFile(compressedFile);

        UploadResponse uploadResponse = new UploadResponse(fileName);

        return ApiResponse.builder()
                .result(uploadResponse)
                .build();
    }
}