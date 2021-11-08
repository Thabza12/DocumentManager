package com.vico.Document_Manager_spring.controller;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.text.DocumentException;
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

    //static variables
    public static final String DIRECTORY = "src/documents/";
    public static final String OUTPUT = System.getProperty("user.dir");
    public static final String USER_PASSWORD = "password";
    public static final String OWNER_PASSWORD = "password1";



    //upload files method
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<List<String>> uploadFiles(@RequestParam("files")List<MultipartFile> multipartFiles) throws IOException, DocumentException {
        List<String> filenames = new ArrayList<>();
        for (MultipartFile file : multipartFiles){
            String filename = StringUtils.cleanPath(file.getOriginalFilename());
            Path fileStorage = get(DIRECTORY, filename).toAbsolutePath().normalize();
            copy(file.getInputStream(), fileStorage, REPLACE_EXISTING);
            encrypt(fileStorage, filename);
            filenames.add(filename);

        }
        return ResponseEntity.ok().body(filenames);
    }

    public static void encrypt(Path src, String filename){
            try {
                FileInputStream fis = new FileInputStream(String.valueOf(src));
                PdfReader pdfReader = new PdfReader(fis);
                pdfReader.setUnethicalReading(true);
                WriterProperties writerProperties = new WriterProperties();
                writerProperties.setStandardEncryption(OWNER_PASSWORD.getBytes(),
                        USER_PASSWORD.getBytes(), EncryptionConstants.ALLOW_PRINTING,
                        EncryptionConstants.ENCRYPTION_AES_128);
                PdfWriter pdfWriter = new PdfWriter(new FileOutputStream(filename), writerProperties);
                PdfDocument pdfDocument = new PdfDocument(pdfReader, pdfWriter);
                pdfDocument.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    //download files method
    @GetMapping("download/{filename}")
    public ResponseEntity<Resource> downloadFiles(@PathVariable("filename") String filename) throws IOException{
        Path filePath = get(OUTPUT).toAbsolutePath().normalize().resolve(filename);
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
