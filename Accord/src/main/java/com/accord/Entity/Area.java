package com.accord.Entity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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

    @OneToMany(mappedBy = "area", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

    public Double getAverageRating() {
        return ratings.isEmpty() ? 0.0 : ratings.stream()
                .mapToInt(Rating::getStars)
                .average()
                .orElse(0.0);
    }
    public String getTrend() {
        Double averageRating = getAverageRating();
        return averageRating > 3.0 ? "Up" : "Down";
    }

	public String generateBase64Cover() {
		return Base64.encodeBase64String(this.coverDocument);
	}
	public String generateBase64Add() {
		return Base64.encodeBase64String(this.addDocument);
	}
}
