package com.neo.mapper;

import java.util.List;

import com.neo.model.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	
	List<User> getAll();
	
	User getOne(Long id);

	User getOne1(Long id);

	void insert(User user);

	void update(User user);

	void delete(Long id);

}