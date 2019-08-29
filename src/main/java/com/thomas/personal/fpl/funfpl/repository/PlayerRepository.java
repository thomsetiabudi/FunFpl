package com.thomas.personal.fpl.funfpl.repository;

import org.springframework.data.repository.CrudRepository;

import com.thomas.personal.fpl.funfpl.persistence.TblPlayer;

public interface PlayerRepository extends CrudRepository<TblPlayer, Long> {

	

}
