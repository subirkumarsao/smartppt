package com.lazybuds.smartppt.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lazybuds.smartppt.entity.User;
import com.lazybuds.smartppt.entity.UserSession;

@Component
public class CommonDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	public void save(Object object){
		sessionFactory.getCurrentSession().save(object);
	}
	
	public User getUser(String userId){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(User.class);
		criteria.add(Restrictions.eq("userId", userId));
		
		@SuppressWarnings("unchecked")
		List<User> users = criteria.list();
		if(users.isEmpty()){
			return null;
		}
		return users.get(0);
	}
	
	public UserSession getUserSession(String sessionId){
		Criteria criteria = sessionFactory.getCurrentSession().createCriteria(UserSession.class);
		criteria.add(Restrictions.eq("sessionId", sessionId));
		
		@SuppressWarnings("unchecked")
		List<UserSession> userSessions = criteria.list();
		if(userSessions.isEmpty()){
			return null;
		}
		return userSessions.get(0);
	}
}
