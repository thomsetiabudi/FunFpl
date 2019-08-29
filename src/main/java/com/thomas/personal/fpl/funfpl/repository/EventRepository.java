package com.thomas.personal.fpl.funfpl.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.thomas.personal.fpl.funfpl.persistence.TblEvent;

public interface EventRepository extends CrudRepository<TblEvent, Long> {

	List<TblEvent> findByStatusOrderByEventDesc(String status);

	List<TblEvent> findByStatusOrderByEventAsc(String status);

	

}
