package com.thomas.personal.fpl.funfpl.bl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomas.personal.fpl.funfpl.model.LeagueDto;
import com.thomas.personal.fpl.funfpl.model.LeagueGwStandingsDataDownloadDto;
import com.thomas.personal.fpl.funfpl.model.LeagueGwStandingsDataDto;
import com.thomas.personal.fpl.funfpl.persistence.TblEvent;
import com.thomas.personal.fpl.funfpl.persistence.TblLeague;
import com.thomas.personal.fpl.funfpl.persistence.TblLeagueGwStandings;
import com.thomas.personal.fpl.funfpl.persistence.TblPlayer;
import com.thomas.personal.fpl.funfpl.repository.EventRepository;
import com.thomas.personal.fpl.funfpl.repository.LeagueGwStandingsRepository;
import com.thomas.personal.fpl.funfpl.repository.LeaguePlayerRepository;
import com.thomas.personal.fpl.funfpl.repository.LeagueRepository;
import com.thomas.personal.fpl.funfpl.repository.PlayerEventRepository;
import com.thomas.personal.fpl.funfpl.repository.PlayerRepository;

@Component
public class DataViewBl {

	private static final String LINE_BREAK = "\r\n";
	
	private static final String DATA_COUNT_FIELD_NAME = "dataCount";

	private PlayerRepository playerRepository;

	private LeagueRepository leagueRepository;

	private LeaguePlayerRepository leaguePlayerRepository;

	private PlayerEventRepository playerEventRepository;

	private EventRepository eventRepository;

	private LeagueGwStandingsRepository leagueGwStandingsRepository;

	@Autowired
	public DataViewBl(PlayerRepository playerRepository, LeagueRepository leagueRepository,
			LeaguePlayerRepository leaguePlayerRepository, PlayerEventRepository playerEventRepository,
			EventRepository eventRepository, LeagueGwStandingsRepository leagueGwStandingsRepository) {
		this.playerRepository = playerRepository;
		this.leagueRepository = leagueRepository;
		this.leaguePlayerRepository = leaguePlayerRepository;
		this.playerEventRepository = playerEventRepository;
		this.eventRepository = eventRepository;
		this.leagueGwStandingsRepository = leagueGwStandingsRepository;
	}

	public List<LeagueDto> getLeagueList() {
		List<LeagueDto> result = new ArrayList<>();

		Iterable<TblLeague> leagueList = leagueRepository.findAll();
		for (TblLeague tblLeague : leagueList) {
			LeagueDto leagueData = new LeagueDto();
			leagueData.setId(tblLeague.getId());
			leagueData.setName(tblLeague.getName());
			result.add(leagueData);
		}

		return result;

	}

	public List<Long> getAvailableGwStandings() {
		List<Long> result = new ArrayList<>();

		List<TblEvent> availableEventList = eventRepository.findByStatusOrderByEventDesc("FINISH");

		for (TblEvent tblEvent : availableEventList) {
			result.add(tblEvent.getEvent());
		}

		return result;
	}

	public List<LeagueGwStandingsDataDto> getLeagueGwStandings(Long leagueId, Long event) {
		List<LeagueGwStandingsDataDto> result = new ArrayList<>();
		Optional<TblLeague> league = leagueRepository.findById(leagueId);

		if (!league.isPresent()) {
			return result;
		}

		List<TblLeagueGwStandings> leagueGwStandingsList = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdOrderByPlayerGwStandingsOrderAsc(leagueId, event);

		for (TblLeagueGwStandings tblLeagueGwStandings : leagueGwStandingsList) {
			LeagueGwStandingsDataDto leagueGwStandingData = new LeagueGwStandingsDataDto();

			Optional<TblPlayer> player = playerRepository.findById(tblLeagueGwStandings.getPlayerEntryId());
			if (!player.isPresent()) {
				continue;
			}

			leagueGwStandingData.setLeagueId(leagueId);
			leagueGwStandingData.setLeagueName(league.get().getName());
			leagueGwStandingData.setPlayerEntryId(tblLeagueGwStandings.getPlayerEntryId());
			leagueGwStandingData.setPlayerEntryName(player.get().getEntryName());
			leagueGwStandingData.setPlayerEventScore(tblLeagueGwStandings.getPlayerEventScore());
			leagueGwStandingData.setPlayerEventScoreReal(tblLeagueGwStandings.getPlayerEventScoreReal());
			leagueGwStandingData.setPlayerEventTransferCost(tblLeagueGwStandings.getPlayerEventTransferCost());
			leagueGwStandingData.setPlayerName(player.get().getPlayerName());
			leagueGwStandingData.setPlayerNick(player.get().getPlayerNick());
			leagueGwStandingData.setPlayerPositionGain(tblLeagueGwStandings.getPlayerGwStandingsPositionGain());
			leagueGwStandingData.setPlayerPositionGainReal(tblLeagueGwStandings.getPlayerGwRealStandingsPositionGain());
			leagueGwStandingData.setPlayerPrevStandingsOrder(tblLeagueGwStandings.getPlayerPrevGwStandingsOrder());
			leagueGwStandingData.setPlayerPrevStandingsOrderReal(tblLeagueGwStandings.getPlayerPrevGwRealStandingsOrder());
			leagueGwStandingData.setPlayerStandingsOrder(tblLeagueGwStandings.getPlayerGwStandingsOrder());
			leagueGwStandingData.setPlayerStandingsOrderReal(tblLeagueGwStandings.getPlayerGwRealStandingsOrder());
			leagueGwStandingData.setPlayerTotalScore(tblLeagueGwStandings.getPlayerTotalScore());

			leagueGwStandingData.setPlayerStandingsRank(tblLeagueGwStandings.getPlayerGwStandingsRank());
			leagueGwStandingData.setPlayerStandingsRankReal(tblLeagueGwStandings.getPlayerGwRealStandingsRank());
			leagueGwStandingData.setPlayerPrevStandingsRank(tblLeagueGwStandings.getPlayerPrevGwStandingsRank());
			leagueGwStandingData.setPlayerPrevStandingsRankReal(tblLeagueGwStandings.getPlayerPrevGwRealStandingsRank());
			leagueGwStandingData.setPlayerRankGain(tblLeagueGwStandings.getPlayerGwStandingsRankPositionGain());
			leagueGwStandingData.setPlayerRankGainReal(tblLeagueGwStandings.getPlayerGwRealStandingsRankPositionGain());

			leagueGwStandingData.setPlayerIsLastGwStandingsRank(tblLeagueGwStandings.getPlayerIsGwStandingsLastPos());
			leagueGwStandingData.setPlayerIsLastGwStandingsRankReal(tblLeagueGwStandings.getPlayerIsGwRealStandingsLastPos());
			leagueGwStandingData.setActiveChip(tblLeagueGwStandings.getPlayerActiveChip());

			result.add(leagueGwStandingData);
		}

		return result;
	}
	
