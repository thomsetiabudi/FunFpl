package com.thomas.personal.fpl.funfpl.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.thomas.personal.fpl.funfpl.persistence.TblPlayerEvent;

public interface PlayerEventRepository extends CrudRepository<TblPlayerEvent, Long> {

	Optional<TblPlayerEvent> findByPlayerEntryIdAndEventId(Long playerEntryId, Long eventId);

	TblPlayerEvent findByEventIdAndPlayerEntryId(Long event, Long playerEntryId);

	

}
