package com.thomas.personal.fpl.funfpl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.thomas.personal.fpl.funfpl.persistence.TblLeaguePlayer;

public interface LeaguePlayerRepository extends CrudRepository<TblLeaguePlayer, Long> {

	Optional<TblLeaguePlayer> findByPlayerEntryIdAndLeagueId(Long playerEntryId, Long leagueId);

	List<TblLeaguePlayer> findByLeagueId(Long leagueId);


}