	public List<LeagueGwStandingsDataDto> getLeagueGwRealStandings(Long leagueId, Long event) {
		List<LeagueGwStandingsDataDto> result = new ArrayList<>();
		Optional<TblLeague> league = leagueRepository.findById(leagueId);

		if (!league.isPresent()) {
			return result;
		}

		List<TblLeagueGwStandings> leagueGwStandingsList = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdOrderByPlayerGwRealStandingsOrderAsc(leagueId, event);

		for (TblLeagueGwStandings tblLeagueGwStandings : leagueGwStandingsList) {
			LeagueGwStandingsDataDto leagueGwStandingData = new LeagueGwStandingsDataDto();

			Optional<TblPlayer> player = playerRepository.findById(tblLeagueGwStandings.getPlayerEntryId());
			if (!player.isPresent()) {
				continue;
			}

			leagueGwStandingData.setLeagueId(leagueId);
			leagueGwStandingData.setLeagueName(league.get().getName());
			leagueGwStandingData.setPlayerEntryId(tblLeagueGwStandings.getPlayerEntryId());
			leagueGwStandingData.setPlayerEntryName(player.get().getEntryName());
			leagueGwStandingData.setPlayerEventScore(tblLeagueGwStandings.getPlayerEventScore());
			leagueGwStandingData.setPlayerEventScoreReal(tblLeagueGwStandings.getPlayerEventScoreReal());
			leagueGwStandingData.setPlayerEventTransferCost(tblLeagueGwStandings.getPlayerEventTransferCost());
			leagueGwStandingData.setPlayerName(player.get().getPlayerName());
			leagueGwStandingData.setPlayerNick(player.get().getPlayerNick());
			leagueGwStandingData.setPlayerPositionGain(tblLeagueGwStandings.getPlayerGwStandingsPositionGain());
			leagueGwStandingData.setPlayerPositionGainReal(tblLeagueGwStandings.getPlayerGwRealStandingsPositionGain());
			leagueGwStandingData.setPlayerPrevStandingsOrder(tblLeagueGwStandings.getPlayerPrevGwStandingsOrder());
			leagueGwStandingData.setPlayerPrevStandingsOrderReal(tblLeagueGwStandings.getPlayerPrevGwRealStandingsOrder());
			leagueGwStandingData.setPlayerStandingsOrder(tblLeagueGwStandings.getPlayerGwStandingsOrder());
			leagueGwStandingData.setPlayerStandingsOrderReal(tblLeagueGwStandings.getPlayerGwRealStandingsOrder());
			leagueGwStandingData.setPlayerTotalScore(tblLeagueGwStandings.getPlayerTotalScore());

			leagueGwStandingData.setPlayerStandingsRank(tblLeagueGwStandings.getPlayerGwStandingsRank());
			leagueGwStandingData.setPlayerStandingsRankReal(tblLeagueGwStandings.getPlayerGwRealStandingsRank());
			leagueGwStandingData.setPlayerPrevStandingsRank(tblLeagueGwStandings.getPlayerPrevGwStandingsRank());
			leagueGwStandingData.setPlayerPrevStandingsRankReal(tblLeagueGwStandings.getPlayerPrevGwRealStandingsRank());
			leagueGwStandingData.setPlayerRankGain(tblLeagueGwStandings.getPlayerGwStandingsRankPositionGain());
			leagueGwStandingData.setPlayerRankGainReal(tblLeagueGwStandings.getPlayerGwRealStandingsRankPositionGain());

			leagueGwStandingData.setPlayerIsLastGwStandingsRank(tblLeagueGwStandings.getPlayerIsGwStandingsLastPos());
			leagueGwStandingData.setPlayerIsLastGwStandingsRankReal(tblLeagueGwStandings.getPlayerIsGwRealStandingsLastPos());
			leagueGwStandingData.setActiveChip(tblLeagueGwStandings.getPlayerActiveChip());

			result.add(leagueGwStandingData);
		}

		return result;
	}

