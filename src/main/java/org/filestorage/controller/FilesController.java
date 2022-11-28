package org.filestorage.controller;


import org.filestorage.service.IFileStoreService;
import org.filestorage.specifications.FileSystemProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/api/v1/file")
public class FilesController {

    private IFileStoreService fileStoreService;
    private FileSystemProvider fileSystemProvider;

    @Autowired
    public FilesController(IFileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }

    @PostMapping("/storefile")
    public ResponseEntity<String> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("subtype") int subType,
            @RequestParam ("userId") Long userId
    ) throws IOException, NoSuchAlgorithmException, InterruptedException {

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        String fileHash = fileStoreService.storeFile(file.getBytes(), file.getOriginalFilename(), subType, userId);
        return ResponseEntity.ok(fileHash);
    }

    @GetMapping("/getfile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("hash") UUID hash) throws IOException {
        byte[] array = fileStoreService.getFile(hash);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new ByteArrayResource(array));
    }

    @GetMapping("/getfiles")
    public ResponseEntity<?> downloadFile(@RequestParam("subtype") int subtype) throws IOException {
        return ResponseEntity.ok(fileStoreService.getMetaFiles(subtype));
    }

    @DeleteMapping("/delete")
    public void deleteFile(@RequestParam("hash") UUID hash) throws IOException {

        if (fileStoreService.countUsers(hash) == 1) {

            fileStoreService.deleteFile(hash);

        }
    }
}