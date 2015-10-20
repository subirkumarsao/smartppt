package com.lazybuds.smartppt.service;

import java.io.IOException;
import java.util.UUID;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lazybuds.smartppt.dao.CommonDao;
import com.lazybuds.smartppt.datatype.SlideState;
import com.lazybuds.smartppt.entity.User;
import com.lazybuds.smartppt.entity.UserSession;

@Component
@Transactional
public class SessionService {
	
	private static final String PARENT_FOLDER_ID = "0B8jaxqkPByeAV2JVZ3A3d19ZcFU";

	@Autowired
	CommonDao commonDao;
	
	@Autowired
	GoogleDriverService googleDriverService;
	
	public String createSession(){
		
		UUID uuid = UUID.randomUUID();
		
		UserSession userSession = new UserSession();
		userSession.sessionId = uuid.toString();
		
		commonDao.save(userSession);
		
		return uuid.toString();
	}
	
	public void linkSession(String sessionId, String userId){
		User user = commonDao.getUser(userId);
		if(user==null){
			throw new EntityNotFoundException("User not found"); 
		}
		
		UserSession userSession = commonDao.getUserSession(sessionId);
		if(userSession==null){
			throw new EntityNotFoundException("Session not found"); 
		}
		
		userSession.user = user;
		commonDao.save(userSession);
	}
	
	public boolean checkSessionLink(String sessionId){
		UserSession userSession = commonDao.getUserSession(sessionId);
		return (userSession.user!=null);
	}
	
	public void uploadFile(String sessionId, byte[] data) throws IOException{
		UserSession userSession = commonDao.getUserSession(sessionId);
		if(userSession==null){
			throw new EntityNotFoundException("Session not found"); 
		}
		
		String fileId = googleDriverService.uploadFile(data, UUID.randomUUID().toString(), PARENT_FOLDER_ID);	
		userSession.fileId = fileId;
		
		commonDao.save(userSession);
	}
	
	public String getSlideState(String sessionId){
		UserSession userSession = commonDao.getUserSession(sessionId);
		String url = "https://docs.google.com/presentation/d/"+userSession.fileId+"/embed#slide="+userSession.currentSlide;
		return url;
	}
	
	public void setSlideState(String sessionId, String currentSlide){
		UserSession userSession = commonDao.getUserSession(sessionId);
		userSession.currentSlide = currentSlide;
		
		commonDao.save(userSession);
	}
}