	public String createLeagueGwPointRankCopyText(List<LeagueGwStandingsDataDto> leagueStandingsData, Long event) {
		StringBuilder sb = new StringBuilder();

		sb.append(leagueStandingsData.get(0).getLeagueName());
		sb.append("⚽🦁");
		sb.append(LINE_BREAK);

		sb.append("*Gameweek ");
		sb.append(event);
		sb.append("*");
		sb.append(LINE_BREAK);

		sb.append("Klasemen poin per Gameweek:");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		for (Integer index = 0; index < leagueStandingsData.size(); index++) {
			LeagueGwStandingsDataDto data = leagueStandingsData.get(index);

			sb.append(data.getPlayerStandingsOrder());
			sb.append(" ( *");
			sb.append(data.getPlayerStandingsRank());
			sb.append("* ) ");
			sb.append(data.getPlayerNick());

			if (data.getPlayerStandingsRank().equals(1)) {
				sb.append(" 🏆");
			} else if (data.getPlayerStandingsRank().equals(2)) {
				sb.append(" 🥈");
			}

			if (data.getPlayerIsLastGwStandingsRank() != null && data.getPlayerIsLastGwStandingsRank()) {
				sb.append(" 🎉");
			}

			sb.append(" ( *");
			sb.append(data.getPlayerEventScore());
			sb.append("* | ");
			sb.append(data.getPlayerTotalScore());

			if (data.getActiveChip() != null) {
				sb.append(" | ");
				sb.append(data.getActiveChip());
			}

			sb.append(")");
			sb.append(LINE_BREAK);
		}

		return sb.toString();
	}
	
	public String createLeagueGwRealPointRankCopyText(List<LeagueGwStandingsDataDto> leagueStandingsDataParam, Long event, Long leagueId) {
		List<LeagueGwStandingsDataDto> leagueStandingsData = getLeagueGwRealStandings(leagueId, event);
		
		StringBuilder sb = new StringBuilder();

		sb.append(leagueStandingsData.get(0).getLeagueName());
		sb.append("⚽🦁");
		sb.append(LINE_BREAK);

		sb.append("*Gameweek ");
		sb.append(event);
		sb.append("*");
		sb.append(LINE_BREAK);

		sb.append("Klasemen poin per Gameweek (minus transfer):");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		for (Integer index = 0; index < leagueStandingsData.size(); index++) {
			LeagueGwStandingsDataDto data = leagueStandingsData.get(index);

			sb.append(data.getPlayerStandingsOrderReal());
			sb.append(" ( *");
			sb.append(data.getPlayerStandingsRankReal());
			sb.append("* ) ");
			sb.append(data.getPlayerNick());

			if (data.getPlayerStandingsRankReal().equals(1)) {
				sb.append(" 🏆");
			} else if (data.getPlayerStandingsRankReal().equals(2)) {
				sb.append(" 🥈");
			}

			if (data.getPlayerIsLastGwStandingsRankReal() != null && data.getPlayerIsLastGwStandingsRankReal()) {
				sb.append(" 🎉");
			}

			sb.append(" ( *");
			sb.append(data.getPlayerEventScoreReal());
			sb.append("* | ");
			sb.append(data.getPlayerTotalScore());
			
			if(data.getPlayerEventTransferCost() > 0L) {
				sb.append(" | ");
				sb.append(data.getPlayerEventScore());
				sb.append("-");
				sb.append(data.getPlayerEventTransferCost());
			}

			if (data.getActiveChip() != null) {
				sb.append(" | ");
				sb.append(data.getActiveChip());
			}

			sb.append(")");
			
			if(index == 0) {
				sb.append(" tabungan donasi 35rb");
			} else if(index == 1) {
				sb.append(" tabungan donasi 15rb");
			} else if (index == leagueStandingsData.size() - 1) {
				sb.append(" donasi 15rb");
			} else if (index == leagueStandingsData.size() - 2) {
				sb.append(" donasi 15rb");
			} else if (index == leagueStandingsData.size() - 3) {
				sb.append(" donasi 10rb");
			} else if (index == leagueStandingsData.size() - 4) {
				sb.append(" donasi 5rb");
			} else if (index == leagueStandingsData.size() - 5) {
				sb.append(" donasi 5rb");
			}
			
			sb.append(LINE_BREAK);
		}

		return sb.toString();
	}
	
