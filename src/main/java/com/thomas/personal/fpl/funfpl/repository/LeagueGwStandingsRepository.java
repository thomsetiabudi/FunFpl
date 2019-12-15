package com.thomas.personal.fpl.funfpl.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
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

	List<TblLeagueGwStandings> findTop2ByLeagueIdAndEventIdOrderByPlayerOverallStandingsOrderAsc(Long leagueid,
			Long event);

	List<TblLeagueGwStandings> findTop3ByLeagueIdAndEventIdOrderByPlayerGwStandingsRankPositionGainDesc(Long leagueid,
			Long event);

	List<TblLeagueGwStandings> findTop3ByLeagueIdAndEventIdOrderByPlayerOverallStandingsPositionGainDesc(Long leagueid,
			Long event);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerGwTransferCountStandingsRankOrderByPlayerGwTransferCountStandingsOrderAsc(
			Long leagueid, Long event, int playerGwTransferCountStandingsRank);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerGwTransferCostStandingsRankOrderByPlayerGwTransferCostStandingsOrderAsc(
			Long leagueid, Long event, int playerGwTransferCostStandingsRank);

	List<TblLeagueGwStandings> findTop3ByLeagueIdAndEventIdOrderByPlayerOverallTransferCostStandingsOrderAsc(
			Long leagueid, Long event);

	List<TblLeagueGwStandings> findTop3ByLeagueIdAndEventIdOrderByPlayerOverallTransferCountStandingsOrderAsc(
			Long leagueid, Long event);

	List<TblLeagueGwStandings> findTop3ByLeagueIdAndEventIdOrderByPlayerOverallBenchPointStandingsOrderAsc(
			Long leagueid, Long event);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerGwBankValueStandingsRankOrderByPlayerGwBankValueStandingsOrderAsc(
			Long leagueid, Long event, int playerGwBankValueStandingsRank);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerGwTeamValueStandingsRankOrderByPlayerGwTeamValueStandingsOrderAsc(
			Long leagueid, Long event, int playerGwTeamValueStandingsRank);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdAndPlayerGwBenchPointStandingsRankOrderByPlayerGwBenchPointStandingsOrderAsc(
			Long leagueid, Long event, int playerGwBenchPointStandingsRank);

	List<TblLeagueGwStandings> findTop5ByLeagueIdAndEventIdOrderByPlayerOverallStandingsOrderAsc(Long leagueid,
			Long event);

	List<TblLeagueGwStandings> findByLeagueIdAndEventIdOrderByPlayerOverallStandingsOrderAsc(Long leagueId, Long event);

	List<TblLeagueGwStandings> findByLeagueIdOrderByEventIdAsc(Long leagueid);

	@Query("SELECT new map(L.playerEntryId AS playerEntryId, COUNT(L) AS dataCount) FROM TblLeagueGwStandings L "
			+ "WHERE L.leagueId = ?2 AND L.eventId <= ?1 "
			+ "AND L.playerGwStandingsRank = 1 "
			+ "GROUP BY L.playerEntryId ")
	List<Map<String, Object>> getTopScorerGwCount(Long event, Long leagueid);

	@Query("SELECT new map(L.playerEntryId AS playerEntryId, COUNT(L) AS dataCount) FROM TblLeagueGwStandings L "
			+ "WHERE L.leagueId = ?2 AND L.eventId <= ?1 "
			+ "AND L.playerIsGwStandingsLastPos = true "
			+ "GROUP BY L.playerEntryId ")
	List<Map<String, Object>> getLowestScoreGwCount(Long event, Long leagueid);

	@Query("SELECT L.eventId FROM TblLeagueGwStandings L "
			+ "WHERE L.playerEntryId = ?1 AND L.leagueId = ?2 AND L.eventId <= ?3 "
			+ "AND L.playerGwStandingsRank = ?4 "
			+ "ORDER BY L.eventId ")
	List<Long> getEventIdListByPlayerEntryIdAndLeagueIdAndMaxEventIdAndPlayerGwStandingsRank(Long playerEntryId, Long leagueid,
			Long event, Integer playerGwStandingsRank);

	@Query("SELECT L.eventId FROM TblLeagueGwStandings L "
			+ "WHERE L.playerEntryId = ?1 AND L.leagueId = ?2 AND L.eventId <= ?3 "
			+ "AND L.playerIsGwStandingsLastPos = ?4 "
			+ "ORDER BY L.eventId ")
	List<Long> getEventIdListByPlayerEntryIdAndLeagueIdAndMaxEventIdAndPlayerIsGwStandingsLastPos(Long id,
			Long leagueid, Long event, Boolean playerIsGwStandingsLastPos);

}
