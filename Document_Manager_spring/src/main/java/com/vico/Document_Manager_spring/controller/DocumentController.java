package com.vico.Document_Manager_spring.controller;
import com.itextpdf.text.DocumentException;
import com.vico.Document_Manager_spring.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


@RestController
@RequestMapping("/document")
public class DocumentController {

    @Autowired
    private DocumentService service;

    //static variables
    public static final String DIRECTORY = "src/documents/";
    public static final String OUTPUT = "src/doc_protected/";



    //upload files method
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files")List<MultipartFile> multipartFiles) throws IOException, DocumentException {
        List<String> filenames = new ArrayList<>();
        for (MultipartFile file : multipartFiles){
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileStorage = get(DIRECTORY, filename).toAbsolutePath().normalize();
            copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);

            File f = new File(String.valueOf(fileStorage));
            if(!f.exists() && !f.isDirectory()) {
                try {

                    service.encryptPdf(f, OUTPUT);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            filenames.add(filename);

        }
        return ResponseEntity.ok().body(filenames);
    }



    //download files method
    @GetMapping("download/{filename}")
    public ResponseEntity<Resource> downloadFiles(@PathVariable("filename") String filename) throws IOException{
        Path filePath = get(DIRECTORY).toAbsolutePath().normalize().resolve(filename);
        if(!Files.exists(filePath)){
            throw new FileNotFoundException(filename + " was not found!");
        }
        Resource resource = new UrlResource(filePath.toUri());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("File-name", filename);
        httpHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;File-name=" + resource.getFilename());
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(Files.probeContentType(filePath)))
                .headers(httpHeaders).body(resource);
    }



}
