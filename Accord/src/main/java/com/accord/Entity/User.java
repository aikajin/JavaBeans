package com.accord.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

	private Boolean confirmation_account;

	private String tenancy_name;

	private String tenancy_type;
	@Lob
	@Column(length = 1999999999)
	private byte[] tenancy_document;

	private String id_name;

	private String id_type;
	@Lob
	@Column(length = 1999999999)
	private byte[] id_document;

	private String role;

	@Lob
	@Column(length = 1999999999)
	private byte[] profile_picture;

	private String profile_name;

	private String profile_type;
	private String resetToken;
	
}
