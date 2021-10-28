package com.vico.Document_Manager_spring.service;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;


@Service
public class DocumentService {

    public static final String USER_PASSWORD = "password";
    public static final String OWNER_PASSWORD = "password1";


    public void encryptPdf(File src, String dest){
        try {
            Document doc = new Document();
            PdfReader reader = new PdfReader(String.valueOf(src));
            FileOutputStream out = new FileOutputStream(dest);
            PdfWriter writer = PdfWriter.getInstance(doc, out);
            writer.setEncryption(USER_PASSWORD.getBytes(), OWNER_PASSWORD.getBytes(), writer.ALLOW_PRINTING,
                    writer.ENCRYPTION_AES_256);
            out.close();
            reader.close();
            doc.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
