package com.thomas.personal.fpl.funfpl.bl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.thomas.personal.fpl.funfpl.model.ClassicLeagueStandingResponseDto;
import com.thomas.personal.fpl.funfpl.model.EventStatusDetailDto;
import com.thomas.personal.fpl.funfpl.model.EventStatusResponseDto;
import com.thomas.personal.fpl.funfpl.model.LeagueStandingResultDto;
import com.thomas.personal.fpl.funfpl.model.LeagueStandingSortingDto;
import com.thomas.personal.fpl.funfpl.model.PlayerEventHistoryDto;
import com.thomas.personal.fpl.funfpl.model.PlayerEventHistoryResponseDto;
import com.thomas.personal.fpl.funfpl.persistence.TblEvent;
import com.thomas.personal.fpl.funfpl.persistence.TblLeague;
import com.thomas.personal.fpl.funfpl.persistence.TblLeagueGwStandings;
import com.thomas.personal.fpl.funfpl.persistence.TblLeaguePlayer;
import com.thomas.personal.fpl.funfpl.persistence.TblPlayer;
import com.thomas.personal.fpl.funfpl.persistence.TblPlayerEvent;
import com.thomas.personal.fpl.funfpl.repository.EventRepository;
import com.thomas.personal.fpl.funfpl.repository.LeagueGwStandingsRepository;
import com.thomas.personal.fpl.funfpl.repository.LeaguePlayerRepository;
import com.thomas.personal.fpl.funfpl.repository.LeagueRepository;
import com.thomas.personal.fpl.funfpl.repository.PlayerEventRepository;
import com.thomas.personal.fpl.funfpl.repository.PlayerRepository;

@Component
public class DataBl {

	private static final String EVENT_STATUS_UNPROCESSED = "UNPROCESSED";

	private static final String HTTP_HEADER_COOKIE = "Cookie";

	private String cookieValue;

	private PlayerRepository playerRepository;

	private LeagueRepository leagueRepository;

	private LeaguePlayerRepository leaguePlayerRepository;

	private PlayerEventRepository playerEventRepository;

	private EventRepository eventRepository;

	private LeagueGwStandingsRepository leagueGwStandingsRepository;

	@Autowired
	public DataBl(PlayerRepository playerRepository, LeagueRepository leagueRepository,
			LeaguePlayerRepository leaguePlayerRepository, PlayerEventRepository playerEventRepository,
			EventRepository eventRepository, LeagueGwStandingsRepository leagueGwStandingsRepository,
			@Value("${funfpl.site.cookievalue}") String cookieValue) {
		this.playerRepository = playerRepository;
		this.leagueRepository = leagueRepository;
		this.leaguePlayerRepository = leaguePlayerRepository;
		this.playerEventRepository = playerEventRepository;
		this.eventRepository = eventRepository;
		this.leagueGwStandingsRepository = leagueGwStandingsRepository;
		this.cookieValue = cookieValue;
	}

	public void updateData() {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HTTP_HEADER_COOKIE, cookieValue);

		String restUrl = "https://fantasy.premierleague.com/api/event-status/";

		ResponseEntity<EventStatusResponseDto> response = null;

		try {
			response = restTemplate.exchange(restUrl, HttpMethod.GET, new HttpEntity<String>(headers),
					EventStatusResponseDto.class);
		} catch (Exception e) {
			return;
		}

		Boolean isEventFinish = true;
		Long lastFinishedEvent = null;

		for (EventStatusDetailDto eventStatusDetail : response.getBody().getStatus()) {
			if (!eventStatusDetail.getBonusAdded() || !"r".equalsIgnoreCase(eventStatusDetail.getPoints())) {
				isEventFinish = false;
				continue;
			}

			lastFinishedEvent = eventStatusDetail.getEvent();
		}

		if (!isEventFinish && lastFinishedEvent != null)
		{
			lastFinishedEvent = lastFinishedEvent - 1;
		}
		
		if (!isEventFinish && lastFinishedEvent == null) {
			return;
		}

		for (Long event = 1L; event <= lastFinishedEvent; event++) {
			Optional<TblEvent> eventTable = eventRepository.findById(event);

			if (!eventTable.isPresent()) {
				TblEvent eventTableData = new TblEvent();
				eventTableData.setEvent(event);
				eventTableData.setCode("GW" + event);
				eventTableData.setStatus(EVENT_STATUS_UNPROCESSED);

				eventRepository.save(eventTableData);
			}
		}

		List<Long> leagueCodeList = new ArrayList<>();
		leagueCodeList.add(965308L);
		leagueCodeList.add(251905L);

		for (Long leagueCode : leagueCodeList) {
			Optional<TblLeague> leagueTable = leagueRepository.findById(leagueCode);

			if (!leagueTable.isPresent()) {
				TblLeague leagueTableData = new TblLeague();
				leagueTableData.setId(leagueCode);
				leagueTableData.setName(null);
				leagueRepository.save(leagueTableData);
			}
		}

		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			restTemplate = new RestTemplate();
			headers = new HttpHeaders();
			headers.add(HTTP_HEADER_COOKIE, cookieValue);

			restUrl = String.format("https://fantasy.premierleague.com/api/leagues-classic/%s/standings/",
					tblLeague.getId());

			ResponseEntity<ClassicLeagueStandingResponseDto> leagueResponse = restTemplate.exchange(restUrl,
					HttpMethod.GET, new HttpEntity<String>(headers), ClassicLeagueStandingResponseDto.class);

