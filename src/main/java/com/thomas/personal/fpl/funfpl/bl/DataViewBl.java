package com.thomas.personal.fpl.funfpl.bl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thomas.personal.fpl.funfpl.model.LeagueDto;
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
			leagueGwStandingData.setPlayerName(player.get().getPlayerName());
			leagueGwStandingData.setPlayerNick(player.get().getPlayerNick());
			leagueGwStandingData.setPlayerPositionGain(tblLeagueGwStandings.getPlayerGwStandingsPositionGain());
			leagueGwStandingData.setPlayerPrevStandingsOrder(tblLeagueGwStandings.getPlayerPrevGwStandingsOrder());
			leagueGwStandingData.setPlayerStandingsOrder(tblLeagueGwStandings.getPlayerGwStandingsOrder());
			leagueGwStandingData.setPlayerTotalScore(tblLeagueGwStandings.getPlayerTotalScore());

			leagueGwStandingData.setPlayerStandingsRank(tblLeagueGwStandings.getPlayerGwStandingsRank());
			leagueGwStandingData.setPlayerPrevStandingsRank(tblLeagueGwStandings.getPlayerPrevGwStandingsRank());
			leagueGwStandingData.setPlayerRankGain(tblLeagueGwStandings.getPlayerGwStandingsRankPositionGain());

			leagueGwStandingData.setPlayerIsLastGwStandingsRank(tblLeagueGwStandings.getPlayerIsGwStandingsLastPos());
			leagueGwStandingData.setActiveChip(tblLeagueGwStandings.getPlayerActiveChip());

			result.add(leagueGwStandingData);
		}

		return result;
	}

	public String createLeagueGwPointRankCopyText(List<LeagueGwStandingsDataDto> leagueStandingsData, Long event) {
//		String LINE_BREAK = "&#013;&#010;";

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

	public String createLeagueGwSummaryCopyText(List<LeagueGwStandingsDataDto> leagueStandingsData, Long event,
			Long leagueid) {
		String LINE_BREAK = "\r\n";
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
		createTopScoreGwString(sb, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*Lowest Score, GW:*");
		sb.append(LINE_BREAK);
		createLowestScoreGwString(sb, event, leagueid);
		sb.append(LINE_BREAK);

		sb.append("*League Leader, Season:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);
		
		sb.append("*Top 3 Most Position Gain, GW:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);
		
		sb.append("*Top 3 Most Position Gain, Season:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer, GW:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer, Season:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer Cost, GW:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Transfer Cost, Season:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Point on Bench, GW:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Point on Bench, Season:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Money on Bank, GW:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		sb.append("*Most Valuable Team, GW:*");
		sb.append(LINE_BREAK);
		sb.append(LINE_BREAK);

		return sb.toString();
	}

	private void createLowestScoreGwString(StringBuilder sb, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerIsGwStandingsLastPosOrderByPlayerGwStandingsOrderDesc(leagueid, event,
						true);

		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwStandingsOrder());
				sb.append(" ( *");
				sb.append(data.getPlayerGwStandingsRank());
				sb.append("* ) ");
				Optional<TblPlayer> player = playerRepository.findById(data.getPlayerEntryId());
				if (player.isPresent()) {
					sb.append(player.get().getPlayerNick());
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

	private void createTopScoreGwString(StringBuilder sb, Long event, Long leagueid) {
		List<TblLeagueGwStandings> standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwStandingsRankOrderByPlayerGwStandingsOrderAsc(leagueid, event, 1);
		createTopScoreGwString(sb, standings);
		standings = leagueGwStandingsRepository
				.findByLeagueIdAndEventIdAndPlayerGwStandingsRankOrderByPlayerGwStandingsOrderAsc(leagueid, event, 2);
		createTopScoreGwString(sb, standings);
	}

	private void createTopScoreGwString(StringBuilder sb, List<TblLeagueGwStandings> standings) {
		if (!standings.isEmpty()) {
			for (TblLeagueGwStandings data : standings) {
				sb.append(data.getPlayerGwStandingsRank());
				sb.append(". ");
				Optional<TblPlayer> player = playerRepository.findById(data.getPlayerEntryId());
				if (player.isPresent()) {
					sb.append(player.get().getPlayerNick());
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

}
