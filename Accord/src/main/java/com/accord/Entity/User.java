package com.accord.Entity;

import java.util.Arrays;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_table")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String email;
	@Column(nullable = false)
	private String contactnumber;
	@Column(nullable = false)
	private int block_num;
	@Column(nullable = false)
	private int lot_num;
	@Column(nullable = false)
	private String property_status;
	@Column(nullable = true)
	private Boolean confirmation_email;
	@Column(nullable = true)
	private Boolean confirmation_account;
	@Column(nullable = false)
	private String tenancy_name;
	@Column(nullable = false)
	private String tenancy_type;
	@Lob
	@Column(nullable = false, length = 1999999999)
	private byte[] tenancy_document;
	@Column(nullable = false)
	private String id_name;
	@Column(nullable = false)
	private String id_type;
	@Lob
	@Column(nullable = false, length = 1999999999)
	private byte[] id_document;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getContactnumber() {
		return contactnumber;
	}
	public void setContactnumber(String contactnumber) {
		this.contactnumber = contactnumber;
	}
	public int getBlock_num() {
		return block_num;
	}
	public void setBlock_num(int block_num) {
		this.block_num = block_num;
	}
	public int getLot_num() {
		return lot_num;
	}
	public void setLot_num(int lot_num) {
		this.lot_num = lot_num;
	}
	public String getProperty_status() {
		return property_status;
	}
	public void setProperty_status(String property_status) {
		this.property_status = property_status;
	}
	public Boolean getConfirmation_email() {
		return confirmation_email;
	}
	public void setConfirmation_email(Boolean confirmation_email) {
		this.confirmation_email = confirmation_email;
	}
	public Boolean getConfirmation_account() {
		return confirmation_account;
	}
	public void setConfirmation_account(Boolean confirmation_account) {
		this.confirmation_account = confirmation_account;
	}
	public String getTenancy_name() {
		return tenancy_name;
	}
	public void setTenancy_name(String tenancy_name) {
		this.tenancy_name = tenancy_name;
	}
	public String getTenancy_type() {
		return tenancy_type;
	}
	public void setTenancy_type(String tenancy_type) {
		this.tenancy_type = tenancy_type;
	}
	public byte[] getTenancy_document() {
		return tenancy_document;
	}
	public void setTenancy_document(byte[] tenancy_document) {
		this.tenancy_document = tenancy_document;
	}
	
	public String getId_name() {
		return id_name;
	}
	public void setId_name(String id_name) {
		this.id_name = id_name;
	}
	public String getId_type() {
		return id_type;
	}
	public void setId_type(String id_type) {
		this.id_type = id_type;
	}
	public byte[] getId_document() {
		return id_document;
	}
	public void setId_document(byte[] id_document) {
		this.id_document = id_document;
	}

	public User(Long id, String name, String password, String email, String contactnumber, int block_num, int lot_num,
			String property_status, Boolean confirmation_email, Boolean confirmation_account, String tenancy_name,
			String tenancy_type, byte[] tenancy_document, String id_name, String id_type, byte[] id_document) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
		this.email = email;
		this.contactnumber = contactnumber;
		this.block_num = block_num;
		this.lot_num = lot_num;
		this.property_status = property_status;
		this.confirmation_email = confirmation_email;
		this.confirmation_account = confirmation_account;
		this.tenancy_name = tenancy_name;
		this.tenancy_type = tenancy_type;
		this.tenancy_document = tenancy_document;
		this.id_name = id_name;
		this.id_type = id_type;
		this.id_document = id_document;
	}
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
