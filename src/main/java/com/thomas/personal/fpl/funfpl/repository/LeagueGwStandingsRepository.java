package com.thomas.personal.fpl.funfpl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.thomas.personal.fpl.funfpl.persistence.TblLeagueGwStandings;

public interface LeagueGwStandingsRepository extends CrudRepository<TblLeagueGwStandings, Long> {

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdOrderByPlayerGwStandingsOrderDesc(Long leagueId, Long eventId);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdOrderByPlayerGwStandingsOrderAsc(Long leagueId, Long event);

	Optional<TblLeagueGwStandings> findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(Long event, Long leagueId);

	TblLeagueGwStandings findByLeagueIdAndEventIdAndPlayerEntryId(Long leagueId, Long event, Long playerEntryId);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerGwStandingsRankOrderByPlayerGwStandingsOrderAsc(
			Long leagueid, Long event, int standingsRank);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerIsGwStandingsLastPosOrderByPlayerGwStandingsOrderDesc(
			Long leagueid, Long event, boolean playerIsGwStandingsLastPos);

}
