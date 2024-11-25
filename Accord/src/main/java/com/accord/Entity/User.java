package com.accord.Entity;

import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
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
@Table(name = "user_table")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	private String password;

	private String email;

	private String contactnumber;

	private int block_num;

	private int lot_num;

	private String property_status;

	private Boolean confirmation_email;

	private Boolean confirmationAccount;

	private String tenancy_name;

	private String tenancy_type;
	@Lob
	@Column(length = 2139999999)
	private byte[] tenancy_document;

	private String id_name;

	private String id_type;
	@Lob
	@Column(length = 2139999999)
	private byte[] id_document;

	private String role;

	@Lob
	@Column(length = 2139999999)
	private byte[] profile_picture;

	private String profile_name;

	private String profile_type;

	private String resetToken;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rating> ratings = new ArrayList<>();

	
	@Column(name = "profile_picture_url")
	private String profilePictureUrl; // Add getter and setter


    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }
	
	public String generateBase64Tenancy() {
		return Base64.encodeBase64String(this.tenancy_document);
	}
	public String generateBase64ValidId() {
		return Base64.encodeBase64String(this.id_document);
	}

	public String generateBase64Profile() {
		return Base64.encodeBase64String(this.profile_picture);
	}
}
