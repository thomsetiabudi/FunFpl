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

}
