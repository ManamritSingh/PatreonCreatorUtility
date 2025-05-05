//package com.patreon.backend;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public class UserRepository{
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//
//	public List<String> findAllEmails(){
//
//		return jdbcTemplate.queryForList("SELECT email FROM users", String.class);
//	}
//}
