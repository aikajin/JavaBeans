package com.accord.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "area")
public class Area {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Lob
    @Column(length = 16777215)
    private String guidelines;

    private LocalTime startTime;

    private LocalTime endTime;

    private Boolean available;

    @Lob
    @Column(length = 2139999999)
    private byte[] coverDocument;

    private String coverName;

    private String coverType;

    @Lob
    @Column(length = 2139999999)
    private byte[] addDocument;

    private String addName;

    private String addType;

	public String generateBase64Cover() {
		return Base64.encodeBase64String(this.coverDocument);
	}
	public String generateBase64Add() {
		return Base64.encodeBase64String(this.addDocument);
	}
}
