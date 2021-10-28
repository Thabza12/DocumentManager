//package com.vico.Document_Manager_spring.domain;
//
//import lombok.Data;
//
//import javax.persistence.*;
//
//@Data
//@Entity
//@Table(name = "docs")
//public class Document {
//
//    @Id
//    @GeneratedValue(strategy=GenerationType.IDENTITY)
//    public Integer id;
//    public String docName;
//    public String docType;
//    @Lob
//    public byte[] data;
//
//    public Document() {
//    }
//
//    public Document(String docName, String docType, byte[] data) {
//        this.docName = docName;
//        this.docType = docType;
//        this.data = data;
//    }
//}