	public String createLeagueGwRealSummaryCopyText(List<LeagueGwStandingsDataDto> leagueStandingsData, Long event,
			Long leagueid) {
		Map<Long, TblPlayer> playerMap = new HashMap<>();
		Iterable<TblPlayer> playerList = playerRepository.findAll();

		for (TblPlayer player : playerList) {
			playerMap.put(player.getId(), player);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(leagueStandingsData.get(0).getLeagueName());
		sb.append("⚽🦁");
		sb.append(LINE_BREAK);

		sb.append("*Gameweek (GW) ");
		sb.append(event);
		sb.append("*");
		sb.append(LINE_BREAK);

		sb.append("Summary");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Top Score, GW (minus transfer):*");
		sb.append(LINE_BREAK);
		createTopScoreGwRealString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Lowest Score, GW (minus transfer):*");
		sb.append(LINE_BREAK);
		createLowestScoreGwRealString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*League Leader, Season:*");
		sb.append(LINE_BREAK);
		createLeagueLeaderString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Position Gain, GW (minus transfer):*");
		sb.append(LINE_BREAK);
		createMostPositionGainGwRealString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Position Gain, Season:*");
		sb.append(LINE_BREAK);
		createMostPositionGainOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer, GW:*");
		sb.append(LINE_BREAK);
		createMostTransferGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Transfer, Season:*");
		sb.append(LINE_BREAK);
		createMostTransferOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer Cost, GW:*");
		sb.append(LINE_BREAK);
		createMostTransferCostGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Transfer Cost, Season:*");
		sb.append(LINE_BREAK);
		createMostTransferCostOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Point on Bench, GW:*");
		sb.append(LINE_BREAK);
		createMostPointOnBenchGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Point on Bench, Season:*");
		sb.append(LINE_BREAK);
		createMostPointOnBenchOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Money on Bank, GW:*");
		sb.append(LINE_BREAK);
		createMostBankGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Valuable Team, GW:*");
		sb.append(LINE_BREAK);
		createMostValueGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		return sb.toString();
	}

	public String createLeagueGwSummaryCopyText(List<LeagueGwStandingsDataDto> leagueStandingsData, Long event,
			Long leagueid) {
		Map<Long, TblPlayer> playerMap = new HashMap<>();
		Iterable<TblPlayer> playerList = playerRepository.findAll();

		for (TblPlayer player : playerList) {
			playerMap.put(player.getId(), player);
		}

		StringBuilder sb = new StringBuilder();

		sb.append(leagueStandingsData.get(0).getLeagueName());
		sb.append("⚽🦁");
		sb.append(LINE_BREAK);

		sb.append("*Gameweek (GW) ");
		sb.append(event);
		sb.append("*");
		sb.append(LINE_BREAK);

		sb.append("Summary");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Top Score, GW:*");
		sb.append(LINE_BREAK);
		createTopScoreGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Lowest Score, GW:*");
		sb.append(LINE_BREAK);
		createLowestScoreGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*League Leader, Season:*");
		sb.append(LINE_BREAK);
		createLeagueLeaderString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Position Gain, GW:*");
		sb.append(LINE_BREAK);
		createMostPositionGainGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Position Gain, Season:*");
		sb.append(LINE_BREAK);
		createMostPositionGainOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer, GW:*");
		sb.append(LINE_BREAK);
		createMostTransferGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Transfer, Season:*");
		sb.append(LINE_BREAK);
		createMostTransferOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer Cost, GW:*");
		sb.append(LINE_BREAK);
		createMostTransferCostGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Transfer Cost, Season:*");
		sb.append(LINE_BREAK);
		createMostTransferCostOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Point on Bench, GW:*");
		sb.append(LINE_BREAK);
		createMostPointOnBenchGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Top 3 Most Point on Bench, Season:*");
		sb.append(LINE_BREAK);
		createMostPointOnBenchOverallString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Money on Bank, GW:*");
		sb.append(LINE_BREAK);
		createMostBankGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Most Valuable Team, GW:*");
		sb.append(LINE_BREAK);
		createMostValueGwString(sb, playerMap, event, leagueid);
		sb.append(LINE_BREAK);

		return sb.toString();
	}

	private void createMostValueGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwTeamValueStandingsRankOrderByPlayerGwTeamValueStandingsOrderAsc(
						leagueid, event, 1);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwTeamValueStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwTeamValueStandingsRank().equals(1)) {
					sb.append(" 😎💰");
				}

				sb.append(" ( *£");
				sb.append(new BigDecimal(data.getPlayerValue()).divide(new BigDecimal(10)));
				sb.append("* | ");
				sb.append(data.getPlayerEventScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}

