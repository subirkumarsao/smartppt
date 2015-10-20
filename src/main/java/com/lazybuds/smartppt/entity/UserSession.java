package com.lazybuds.smartppt.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class UserSession {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO) 
	public Long id;
	
	public String sessionId;
	
	@OneToOne
	public User user;
	
	public String fileId;
	
	
	public String currentSlide;
	
}