			saveLeagueData(leagueResponse.getBody());
		}

		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);

		for (TblEvent tblEvent : unprocessedEventList) {
			tblEvent.setStatus("FINISH");
			eventRepository.save(tblEvent);
		}
	}

	private void saveLeagueData(ClassicLeagueStandingResponseDto response) {
		if (response == null || response.getStandings() == null || response.getStandings().getResults() == null
				|| response.getStandings().getResults().isEmpty()) {
			return;
		}

		Optional<TblLeague> league = leagueRepository.findById(response.getLeague().getId());
		TblLeague leagueData;

		if (!league.isPresent()) {
			TblLeague tblLeague = new TblLeague();
			tblLeague.setId(response.getLeague().getId());
			tblLeague.setName(response.getLeague().getName());

			leagueData = leagueRepository.save(tblLeague);
		} else {
			leagueData = league.get();

			if (leagueData.getName() == null) {
				leagueData.setName(response.getLeague().getName());
				leagueRepository.save(leagueData);
			}
		}

		for (LeagueStandingResultDto result : response.getStandings().getResults()) {
			Optional<TblPlayer> player = playerRepository.findById(result.getEntry());
			TblPlayer playerData;

			if (!player.isPresent()) {
				TblPlayer tblPlayer = new TblPlayer();
				tblPlayer.setId(result.getEntry());
				String playerName = result.getPlayerName();
				playerName = WordUtils.capitalizeFully(playerName);
				tblPlayer.setPlayerName(playerName);
				tblPlayer.setEntryName(result.getEntryName());

				String playerNickName = tblPlayer.getPlayerName();

				if (playerNickName.indexOf(' ') != -1) {
					playerNickName = playerNickName.substring(0, playerNickName.indexOf(' '));
				}

				if ("Daniel Talenta".equalsIgnoreCase(playerName)) {
					playerNickName = "Dante";
				} else if (playerName.contains("Dioalip")) {
					playerNickName = "Dio";
				} else if (playerName.contains("Albertus Reinaldi")) {
					playerNickName = "Rei";
				} else if (playerName.contains("Firdaus Dwi Avianto")) {
					playerNickName = "Anto";
				} else if (playerName.contains("Yoseph Andhi Wicaksono")) {
					playerNickName = "Andhi";
				} else if (playerName.contains("Yeremia Valentino")) {
					playerNickName = "Valen";
				} else if (playerName.contains("Samuel Eko Yulianto")) {
					playerNickName = "Samuel Eko";
				} else if (playerName.contains("Lucas Ega Krisetya")) {
					playerNickName = "Ega";
				} else if (playerName.contains("Nugraha Chandra")) {
					playerNickName = "Chandra";
				}

				tblPlayer.setPlayerNick(playerNickName);

				playerData = playerRepository.save(tblPlayer);
			} else {
				playerData = player.get();
			}

			Optional<TblLeaguePlayer> leaguePlayer = leaguePlayerRepository
					.findByPlayerEntryIdAndLeagueId(playerData.getId(), leagueData.getId());

			if (!leaguePlayer.isPresent()) {
				TblLeaguePlayer tblLeaguePlayer = new TblLeaguePlayer();
				tblLeaguePlayer.setLeagueId(leagueData.getId());
				tblLeaguePlayer.setPlayerEntryId(playerData.getId());
				leaguePlayerRepository.save(tblLeaguePlayer);
			}
		}

		savePlayerEventData();

		saveLeagueStandingsData();
	}

	private void saveLeagueStandingsData() {
		saveLeagueOverallStandingsData();
		saveLeagueGwStandingsData();

		saveLeagueOverallBenchPointStandingsData();
		saveLeagueGwBenchPointStandingsData();

		saveLeagueOverallTransferCostStandingsData();
		saveLeagueGwTransferCostStandingsData();

		saveLeagueOverallTransferCountStandingsData();
		saveLeagueGwTransferCountStandingsData();

		saveLeagueGwBankValueStandingsData();

		saveLeagueGwTeamValueStandingsData();
	}

	private void saveLeagueGwTeamValueStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueGwTeamValueStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueGwTeamValueStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueGwTeamValueStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueGwTeamValueStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerGwTeamValueStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new GwTeamValueStandingsSorter());

		Integer gwStandingsRank = 1;
		Long prevEventScore = null;
		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerGwTeamValueStandingsOrder(index + 1);

			if (index.equals(0)) {
				leagueStandingsData.setPlayerGwTeamValueStandingsRank(gwStandingsRank);
				prevEventScore = leagueSortingData.getPlayerEvent().getValue();
			} else {
				if (prevEventScore.equals(leagueSortingData.getPlayerEvent().getValue())) {
					leagueStandingsData.setPlayerGwTeamValueStandingsRank(gwStandingsRank);
				} else {
					gwStandingsRank = gwStandingsRank + 1;
					leagueStandingsData.setPlayerGwTeamValueStandingsRank(gwStandingsRank);
					prevEventScore = leagueSortingData.getPlayerEvent().getValue();
				}
			}

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevGwTeamValueStandingsOrder(0);
				leagueStandingsData.setPlayerGwTeamValueStandingsPositionGain(0);
				leagueStandingsData.setPlayerPrevGwTeamValueStandingsRank(0);
				leagueStandingsData.setPlayerGwTeamValueStandingsRankPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevGwTeamValueStandingsOrder(
						prevLeagueGwStandingData.getPlayerGwTeamValueStandingsOrder());
				leagueStandingsData.setPlayerGwTeamValueStandingsPositionGain(
						leagueStandingsData.getPlayerPrevGwTeamValueStandingsOrder()
								- leagueStandingsData.getPlayerGwTeamValueStandingsOrder());
				leagueStandingsData.setPlayerPrevGwTeamValueStandingsRank(
						prevLeagueGwStandingData.getPlayerGwTeamValueStandingsRank());
				leagueStandingsData.setPlayerGwTeamValueStandingsRankPositionGain(
						prevLeagueGwStandingData.getPlayerGwTeamValueStandingsRank()
								- leagueStandingsData.getPlayerGwTeamValueStandingsRank());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		Long minEventScore = null;
		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				if (minEventScore == null) {
					minEventScore = leagueStandingsData.getPlayerValue();
					leagueStandingsData.setPlayerIsGwTeamValueStandingsLastPos(true);
					leagueGwStandingsRepository.save(leagueStandingsData);
				} else {
					if (leagueStandingsData.getPlayerValue().equals(minEventScore)) {
						leagueStandingsData.setPlayerIsGwTeamValueStandingsLastPos(true);
						leagueGwStandingsRepository.save(leagueStandingsData);
					} else {
						break;
					}
				}
			}
		}
	}

	private void saveLeagueGwBankValueStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueGwBankValueStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueGwBankValueStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueGwBankValueStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueGwBankValueStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerGwBankValueStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new GwBankValueStandingsSorter());

		Integer gwStandingsRank = 1;
		Long prevEventScore = null;
		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerGwBankValueStandingsOrder(index + 1);

			if (index.equals(0)) {
				leagueStandingsData.setPlayerGwBankValueStandingsRank(gwStandingsRank);
				prevEventScore = leagueSortingData.getPlayerEvent().getBank();
			} else {
				if (prevEventScore.equals(leagueSortingData.getPlayerEvent().getBank())) {
					leagueStandingsData.setPlayerGwBankValueStandingsRank(gwStandingsRank);
				} else {
					gwStandingsRank = gwStandingsRank + 1;
					leagueStandingsData.setPlayerGwBankValueStandingsRank(gwStandingsRank);
					prevEventScore = leagueSortingData.getPlayerEvent().getBank();
				}
			}

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevGwBankValueStandingsOrder(0);
				leagueStandingsData.setPlayerGwBankValueStandingsPositionGain(0);
				leagueStandingsData.setPlayerPrevGwBankValueStandingsRank(0);
				leagueStandingsData.setPlayerGwBankValueStandingsRankPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevGwBankValueStandingsOrder(
						prevLeagueGwStandingData.getPlayerGwBankValueStandingsOrder());
				leagueStandingsData.setPlayerGwBankValueStandingsPositionGain(
						leagueStandingsData.getPlayerPrevGwBankValueStandingsOrder()
								- leagueStandingsData.getPlayerGwBankValueStandingsOrder());
				leagueStandingsData.setPlayerPrevGwBankValueStandingsRank(
						prevLeagueGwStandingData.getPlayerGwBankValueStandingsRank());
				leagueStandingsData.setPlayerGwBankValueStandingsRankPositionGain(
						prevLeagueGwStandingData.getPlayerGwBankValueStandingsRank()
								- leagueStandingsData.getPlayerGwBankValueStandingsRank());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		Long minEventScore = null;
		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				if (minEventScore == null) {
					minEventScore = leagueStandingsData.getPlayerBank();
					leagueStandingsData.setPlayerIsGwBankValueStandingsLastPos(true);
					leagueGwStandingsRepository.save(leagueStandingsData);
				} else {
					if (leagueStandingsData.getPlayerBank().equals(minEventScore)) {
						leagueStandingsData.setPlayerIsGwBankValueStandingsLastPos(true);
						leagueGwStandingsRepository.save(leagueStandingsData);
					} else {
						break;
					}
				}
			}
		}
	}

	private void saveLeagueGwTransferCountStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueGwTransferCountStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueGwTransferCountStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueGwTransferCountStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueGwTransferCountStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerGwTransferCountStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new GwTransferCountStandingsSorter());

		Integer gwStandingsRank = 1;
		Long prevEventScore = null;
		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerGwTransferCountStandingsOrder(index + 1);

			if (index.equals(0)) {
				leagueStandingsData.setPlayerGwTransferCountStandingsRank(gwStandingsRank);
				prevEventScore = leagueSortingData.getPlayerEvent().getTransfer();
			} else {
				if (prevEventScore.equals(leagueSortingData.getPlayerEvent().getTransfer())) {
					leagueStandingsData.setPlayerGwTransferCountStandingsRank(gwStandingsRank);
				} else {
					gwStandingsRank = gwStandingsRank + 1;
					leagueStandingsData.setPlayerGwTransferCountStandingsRank(gwStandingsRank);
					prevEventScore = leagueSortingData.getPlayerEvent().getTransfer();
				}
			}

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevGwTransferCountStandingsOrder(0);
				leagueStandingsData.setPlayerGwTransferCountStandingsPositionGain(0);
				leagueStandingsData.setPlayerPrevGwTransferCountStandingsRank(0);
				leagueStandingsData.setPlayerGwTransferCountStandingsRankPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevGwTransferCountStandingsOrder(
						prevLeagueGwStandingData.getPlayerGwTransferCountStandingsOrder());
				leagueStandingsData.setPlayerGwTransferCountStandingsPositionGain(
						leagueStandingsData.getPlayerPrevGwTransferCountStandingsOrder()
								- leagueStandingsData.getPlayerGwTransferCountStandingsOrder());
				leagueStandingsData.setPlayerPrevGwTransferCountStandingsRank(
						prevLeagueGwStandingData.getPlayerGwTransferCountStandingsRank());
				leagueStandingsData.setPlayerGwTransferCountStandingsRankPositionGain(
						prevLeagueGwStandingData.getPlayerGwTransferCountStandingsRank()
								- leagueStandingsData.getPlayerGwTransferCountStandingsRank());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		Long minEventScore = null;
		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				if (minEventScore == null) {
					minEventScore = leagueStandingsData.getPlayerEventTransfer();
					leagueStandingsData.setPlayerIsGwTransferCountStandingsLastPos(true);
					leagueGwStandingsRepository.save(leagueStandingsData);
				} else {
					if (leagueStandingsData.getPlayerEventTransfer().equals(minEventScore)) {
						leagueStandingsData.setPlayerIsGwTransferCountStandingsLastPos(true);
						leagueGwStandingsRepository.save(leagueStandingsData);
					} else {
						break;
					}
				}
			}
		}
	}

	private void saveLeagueOverallTransferCountStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueOverallTransferCountStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueOverallTransferCountStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueOverallTransferCountStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueOverallTransferCountStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerOverallTransferCountStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new OverallTransferCountStandingsSorter());

		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerOverallTransferCountStandingsOrder(index + 1);

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevOverallTransferCountStandingsOrder(0);
				leagueStandingsData.setPlayerOverallTransferCountStandingsPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevOverallTransferCountStandingsOrder(
						prevLeagueGwStandingData.getPlayerOverallTransferCountStandingsOrder());
				leagueStandingsData.setPlayerOverallTransferCountStandingsPositionGain(
						leagueStandingsData.getPlayerPrevOverallTransferCountStandingsOrder()
								- leagueStandingsData.getPlayerOverallTransferCountStandingsOrder());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				leagueStandingsData.setPlayerIsOverallTransferCountStandingsLastPos(true);
				leagueGwStandingsRepository.save(leagueStandingsData);

				break;
			}
		}
	}

	private void saveLeagueGwTransferCostStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueGwTransferCostStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueGwTransferCostStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueGwTransferCostStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueGwTransferCostStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerGwTransferCostStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new GwTransferCostStandingsSorter());

		Integer gwStandingsRank = 1;
		Long prevEventScore = null;
		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerGwTransferCostStandingsOrder(index + 1);

			if (index.equals(0)) {
				leagueStandingsData.setPlayerGwTransferCostStandingsRank(gwStandingsRank);
				prevEventScore = leagueSortingData.getPlayerEvent().getTransferCost();
			} else {
				if (prevEventScore.equals(leagueSortingData.getPlayerEvent().getTransferCost())) {
					leagueStandingsData.setPlayerGwTransferCostStandingsRank(gwStandingsRank);
				} else {
					gwStandingsRank = gwStandingsRank + 1;
					leagueStandingsData.setPlayerGwTransferCostStandingsRank(gwStandingsRank);
					prevEventScore = leagueSortingData.getPlayerEvent().getTransferCost();
				}
			}

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevGwTransferCostStandingsOrder(0);
				leagueStandingsData.setPlayerGwTransferCostStandingsPositionGain(0);
				leagueStandingsData.setPlayerPrevGwTransferCostStandingsRank(0);
				leagueStandingsData.setPlayerGwTransferCostStandingsRankPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevGwTransferCostStandingsOrder(
						prevLeagueGwStandingData.getPlayerGwTransferCostStandingsOrder());
				leagueStandingsData.setPlayerGwTransferCostStandingsPositionGain(
						leagueStandingsData.getPlayerPrevGwTransferCostStandingsOrder()
								- leagueStandingsData.getPlayerGwTransferCostStandingsOrder());
				leagueStandingsData.setPlayerPrevGwTransferCostStandingsRank(
						prevLeagueGwStandingData.getPlayerGwTransferCostStandingsRank());
				leagueStandingsData.setPlayerGwTransferCostStandingsRankPositionGain(
						prevLeagueGwStandingData.getPlayerGwTransferCostStandingsRank()
								- leagueStandingsData.getPlayerGwTransferCostStandingsRank());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		Long minEventScore = null;
		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				if (minEventScore == null) {
					minEventScore = leagueStandingsData.getPlayerEventTransferCost();
					leagueStandingsData.setPlayerIsGwTransferCostStandingsLastPos(true);
					leagueGwStandingsRepository.save(leagueStandingsData);
				} else {
					if (leagueStandingsData.getPlayerEventTransferCost().equals(minEventScore)) {
						leagueStandingsData.setPlayerIsGwTransferCostStandingsLastPos(true);
						leagueGwStandingsRepository.save(leagueStandingsData);
					} else {
						break;
					}
				}
			}
		}
	}

	private void saveLeagueOverallTransferCostStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueOverallTransferCostStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueOverallTransferCostStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueOverallTransferCostStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueOverallTransferCostStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerOverallTransferCostStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new OverallTransferCostStandingsSorter());

		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerOverallTransferCostStandingsOrder(index + 1);

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevOverallTransferCostStandingsOrder(0);
				leagueStandingsData.setPlayerOverallTransferCostStandingsPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevOverallTransferCostStandingsOrder(
						prevLeagueGwStandingData.getPlayerOverallTransferCostStandingsOrder());
				leagueStandingsData.setPlayerOverallTransferCostStandingsPositionGain(
						leagueStandingsData.getPlayerPrevOverallTransferCostStandingsOrder()
								- leagueStandingsData.getPlayerOverallTransferCostStandingsOrder());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				leagueStandingsData.setPlayerIsOverallTransferCostStandingsLastPos(true);
				leagueGwStandingsRepository.save(leagueStandingsData);

				break;
			}
		}

	}

	private void saveLeagueGwBenchPointStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueGwBenchPointStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueGwBenchPointStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueGwBenchPointStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueGwBenchPointStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerGwBenchPointStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new GwBenchPointStandingsSorter());

		Integer gwStandingsRank = 1;
		Long prevEventScore = null;
		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerGwBenchPointStandingsOrder(index + 1);

			if (index.equals(0)) {
				leagueStandingsData.setPlayerGwBenchPointStandingsRank(gwStandingsRank);
				prevEventScore = leagueSortingData.getPlayerEvent().getPointsOnBench();
			} else {
				if (prevEventScore.equals(leagueSortingData.getPlayerEvent().getPointsOnBench())) {
					leagueStandingsData.setPlayerGwBenchPointStandingsRank(gwStandingsRank);
				} else {
					gwStandingsRank = gwStandingsRank + 1;
					leagueStandingsData.setPlayerGwBenchPointStandingsRank(gwStandingsRank);
					prevEventScore = leagueSortingData.getPlayerEvent().getPointsOnBench();
				}
			}

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevGwBenchPointStandingsOrder(0);
				leagueStandingsData.setPlayerGwBenchPointStandingsPositionGain(0);
				leagueStandingsData.setPlayerPrevGwBenchPointStandingsRank(0);
				leagueStandingsData.setPlayerGwBenchPointStandingsRankPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevGwBenchPointStandingsOrder(
						prevLeagueGwStandingData.getPlayerGwBenchPointStandingsOrder());
				leagueStandingsData.setPlayerGwBenchPointStandingsPositionGain(
						leagueStandingsData.getPlayerPrevGwBenchPointStandingsOrder()
								- leagueStandingsData.getPlayerGwBenchPointStandingsOrder());
				leagueStandingsData.setPlayerPrevGwBenchPointStandingsRank(
						prevLeagueGwStandingData.getPlayerGwBenchPointStandingsRank());
				leagueStandingsData.setPlayerGwBenchPointStandingsRankPositionGain(
						prevLeagueGwStandingData.getPlayerGwBenchPointStandingsRank()
								- leagueStandingsData.getPlayerGwBenchPointStandingsRank());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		Long minEventScore = null;
		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				if (minEventScore == null) {
					minEventScore = leagueStandingsData.getPlayerEventPointsOnBench();
					leagueStandingsData.setPlayerIsGwBenchPointStandingsLastPos(true);
					leagueGwStandingsRepository.save(leagueStandingsData);
				} else {
					if (leagueStandingsData.getPlayerEventPointsOnBench().equals(minEventScore)) {
						leagueStandingsData.setPlayerIsGwBenchPointStandingsLastPos(true);
						leagueGwStandingsRepository.save(leagueStandingsData);
					} else {
						break;
					}
				}
			}
		}
	}

	private void saveLeagueOverallBenchPointStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueOverallBenchPointStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueOverallBenchPointStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueOverallBenchPointStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueOverallBenchPointStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()
				&& existingLeagueStandings.get().getPlayerOverallBenchPointStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new OverallBenchPointStandingsSorter());

		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerOverallBenchPointStandingsOrder(index + 1);

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevOverallBenchPointStandingsOrder(0);
				leagueStandingsData.setPlayerOverallBenchPointStandingsPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevOverallBenchPointStandingsOrder(
						prevLeagueGwStandingData.getPlayerOverallBenchPointStandingsOrder());
				leagueStandingsData.setPlayerOverallBenchPointStandingsPositionGain(
						leagueStandingsData.getPlayerPrevOverallBenchPointStandingsOrder()
								- leagueStandingsData.getPlayerOverallBenchPointStandingsOrder());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				leagueStandingsData.setPlayerIsOverallBenchPointStandingsLastPos(true);
				leagueGwStandingsRepository.save(leagueStandingsData);

				break;
			}
		}
	}

	private void saveLeagueOverallStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueOverallStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueOverallStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueOverallStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueOverallStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent()) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new OverallStandingsSorter());

		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = new TblLeagueGwStandings();
			leagueStandingsData.setEventId(event);
			leagueStandingsData.setLeagueId(leagueId);
			leagueStandingsData.setPlayerEntryId(leagueSortingData.getPlayer().getId());
			leagueStandingsData.setPlayerEventScore(leagueSortingData.getPlayerEvent().getPlayerEventScore());
			leagueStandingsData.setPlayerOverallStandingsOrder(index + 1);
			leagueStandingsData.setPlayerTotalScore(leagueSortingData.getPlayerEvent().getPlayerTotalScore());
			leagueStandingsData.setPlayerActiveChip(leagueSortingData.getPlayerEvent().getActiveChip());
			leagueStandingsData.setPlayerBank(leagueSortingData.getPlayerEvent().getBank());
			leagueStandingsData.setPlayerValue(leagueSortingData.getPlayerEvent().getValue());
			leagueStandingsData.setPlayerEventTransfer(leagueSortingData.getPlayerEvent().getTransfer());
			leagueStandingsData.setPlayerTotalTransfer(leagueSortingData.getPlayerEvent().getTotalTransfer());
			leagueStandingsData.setPlayerEventTransferCost(leagueSortingData.getPlayerEvent().getTransferCost());
			leagueStandingsData.setPlayerTotalTransferCost(leagueSortingData.getPlayerEvent().getTotalTransferCost());
			leagueStandingsData.setPlayerEventPointsOnBench(leagueSortingData.getPlayerEvent().getPointsOnBench());
			leagueStandingsData.setPlayerTotalPointsOnBench(leagueSortingData.getPlayerEvent().getTotalPointsOnBench());

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevOverallStandingsOrder(0);
				leagueStandingsData.setPlayerOverallStandingsPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData
						.setPlayerPrevOverallStandingsOrder(prevLeagueGwStandingData.getPlayerOverallStandingsOrder());
				leagueStandingsData
						.setPlayerOverallStandingsPositionGain(leagueStandingsData.getPlayerPrevOverallStandingsOrder()
								- leagueStandingsData.getPlayerOverallStandingsOrder());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				leagueStandingsData.setPlayerIsOverallStandingsLastPos(true);
				leagueGwStandingsRepository.save(leagueStandingsData);

				break;
			}
		}
	}

	private void saveLeagueGwStandingsData() {
		List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);
		for (TblEvent unprocessedEvent : unprocessedEventList) {
			saveLeagueGwStandingsData(unprocessedEvent.getEvent());
		}
	}

	private void saveLeagueGwStandingsData(Long event) {
		Iterable<TblLeague> leagueList = leagueRepository.findAll();

		for (TblLeague tblLeague : leagueList) {
			saveLeagueGwStandingsData(event, tblLeague.getId());
		}
	}

	private void saveLeagueGwStandingsData(Long event, Long leagueId) {
		Optional<TblLeagueGwStandings> existingLeagueStandings = leagueGwStandingsRepository
				.findFirstByEventIdAndLeagueIdOrderByLeagueIdAsc(event, leagueId);

		if (existingLeagueStandings.isPresent() && existingLeagueStandings.get().getPlayerGwStandingsOrder() != null) {
			return;
		}

		List<TblLeaguePlayer> leaguePlayerList = leaguePlayerRepository.findByLeagueId(leagueId);
		List<LeagueStandingSortingDto> leagueSortingDataList = new ArrayList<>();

		for (TblLeaguePlayer leaguePlayer : leaguePlayerList) {
			TblPlayerEvent playerEventData = playerEventRepository.findByEventIdAndPlayerEntryId(event,
					leaguePlayer.getPlayerEntryId());

			Optional<TblPlayer> player = playerRepository.findById(playerEventData.getPlayerEntryId());

			if (!player.isPresent()) {
				return;
			}

			LeagueStandingSortingDto leagueSortingData = new LeagueStandingSortingDto();
			leagueSortingData.setPlayer(player.get());
			leagueSortingData.setPlayerEvent(playerEventData);
			leagueSortingDataList.add(leagueSortingData);
		}

		Collections.sort(leagueSortingDataList, new GwStandingsSorter());

		Integer gwStandingsRank = 1;
		Long prevEventScore = null;
		for (Integer index = 0; index < leagueSortingDataList.size(); index++) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);
			TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
					.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event, leagueSortingData.getPlayer().getId());

			leagueStandingsData.setPlayerGwStandingsOrder(index + 1);

			if (index.equals(0)) {
				leagueStandingsData.setPlayerGwStandingsRank(gwStandingsRank);
				prevEventScore = leagueSortingData.getPlayerEvent().getPlayerEventScore();
			} else {
				if (prevEventScore.equals(leagueSortingData.getPlayerEvent().getPlayerEventScore())) {
					leagueStandingsData.setPlayerGwStandingsRank(gwStandingsRank);
				} else {
					gwStandingsRank = gwStandingsRank + 1;
					leagueStandingsData.setPlayerGwStandingsRank(gwStandingsRank);
					prevEventScore = leagueSortingData.getPlayerEvent().getPlayerEventScore();
				}
			}

			if (event.equals(1L)) {
				leagueStandingsData.setPlayerPrevGwStandingsOrder(0);
				leagueStandingsData.setPlayerGwStandingsPositionGain(0);
				leagueStandingsData.setPlayerPrevGwStandingsRank(0);
				leagueStandingsData.setPlayerGwStandingsRankPositionGain(0);
			} else {
				TblLeagueGwStandings prevLeagueGwStandingData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event - 1L,
								leagueStandingsData.getPlayerEntryId());

				leagueStandingsData.setPlayerPrevGwStandingsOrder(prevLeagueGwStandingData.getPlayerGwStandingsOrder());
				leagueStandingsData.setPlayerGwStandingsPositionGain(leagueStandingsData.getPlayerPrevGwStandingsOrder()
						- leagueStandingsData.getPlayerGwStandingsOrder());
				leagueStandingsData.setPlayerPrevGwStandingsRank(prevLeagueGwStandingData.getPlayerGwStandingsRank());
				leagueStandingsData
						.setPlayerGwStandingsRankPositionGain(prevLeagueGwStandingData.getPlayerGwStandingsRank()
								- leagueStandingsData.getPlayerGwStandingsRank());
			}

			leagueGwStandingsRepository.save(leagueStandingsData);
		}

		Long minEventScore = null;
		for (Integer index = leagueSortingDataList.size() - 1; index >= 0; index--) {
			LeagueStandingSortingDto leagueSortingData = leagueSortingDataList.get(index);

			if (!(leagueSortingData.getPlayerEvent().getBank().equals(1000L)
					&& leagueSortingData.getPlayerEvent().getPlayerTotalScore().equals(0L))) {
				TblLeagueGwStandings leagueStandingsData = leagueGwStandingsRepository
						.findByLeagueIdAndEventIdAndPlayerEntryId(leagueId, event,
								leagueSortingData.getPlayer().getId());

				if (minEventScore == null) {
					minEventScore = leagueStandingsData.getPlayerEventScore();
					leagueStandingsData.setPlayerIsGwStandingsLastPos(true);
					leagueGwStandingsRepository.save(leagueStandingsData);
				} else {
					if (leagueStandingsData.getPlayerEventScore().equals(minEventScore)) {
						leagueStandingsData.setPlayerIsGwStandingsLastPos(true);
						leagueGwStandingsRepository.save(leagueStandingsData);
					} else {
						break;
					}
				}
			}
		}
	}

	private void savePlayerEventData() {
		Iterable<TblPlayer> allPlayerList = playerRepository.findAll();

		for (TblPlayer playerData : allPlayerList) {

			List<TblEvent> unprocessedEventList = eventRepository.findByStatusOrderByEventAsc(EVENT_STATUS_UNPROCESSED);

			for (TblEvent unprocessedEvent : unprocessedEventList) {
				Optional<TblPlayerEvent> playerEvent = playerEventRepository
						.findByPlayerEntryIdAndEventId(playerData.getId(), unprocessedEvent.getEvent());

				if (!playerEvent.isPresent()) {
					getPlayerEventData(playerData, unprocessedEvent.getEvent());
				}
			}
		}
	}

	private void getPlayerEventData(TblPlayer playerData, Long currentEventId) {
		RestTemplate restTemplate = new RestTemplate();
		HttpHeaders headers = new HttpHeaders();
		headers.add(HTTP_HEADER_COOKIE, cookieValue);

		String restUrl = String.format("https://fantasy.premierleague.com/api/entry/%s/event/%s/picks/",
				playerData.getId(), currentEventId);

		ResponseEntity<PlayerEventHistoryResponseDto> response = null;

		try {
			response = restTemplate.exchange(restUrl, HttpMethod.GET, new HttpEntity<String>(headers),
					PlayerEventHistoryResponseDto.class);
		} catch (HttpClientErrorException.NotFound e) {
			createEmptyPlayerEventData(playerData, currentEventId);
			return;
		}

		if (response == null || response.getBody().getEntryHistory() == null) {
			return;
		}
		PlayerEventHistoryResponseDto data = response.getBody();
		PlayerEventHistoryDto history = data.getEntryHistory();

		TblPlayerEvent playerEventData = new TblPlayerEvent();
		playerEventData.setActiveChip(data.getActiveChip());
		playerEventData.setBank(history.getBank());
		playerEventData.setEventId(currentEventId);
		playerEventData.setPlayerEntryId(playerData.getId());
		playerEventData.setPlayerEventScore(history.getPoints());
		playerEventData.setPlayerTotalScore(history.getTotalPoints());
		playerEventData.setPointsOnBench(history.getPointsOnBench());
		playerEventData.setTransfer(history.getEventTransfers());
		playerEventData.setTransferCost(history.getEventTransfersCost());
		playerEventData.setValue(history.getValue());

		if (currentEventId.equals(1L)) {
			playerEventData.setTotalPointsOnBench(playerEventData.getPointsOnBench());
			playerEventData.setTotalTransfer(playerEventData.getTransfer());
			playerEventData.setTotalTransferCost(playerEventData.getTransferCost());
		} else {
			Optional<TblPlayerEvent> prevPlayerEvent = playerEventRepository
					.findByPlayerEntryIdAndEventId(playerData.getId(), currentEventId - 1L);

			if (!prevPlayerEvent.isPresent()) {
				playerEventData.setTotalPointsOnBench(playerEventData.getPointsOnBench());
				playerEventData.setTotalTransfer(playerEventData.getTransfer());
				playerEventData.setTotalTransferCost(playerEventData.getTransferCost());
			} else {
				playerEventData.setTotalPointsOnBench(
						prevPlayerEvent.get().getTotalPointsOnBench() + playerEventData.getPointsOnBench());
				playerEventData
						.setTotalTransfer(prevPlayerEvent.get().getTotalTransfer() + playerEventData.getTransfer());
				playerEventData.setTotalTransferCost(
						prevPlayerEvent.get().getTotalTransferCost() + playerEventData.getTransferCost());
			}
		}

		playerEventRepository.save(playerEventData);
	}

	private void createEmptyPlayerEventData(TblPlayer playerData, Long currentEventId) {
		TblPlayerEvent playerEventData = new TblPlayerEvent();
		playerEventData.setActiveChip(null);
		playerEventData.setBank(1000L);
		playerEventData.setEventId(currentEventId);
		playerEventData.setPlayerEntryId(playerData.getId());
		playerEventData.setPlayerEventScore(0L);
		playerEventData.setPlayerTotalScore(0L);
		playerEventData.setPointsOnBench(0L);
		playerEventData.setTransfer(0L);
		playerEventData.setTotalTransfer(0L);
		playerEventData.setTransferCost(0L);
		playerEventData.setTotalTransferCost(0L);
		playerEventData.setValue(1000L);
		playerEventData.setTotalPointsOnBench(0L);
		playerEventRepository.save(playerEventData);
	}
}

class OverallStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
			if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
				if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
					return b.getPlayer().getId().compareTo(a.getPlayer().getId());
				}
				return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
			}
			return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
		}

		return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
	}
}

class GwStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
			if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
				if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
					return b.getPlayer().getId().compareTo(a.getPlayer().getId());
				}
				return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
			}
			return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
		}

		return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
	}
}

class OverallBenchPointStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getTotalPointsOnBench().compareTo(a.getPlayerEvent().getTotalPointsOnBench()) == 0) {
			if (b.getPlayerEvent().getPointsOnBench().compareTo(a.getPlayerEvent().getPointsOnBench()) == 0) {
				if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
					if (b.getPlayerEvent().getPlayerEventScore()
							.compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
						if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
							return b.getPlayer().getId().compareTo(a.getPlayer().getId());
						}
						return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
					}
					return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
				}

				return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
			}

			return b.getPlayerEvent().getPointsOnBench().compareTo(a.getPlayerEvent().getPointsOnBench());
		}

		return b.getPlayerEvent().getTotalPointsOnBench().compareTo(a.getPlayerEvent().getTotalPointsOnBench());
	}
}

class GwBenchPointStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getPointsOnBench().compareTo(a.getPlayerEvent().getPointsOnBench()) == 0) {
			if (b.getPlayerEvent().getTotalPointsOnBench().compareTo(a.getPlayerEvent().getTotalPointsOnBench()) == 0) {
				if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
					if (b.getPlayerEvent().getPlayerTotalScore()
							.compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
						if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
							return b.getPlayer().getId().compareTo(a.getPlayer().getId());
						}
						return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
					}
					return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
				}

				return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
			}

			return b.getPlayerEvent().getTotalPointsOnBench().compareTo(a.getPlayerEvent().getTotalPointsOnBench());
		}

		return b.getPlayerEvent().getPointsOnBench().compareTo(a.getPlayerEvent().getPointsOnBench());

	}
}

class OverallTransferCostStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getTotalTransferCost().compareTo(a.getPlayerEvent().getTotalTransferCost()) == 0) {
			if (b.getPlayerEvent().getTransferCost().compareTo(a.getPlayerEvent().getTransferCost()) == 0) {
				if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
					if (b.getPlayerEvent().getPlayerEventScore()
							.compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
						if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
							return b.getPlayer().getId().compareTo(a.getPlayer().getId());
						}
						return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
					}
					return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
				}

				return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
			}

			return b.getPlayerEvent().getTransferCost().compareTo(a.getPlayerEvent().getTransferCost());
		}

		return b.getPlayerEvent().getTotalTransferCost().compareTo(a.getPlayerEvent().getTotalTransferCost());
	}
}

class GwTransferCostStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getTransferCost().compareTo(a.getPlayerEvent().getTransferCost()) == 0) {
			if (b.getPlayerEvent().getTotalTransferCost().compareTo(a.getPlayerEvent().getTotalTransferCost()) == 0) {
				if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
					if (b.getPlayerEvent().getPlayerTotalScore()
							.compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
						if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
							return b.getPlayer().getId().compareTo(a.getPlayer().getId());
						}
						return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
					}
					return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
				}

				return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
			}

			return b.getPlayerEvent().getTotalTransferCost().compareTo(a.getPlayerEvent().getTotalTransferCost());
		}

		return b.getPlayerEvent().getTransferCost().compareTo(a.getPlayerEvent().getTransferCost());

	}
}

class OverallTransferCountStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getTotalTransfer().compareTo(a.getPlayerEvent().getTotalTransfer()) == 0) {
			if (b.getPlayerEvent().getTransfer().compareTo(a.getPlayerEvent().getTransfer()) == 0) {
				if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
					if (b.getPlayerEvent().getPlayerEventScore()
							.compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
						if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
							return b.getPlayer().getId().compareTo(a.getPlayer().getId());
						}
						return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
					}
					return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
				}

				return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
			}

			return b.getPlayerEvent().getTransfer().compareTo(a.getPlayerEvent().getTransfer());
		}

		return b.getPlayerEvent().getTotalTransfer().compareTo(a.getPlayerEvent().getTotalTransfer());
	}
}

class GwTransferCountStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getTransfer().compareTo(a.getPlayerEvent().getTransfer()) == 0) {
			if (b.getPlayerEvent().getTotalTransfer().compareTo(a.getPlayerEvent().getTotalTransfer()) == 0) {
				if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
					if (b.getPlayerEvent().getPlayerTotalScore()
							.compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
						if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
							return b.getPlayer().getId().compareTo(a.getPlayer().getId());
						}
						return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
					}
					return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
				}

				return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
			}

			return b.getPlayerEvent().getTotalTransfer().compareTo(a.getPlayerEvent().getTotalTransfer());
		}

		return b.getPlayerEvent().getTransfer().compareTo(a.getPlayerEvent().getTransfer());

	}
}

class GwBankValueStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getBank().compareTo(a.getPlayerEvent().getBank()) == 0) {
			if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
				if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
					if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
						return b.getPlayer().getId().compareTo(a.getPlayer().getId());
					}
					return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
				}
				return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
			}

			return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
		}
		return b.getPlayerEvent().getBank().compareTo(a.getPlayerEvent().getBank());

	}
}

class GwTeamValueStandingsSorter implements Comparator<LeagueStandingSortingDto> {
	public int compare(LeagueStandingSortingDto a, LeagueStandingSortingDto b) {

		if (b.getPlayerEvent().getValue().compareTo(a.getPlayerEvent().getValue()) == 0) {
			if (b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore()) == 0) {
				if (b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore()) == 0) {
					if (b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName()) == 0) {
						return b.getPlayer().getId().compareTo(a.getPlayer().getId());
					}
					return b.getPlayer().getPlayerName().compareToIgnoreCase(a.getPlayer().getPlayerName());
				}
				return b.getPlayerEvent().getPlayerTotalScore().compareTo(a.getPlayerEvent().getPlayerTotalScore());
			}

			return b.getPlayerEvent().getPlayerEventScore().compareTo(a.getPlayerEvent().getPlayerEventScore());
		}
		return b.getPlayerEvent().getValue().compareTo(a.getPlayerEvent().getValue());

	}
}
