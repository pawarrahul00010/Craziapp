package com.technohertz.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.technohertz.model.GroupProfile;

@Repository
public interface GroupProfileRepository extends JpaRepository<GroupProfile, Integer>{

	@Query("SELECT g from GroupProfile g where groupId=?1")
	List<GroupProfile> findAllById(Integer groupId);

	

}