	private void createMostBankGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwBankValueStandingsRankOrderByPlayerGwBankValueStandingsOrderAsc(
						leagueid, event, 1);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwBankValueStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwBankValueStandingsRank().equals(1)) {
					sb.append(" 🏦💶");
				}

				sb.append(" ( *£");
				sb.append(new BigDecimal(data.getPlayerBank()).divide(new BigDecimal(10)));
				sb.append("* | ");
				sb.append(data.getPlayerEventScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}

	private void createMostPointOnBenchOverallString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerOverallBenchPointStandingsOrderAsc(leagueid, event);

		if (!standings.isEmpty()) {
			Integer orderNumber = 1;
			for (TblLeagueGwStandings data : standings) {
				sb.append(orderNumber);
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (orderNumber.equals(1)) {
					sb.append(" 😅");
				}

				sb.append(" ( *");
				sb.append(data.getPlayerTotalPointsOnBench());
				sb.append("* pts on bench | ");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);

				orderNumber = orderNumber + 1;
			}
		}
	}

	private void createMostPointOnBenchGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerGwBenchPointStandingsOrderAsc(
						leagueid, event);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwBenchPointStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwBenchPointStandingsRank().equals(1)) {
					sb.append(" 😅");
				}

				sb.append(" ( *");
				sb.append(data.getPlayerEventPointsOnBench());
				sb.append("* pts on bench | ");
				sb.append(data.getPlayerEventScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}

	private void createMostTransferCostOverallString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerOverallTransferCostStandingsOrderAsc(leagueid, event);

		if (!standings.isEmpty()) {
			Integer orderNumber = 1;
			for (TblLeagueGwStandings data : standings) {
				sb.append(orderNumber);
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (orderNumber.equals(1)) {
					sb.append(" 💸");
				}

				sb.append(" (");
				sb.append(data.getPlayerTotalTransfer());
				sb.append(" trf | *-");
				sb.append(data.getPlayerTotalTransferCost());
				sb.append("* pts | ");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);

				orderNumber = orderNumber + 1;
			}
		}
	}

	private void createMostTransferCostGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwTransferCostStandingsRankOrderByPlayerGwTransferCostStandingsOrderAsc(
						leagueid, event, 1);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwTransferCostStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwTransferCostStandingsRank().equals(1)) {
					sb.append(" 💸");
				}

				sb.append(" (");
				sb.append(data.getPlayerEventTransfer());
				sb.append(" trf | *-");
				sb.append(data.getPlayerEventTransferCost());
				sb.append("* pts | ");
				sb.append(data.getPlayerEventScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}

	private void createMostTransferOverallString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerOverallTransferCountStandingsOrderAsc(leagueid, event);

		if (!standings.isEmpty()) {
			Integer orderNumber = 1;
			for (TblLeagueGwStandings data : standings) {
				sb.append(orderNumber);
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (orderNumber.equals(1)) {
					sb.append(" 😎");
				}

				sb.append(" ( *");
				sb.append(data.getPlayerTotalTransfer());
				sb.append("* trf | ");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);

				orderNumber = orderNumber + 1;
			}
		}
	}

	private void createMostTransferGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwTransferCountStandingsRankOrderByPlayerGwTransferCountStandingsOrderAsc(
						leagueid, event, 1);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwTransferCountStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwTransferCountStandingsRank().equals(1)) {
					sb.append(" 😎");
				}

				sb.append(" ( *");
				sb.append(data.getPlayerEventTransfer());
				sb.append("* trf | ");
				sb.append(data.getPlayerEventScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}

	}

	private void createMostPositionGainOverallString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerOverallStandingsPositionGainDesc(leagueid, event);

		if (!standings.isEmpty()) {
			Integer orderNumber = 1;
			for (TblLeagueGwStandings data : standings) {
				if (data.getPlayerOverallStandingsPositionGain() <= 0) {
					break;
				}

				sb.append(orderNumber);
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				sb.append(" ⬆ *");
				sb.append(data.getPlayerOverallStandingsPositionGain());
				sb.append("* pos (pos ");
				sb.append(data.getPlayerOverallStandingsOrder());
				sb.append(", was ");
				sb.append(data.getPlayerPrevOverallStandingsOrder());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);

				orderNumber = orderNumber + 1;
			}
		}
	}

	private void createMostPositionGainGwRealString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerGwRealStandingsRankPositionGainDesc(leagueid, event);

		if (!standings.isEmpty()) {
			Integer orderNumber = 1;
			for (TblLeagueGwStandings data : standings) {
				if (data.getPlayerGwRealStandingsRankPositionGain() <= 0) {
					break;
				}

				sb.append(orderNumber);
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				sb.append(" ⬆ *");
				sb.append(data.getPlayerGwRealStandingsRankPositionGain());
				sb.append("* pos (pos ");
				sb.append(data.getPlayerGwRealStandingsRank());
				sb.append(", was ");
				sb.append(data.getPlayerPrevGwRealStandingsRank());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);

				orderNumber = orderNumber + 1;
			}
		}
	}
	
	private void createMostPositionGainGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop3ByLeagueIdAndEventIdOrderByPlayerGwStandingsRankPositionGainDesc(leagueid, event);

		if (!standings.isEmpty()) {
			Integer orderNumber = 1;
			for (TblLeagueGwStandings data : standings) {
				if (data.getPlayerGwStandingsRankPositionGain() <= 0) {
					break;
				}

				sb.append(orderNumber);
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				sb.append(" ⬆ *");
				sb.append(data.getPlayerGwStandingsRankPositionGain());
				sb.append("* pos (pos ");
				sb.append(data.getPlayerGwStandingsRank());
				sb.append(", was ");
				sb.append(data.getPlayerPrevGwStandingsRank());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);

				orderNumber = orderNumber + 1;
			}
		}
	}

	private void createLeagueLeaderString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findTop5ByLeagueIdAndEventIdOrderByPlayerOverallStandingsOrderAsc(leagueid, event);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerOverallStandingsOrder());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerOverallStandingsOrder().equals(1)) {
					sb.append(" 🏆");
				} else if (data.getPlayerOverallStandingsOrder().equals(2)) {
					sb.append(" 🥈");
				}

				sb.append(" (");
				sb.append(data.getPlayerEventScore());
				sb.append(" | *");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append("* | ");
					sb.append(data.getPlayerActiveChip());
				} else {
					sb.append("*");
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}

	}

	private void createLowestScoreGwRealString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerIsGwRealStandingsLastPosOrderByPlayerGwRealStandingsOrderDesc(leagueid, event,
						true);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwRealStandingsOrder());
				sb.append(" (");
				sb.append(data.getPlayerGwRealStandingsRank());
				sb.append(") ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				sb.append(" 🎉");

				sb.append(" ( *");
				sb.append(data.getPlayerEventScoreReal());
				sb.append("* | ");
				sb.append(data.getPlayerTotalScore());
				
				if (data.getPlayerEventTransferCost() > 0L) {
					sb.append(" | ");
					sb.append(data.getPlayerEventScore());
					sb.append("-");
					sb.append(data.getPlayerEventTransferCost());
				}

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}
	
	private void createLowestScoreGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event,
			Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerIsGwStandingsLastPosOrderByPlayerGwStandingsOrderDesc(leagueid, event,
						true);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwStandingsOrder());
				sb.append(" (");
				sb.append(data.getPlayerGwStandingsRank());
				sb.append(") ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				sb.append(" 🎉");

				sb.append(" ( *");
				sb.append(data.getPlayerEventScore());
				sb.append("* | ");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}

	private void createTopScoreGwRealString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwRealStandingsRankOrderByPlayerGwRealStandingsOrderAsc(leagueid, event, 1);
		createTopScoreGwRealString(sb, playerMap, standings);
		standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwRealStandingsRankOrderByPlayerGwRealStandingsOrderAsc(leagueid, event, 2);
		createTopScoreGwRealString(sb, playerMap, standings);
	}
	
	private void createTopScoreGwRealString(StringBuilder sb, Map<Long, TblPlayer> playerMap,
			List<TblLeagueGwStandings> standings) {
		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwRealStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwRealStandingsRank().equals(1)) {
					sb.append(" 🏆");
				} else if (data.getPlayerGwRealStandingsRank().equals(2)) {
					sb.append(" 🥈");
				}

				sb.append(" ( *");
				sb.append(data.getPlayerEventScoreReal());
				sb.append("* | ");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}
				
				if (data.getPlayerEventTransferCost() > 0L) {
					sb.append(" | ");
					sb.append(data.getPlayerEventScore());
					sb.append("-");
					sb.append(data.getPlayerEventTransferCost());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}
	
	private void createTopScoreGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwStandingsRankOrderByPlayerGwStandingsOrderAsc(leagueid, event, 1);
		createTopScoreGwString(sb, playerMap, standings);
		standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwStandingsRankOrderByPlayerGwStandingsOrderAsc(leagueid, event, 2);
		createTopScoreGwString(sb, playerMap, standings);
	}

	private void createTopScoreGwString(StringBuilder sb, Map<Long, TblPlayer> playerMap,
			List<TblLeagueGwStandings> standings) {
		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwStandingsRank());
				sb.append(". ");
				TblPlayer player = playerMap.get(data.getPlayerEntryId());
				if (player != null) {
					sb.append(player.getPlayerNick());
				}

				if (data.getPlayerGwStandingsRank().equals(1)) {
					sb.append(" 🏆");
				} else if (data.getPlayerGwStandingsRank().equals(2)) {
					sb.append(" 🥈");
				}

				sb.append(" ( *");
				sb.append(data.getPlayerEventScore());
				sb.append("* | ");
				sb.append(data.getPlayerTotalScore());

				if (data.getPlayerActiveChip() != null) {
					sb.append(" | ");
					sb.append(data.getPlayerActiveChip());
				}

				sb.append(")");
				sb.append(LINE_BREAK);
			}
		}
	}

	public String createLeagueLeaguePointRankCopyText(Long event, Long leagueId) {
		Optional<TblLeague> league = leagueRepository.findById(leagueId);

		if (!league.isPresent()) {
			return "";
		}

		List<TblLeagueGwStandings> leagueGwStandingsList = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdOrderByPlayerOverallStandingsOrderAsc(leagueId, event);

		StringBuilder sb = new StringBuilder();

		sb.append(league.get().getName());
		sb.append("⚽🦁");
		sb.append(LINE_BREAK);

		sb.append("*Gameweek ");
		sb.append(event);
		sb.append("*");
		sb.append(LINE_BREAK);

		sb.append("Klasemen Liga:");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		for (Integer index = 0; index < leagueGwStandingsList.size(); index++) {
			TblLeagueGwStandings data = leagueGwStandingsList.get(index);

			Optional<TblPlayer> player = playerRepository.findById(data.getPlayerEntryId());
			if (!player.isPresent()) {
				continue;
			}

			sb.append(data.getPlayerOverallStandingsOrder());
			sb.append(" ");
			sb.append(player.get().getPlayerNick());

			if (data.getPlayerOverallStandingsOrder().equals(1)) {
				sb.append(" 🏆");
			} else if (data.getPlayerOverallStandingsOrder().equals(2)) {
				sb.append(" 🥈");
			}

			sb.append(" ( ");
			sb.append(data.getPlayerEventScore());
			sb.append(" | *");
			sb.append(data.getPlayerTotalScore());
			sb.append("*");

			if (data.getPlayerActiveChip() != null) {
				sb.append(" | ");
				sb.append(data.getPlayerActiveChip());
			}

			sb.append(")");
			sb.append(LINE_BREAK);
		}

		return sb.toString();
	}

	public List<LeagueGwStandingsDataDownloadDto> getLeagueGwStandingsDownload(Long event, Long leagueid) {
		List<LeagueGwStandingsDataDownloadDto> result = new ArrayList<>();

		List<TblLeagueGwStandings> leagueGwStandingDataList = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdOrderByPlayerOverallStandingsOrderAsc(leagueid, event);

		if (leagueGwStandingDataList == null || leagueGwStandingDataList.isEmpty()) {
			return result;
		}

		for (TblLeagueGwStandings leagueGwStandingData : leagueGwStandingDataList) {
			result.add(mapLeagueGwStandingDataToLeagueGwStandingsDataDownloadDto(leagueGwStandingData));
		}

		return result;
	}

	private LeagueGwStandingsDataDownloadDto mapLeagueGwStandingDataToLeagueGwStandingsDataDownloadDto(
			TblLeagueGwStandings data) {
		if (data == null) {
			return null;
		}
		Optional<TblPlayer> player = playerRepository.findById(data.getPlayerEntryId());

		if (!player.isPresent()) {
			return null;
		}

		LeagueGwStandingsDataDownloadDto result = new LeagueGwStandingsDataDownloadDto();

		result.setEventId(data.getEventId());
		result.setLeagueGwStandingsId(data.getLeagueGwStandingsId());
		result.setLeagueId(data.getLeagueId());
		result.setPlayerActiveChip(data.getPlayerActiveChip());
		result.setPlayerBank(data.getPlayerBank());
		result.setPlayerEntryId(data.getPlayerEntryId());
		result.setPlayerEntryName(player.get().getEntryName());
		result.setPlayerEventPointsOnBench(data.getPlayerEventPointsOnBench());
		result.setPlayerEventScore(data.getPlayerEventScore());
		result.setPlayerEventTransfer(data.getPlayerEventTransfer());
		result.setPlayerEventTransferCost(data.getPlayerEventTransferCost());
		result.setPlayerGwBankValueStandingsOrder(data.getPlayerGwBankValueStandingsOrder());
		result.setPlayerGwBankValueStandingsPositionGain(data.getPlayerGwBankValueStandingsPositionGain());
		result.setPlayerGwBankValueStandingsRank(data.getPlayerGwBankValueStandingsRank());
		result.setPlayerGwBankValueStandingsRankPositionGain(data.getPlayerGwBankValueStandingsRankPositionGain());
		result.setPlayerGwBenchPointStandingsOrder(data.getPlayerGwBenchPointStandingsOrder());
		result.setPlayerGwBenchPointStandingsPositionGain(data.getPlayerGwBenchPointStandingsPositionGain());
		result.setPlayerGwBenchPointStandingsRank(data.getPlayerGwBenchPointStandingsRank());
		result.setPlayerGwBenchPointStandingsRankPositionGain(data.getPlayerGwBenchPointStandingsRankPositionGain());
		result.setPlayerGwStandingsOrder(data.getPlayerGwStandingsOrder());
		result.setPlayerGwStandingsPositionGain(data.getPlayerGwStandingsPositionGain());
		result.setPlayerGwStandingsRank(data.getPlayerGwStandingsRank());
		result.setPlayerGwStandingsRankPositionGain(data.getPlayerGwStandingsRankPositionGain());
		result.setPlayerGwTeamValueStandingsOrder(data.getPlayerGwTeamValueStandingsOrder());
		result.setPlayerGwTeamValueStandingsPositionGain(data.getPlayerGwTeamValueStandingsPositionGain());
		result.setPlayerGwTeamValueStandingsRank(data.getPlayerGwTeamValueStandingsRank());
		result.setPlayerGwTeamValueStandingsRankPositionGain(data.getPlayerGwTeamValueStandingsRankPositionGain());
		result.setPlayerGwTransferCostStandingsOrder(data.getPlayerGwTransferCostStandingsOrder());
		result.setPlayerGwTransferCostStandingsPositionGain(data.getPlayerGwTransferCostStandingsPositionGain());
		result.setPlayerGwTransferCostStandingsRank(data.getPlayerGwTransferCostStandingsRank());
		result.setPlayerGwTransferCostStandingsRankPositionGain(
				data.getPlayerGwTransferCostStandingsRankPositionGain());
		result.setPlayerGwTransferCountStandingsOrder(data.getPlayerGwTransferCountStandingsOrder());
		result.setPlayerGwTransferCountStandingsPositionGain(data.getPlayerGwTransferCountStandingsPositionGain());
		result.setPlayerGwTransferCountStandingsRank(data.getPlayerGwTransferCountStandingsRank());
		result.setPlayerGwTransferCountStandingsRankPositionGain(
				data.getPlayerGwTransferCountStandingsRankPositionGain());
		result.setPlayerIsGwBankValueStandingsLastPos(data.getPlayerIsGwBankValueStandingsLastPos());
		result.setPlayerIsGwBenchPointStandingsLastPos(data.getPlayerIsGwBenchPointStandingsLastPos());
		result.setPlayerIsGwStandingsLastPos(data.getPlayerIsGwStandingsLastPos());
		result.setPlayerIsGwTeamValueStandingsLastPos(data.getPlayerIsGwTeamValueStandingsLastPos());
		result.setPlayerIsGwTransferCostStandingsLastPos(data.getPlayerIsGwTransferCostStandingsLastPos());
		result.setPlayerIsGwTransferCountStandingsLastPos(data.getPlayerIsGwTransferCountStandingsLastPos());
		result.setPlayerName(player.get().getPlayerName());
		result.setPlayerNick(player.get().getPlayerNick());
		result.setPlayerOverallBenchPointStandingsOrder(data.getPlayerOverallBenchPointStandingsOrder());
		result.setPlayerOverallBenchPointStandingsPositionGain(data.getPlayerOverallBenchPointStandingsPositionGain());
		result.setPlayerOverallStandingsOrder(data.getPlayerOverallStandingsOrder());
		result.setPlayerOverallStandingsPositionGain(data.getPlayerOverallStandingsPositionGain());
		result.setPlayerOverallTransferCostStandingsOrder(data.getPlayerOverallTransferCostStandingsOrder());
		result.setPlayerOverallTransferCostStandingsPositionGain(
				data.getPlayerOverallTransferCostStandingsPositionGain());
		result.setPlayerOverallTransferCountStandingsOrder(data.getPlayerOverallTransferCountStandingsOrder());
		result.setPlayerOverallTransferCountStandingsPositionGain(
				data.getPlayerOverallTransferCountStandingsPositionGain());
		result.setPlayerPrevGwBankValueStandingsOrder(data.getPlayerPrevGwBankValueStandingsOrder());
		result.setPlayerPrevGwBankValueStandingsRank(data.getPlayerPrevGwBankValueStandingsRank());
		result.setPlayerPrevGwBenchPointStandingsOrder(data.getPlayerPrevGwBenchPointStandingsOrder());
		result.setPlayerPrevGwBenchPointStandingsRank(data.getPlayerPrevGwBenchPointStandingsRank());
		result.setPlayerPrevGwStandingsOrder(data.getPlayerPrevGwStandingsOrder());
		result.setPlayerPrevGwStandingsRank(data.getPlayerPrevGwStandingsRank());
		result.setPlayerPrevGwTeamValueStandingsOrder(data.getPlayerPrevGwTeamValueStandingsOrder());
		result.setPlayerPrevGwTeamValueStandingsRank(data.getPlayerPrevGwTeamValueStandingsRank());
		result.setPlayerPrevGwTransferCostStandingsOrder(data.getPlayerPrevGwTransferCostStandingsOrder());
		result.setPlayerPrevGwTransferCostStandingsRank(data.getPlayerPrevGwTransferCostStandingsRank());
		result.setPlayerPrevGwTransferCountStandingsOrder(data.getPlayerPrevGwTransferCountStandingsOrder());
		result.setPlayerPrevGwTransferCountStandingsRank(data.getPlayerPrevGwTransferCountStandingsRank());
		result.setPlayerPrevOverallBenchPointStandingsOrder(data.getPlayerPrevOverallBenchPointStandingsOrder());
		result.setPlayerPrevOverallStandingsOrder(data.getPlayerPrevOverallStandingsOrder());
		result.setPlayerPrevOverallTransferCostStandingsOrder(data.getPlayerPrevOverallTransferCostStandingsOrder());
		result.setPlayerPrevOverallTransferCountStandingsOrder(data.getPlayerPrevOverallTransferCountStandingsOrder());
		result.setPlayerTotalPointsOnBench(data.getPlayerTotalPointsOnBench());
		result.setPlayerTotalScore(data.getPlayerTotalScore());
		result.setPlayerTotalTransfer(data.getPlayerTotalTransfer());
		result.setPlayerTotalTransferCost(data.getPlayerTotalTransferCost());
		result.setPlayerValue(data.getPlayerValue());

		return result;
	}

	public List<LeagueGwStandingsDataDownloadDto> getAvailableLeagueGwStandingsDownload(Long leagueid) {
		List<LeagueGwStandingsDataDownloadDto> result = new ArrayList<>();

		List<TblLeagueGwStandings> leagueGwStandingDataList = leagueGwStandingsRepository
				.findByLeagueIdOrderByEventIdAsc(leagueid);

		if (leagueGwStandingDataList == null || leagueGwStandingDataList.isEmpty()) {
			return result;
		}

		for (TblLeagueGwStandings leagueGwStandingData : leagueGwStandingDataList) {
			result.add(mapLeagueGwStandingDataToLeagueGwStandingsDataDownloadDto(leagueGwStandingData));
		}

		return result;
	}

	public String createLeagueGwRecordCopyText(Long leagueid, Long event) {
		Optional<TblLeague> league = leagueRepository.findById(leagueid);
		
		if(!league.isPresent()) {
			return null;
		}
		
		StringBuilder sb = new StringBuilder();

		sb.append(league.get().getName());
		sb.append("⚽🦁");
		sb.append(LINE_BREAK);

		sb.append("Rekor per *GW ");
		sb.append(event);
		sb.append("*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Top Scorer GW Terbanyak:*");
		sb.append(LINE_BREAK);
		createTopScorerGwCountRankString(sb, event, leagueid);
		sb.append(LINE_BREAK);
		
		sb.append("*Lowest Score GW Terbanyak:*");
		sb.append(LINE_BREAK);
		createLowestScorerGwCountRankString(sb, event, leagueid);
		sb.append(LINE_BREAK);
		
		return sb.toString();
	}

	private void createLowestScorerGwCountRankString(StringBuilder sb, Long event, Long leagueid) {
		List<Map<String, Object>> topScorerGwCountList = leagueGwStandingsRepository.getLowestScoreGwCount(event, leagueid);
		
		Long maxCount = 0L;
		List<Map<String, Object>> topScorerList = new ArrayList<>();
		
		for (Map<String, Object> topScorerGwCount : topScorerGwCountList) {
			if(((Long) topScorerGwCount.get(DATA_COUNT_FIELD_NAME)).compareTo(maxCount) > 0) {
				maxCount = (Long) topScorerGwCount.get(DATA_COUNT_FIELD_NAME);
				topScorerList = new ArrayList<>();
				topScorerList.add(topScorerGwCount);
			} else if(((Long) topScorerGwCount.get(DATA_COUNT_FIELD_NAME)).compareTo(maxCount) == 0) {
				topScorerList.add(topScorerGwCount);
			}
		}
		
		Integer numbering = 0;
		
		for (Map<String, Object> topScorer : topScorerList) {
			Optional<TblPlayer> player = playerRepository.findById((Long) topScorer.get("playerEntryId"));
			if(!player.isPresent()) {
				continue;
			}
			
			numbering = numbering + 1;
			
			sb.append(numbering);
			sb.append(". ");
			sb.append(player.get().getPlayerNick());
			sb.append(" ( *");
			sb.append((Long) topScorer.get(DATA_COUNT_FIELD_NAME));
			sb.append("* kali) ");
			sb.append(LINE_BREAK);
			sb.append("di GW: ");
			List<Long> eventIdList = leagueGwStandingsRepository.getEventIdListByPlayerEntryIdAndLeagueIdAndMaxEventIdAndPlayerIsGwStandingsLastPos(player.get().getId(), leagueid, event, true);
			for (int idx = 0; idx < eventIdList.size(); idx++) {
				if(idx > 0) {
					sb.append(", ");
				}
				
				sb.append(eventIdList.get(idx));
			}
			sb.append(LINE_BREAK);
		}
	}

	private void createTopScorerGwCountRankString(StringBuilder sb, Long event, Long leagueid) {
		List<Map<String, Object>> topScorerGwCountList = leagueGwStandingsRepository.getTopScorerGwCount(event, leagueid);
		
		Long maxCount = 0L;
		List<Map<String, Object>> topScorerList = new ArrayList<>();
		
		for (Map<String, Object> topScorerGwCount : topScorerGwCountList) {
			if(((Long) topScorerGwCount.get(DATA_COUNT_FIELD_NAME)).compareTo(maxCount) > 0) {
				maxCount = (Long) topScorerGwCount.get(DATA_COUNT_FIELD_NAME);
				topScorerList = new ArrayList<>();
				topScorerList.add(topScorerGwCount);
			} else if(((Long) topScorerGwCount.get(DATA_COUNT_FIELD_NAME)).compareTo(maxCount) == 0) {
				topScorerList.add(topScorerGwCount);
			}
		}
		
		Integer numbering = 0;
		
		for (Map<String, Object> topScorer : topScorerList) {
			Optional<TblPlayer> player = playerRepository.findById((Long) topScorer.get("playerEntryId"));
			if(!player.isPresent()) {
				continue;
			}
			
			numbering = numbering + 1;
			
			sb.append(numbering);
			sb.append(". ");
			sb.append(player.get().getPlayerNick());
			sb.append(" ( *");
			sb.append((Long) topScorer.get(DATA_COUNT_FIELD_NAME));
			sb.append("* kali) ");
			sb.append(LINE_BREAK);
			sb.append("di GW: ");
			List<Long> eventIdList = leagueGwStandingsRepository.getEventIdListByPlayerEntryIdAndLeagueIdAndMaxEventIdAndPlayerGwStandingsRank(player.get().getId(), leagueid, event, 1);
			for (int idx = 0; idx < eventIdList.size(); idx++) {
				if(idx > 0) {
					sb.append(", ");
				}
				
				sb.append(eventIdList.get(idx));
			}
			sb.append(LINE_BREAK);
		}
	}

}
