package com.thomas.personal.fpl.funfpl.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "league_gw_standings")
public class TblLeagueGwStandings {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long leagueGwStandingsId;

	private Long leagueId;
	
	private Long eventId;
	
	private Long playerEntryId;
	
	private Long playerEventScore;
	
	private Long playerTotalScore;
	
	private String playerActiveChip;
	
	private Long playerBank;
	
	private Long playerValue;
	
	private Long playerEventTransfer;
	
	private Long playerTotalTransfer;
	
	private Long playerEventTransferCost;
	
	private Long playerTotalTransferCost;
	
	private Long playerEventPointsOnBench;
	
	private Long playerTotalPointsOnBench;
	
	private Integer playerGwStandingsOrder;
	
	private Integer playerPrevGwStandingsOrder;
	
	private Integer playerGwStandingsPositionGain;
	
	private Integer playerGwStandingsRank;
	
	private Integer playerPrevGwStandingsRank;
	
	private Integer playerGwStandingsRankPositionGain;
	
	private Boolean playerIsGwStandingsLastPos;
	
	private Integer playerOverallStandingsOrder;
	
	private Integer playerPrevOverallStandingsOrder;
	
	private Integer playerOverallStandingsPositionGain;
	
	private Boolean playerIsOverallStandingsLastPos;
	
	private Integer playerGwBenchPointStandingsOrder;
	
	private Integer playerPrevGwBenchPointStandingsOrder;
	
	private Integer playerGwBenchPointStandingsPositionGain;
	
	private Integer playerGwBenchPointStandingsRank;
	
	private Integer playerPrevGwBenchPointStandingsRank;
	
	private Integer playerGwBenchPointStandingsRankPositionGain;
	
	private Boolean playerIsGwBenchPointStandingsLastPos;
	
	private Integer playerOverallBenchPointStandingsOrder;
	
	private Integer playerPrevOverallBenchPointStandingsOrder;
	
	private Integer playerOverallBenchPointStandingsPositionGain;
	
	private Boolean playerIsOverallBenchPointStandingsLastPos;
	
	private Integer playerGwTransferCostStandingsOrder;
	
	private Integer playerPrevGwTransferCostStandingsOrder;
	
	private Integer playerGwTransferCostStandingsPositionGain;
	
	private Integer playerGwTransferCostStandingsRank;
	
	private Integer playerPrevGwTransferCostStandingsRank;
	
	private Integer playerGwTransferCostStandingsRankPositionGain;
	
	private Boolean playerIsGwTransferCostStandingsLastPos;
	
	private Integer playerOverallTransferCostStandingsOrder;
	
	private Integer playerPrevOverallTransferCostStandingsOrder;
	
	private Integer playerOverallTransferCostStandingsPositionGain;
	
	private Boolean playerIsOverallTransferCostStandingsLastPos;
	
	private Integer playerGwTransferCountStandingsOrder;
	
	private Integer playerPrevGwTransferCountStandingsOrder;
	
	private Integer playerGwTransferCountStandingsPositionGain;
	
	private Integer playerGwTransferCountStandingsRank;
	
	private Integer playerPrevGwTransferCountStandingsRank;
	
	private Integer playerGwTransferCountStandingsRankPositionGain;
	
	private Boolean playerIsGwTransferCountStandingsLastPos;
	
	private Integer playerOverallTransferCountStandingsOrder;
	
	private Integer playerPrevOverallTransferCountStandingsOrder;
	
	private Integer playerOverallTransferCountStandingsPositionGain;
	
	private Boolean playerIsOverallTransferCountStandingsLastPos;
	
	private Integer playerGwBankValueStandingsOrder;
	
	private Integer playerPrevGwBankValueStandingsOrder;
	
	private Integer playerGwBankValueStandingsPositionGain;
	
	private Integer playerGwBankValueStandingsRank;
	
	private Integer playerPrevGwBankValueStandingsRank;
	
	private Integer playerGwBankValueStandingsRankPositionGain;
	
	private Boolean playerIsGwBankValueStandingsLastPos;
	
	private Integer playerGwTeamValueStandingsOrder;
	
	private Integer playerPrevGwTeamValueStandingsOrder;
	
	private Integer playerGwTeamValueStandingsPositionGain;
	
	private Integer playerGwTeamValueStandingsRank;
	
	private Integer playerPrevGwTeamValueStandingsRank;
	
	private Integer playerGwTeamValueStandingsRankPositionGain;
	
	private Boolean playerIsGwTeamValueStandingsLastPos;
		
	public TblLeagueGwStandings() {
		// do nothing
	}

	public Long getLeagueGwStandingsId() {
		return leagueGwStandingsId;
	}

	public void setLeagueGwStandingsId(Long leagueGwStandingsId) {
		this.leagueGwStandingsId = leagueGwStandingsId;
	}

	public Long getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(Long leagueId) {
		this.leagueId = leagueId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getPlayerEntryId() {
		return playerEntryId;
	}

	public void setPlayerEntryId(Long playerEntryId) {
		this.playerEntryId = playerEntryId;
	}

	public Long getPlayerEventScore() {
		return playerEventScore;
	}

	public void setPlayerEventScore(Long playerEventScore) {
		this.playerEventScore = playerEventScore;
	}

	public Long getPlayerTotalScore() {
		return playerTotalScore;
	}

	public void setPlayerTotalScore(Long playerTotalScore) {
		this.playerTotalScore = playerTotalScore;
	}

	public String getPlayerActiveChip() {
		return playerActiveChip;
	}

	public void setPlayerActiveChip(String playerActiveChip) {
		this.playerActiveChip = playerActiveChip;
	}

	public Long getPlayerBank() {
		return playerBank;
	}

	public void setPlayerBank(Long playerBank) {
		this.playerBank = playerBank;
	}

	public Long getPlayerValue() {
		return playerValue;
	}

	public void setPlayerValue(Long playerValue) {
		this.playerValue = playerValue;
	}

	public Long getPlayerEventTransfer() {
		return playerEventTransfer;
	}

	public void setPlayerEventTransfer(Long playerEventTransfer) {
		this.playerEventTransfer = playerEventTransfer;
	}

	public Long getPlayerTotalTransfer() {
		return playerTotalTransfer;
	}

	public void setPlayerTotalTransfer(Long playerTotalTransfer) {
		this.playerTotalTransfer = playerTotalTransfer;
	}

	public Long getPlayerEventTransferCost() {
		return playerEventTransferCost;
	}

	public void setPlayerEventTransferCost(Long playerEventTransferCost) {
		this.playerEventTransferCost = playerEventTransferCost;
	}

	public Long getPlayerTotalTransferCost() {
		return playerTotalTransferCost;
	}

	public void setPlayerTotalTransferCost(Long playerTotalTransferCost) {
		this.playerTotalTransferCost = playerTotalTransferCost;
	}

	public Long getPlayerEventPointsOnBench() {
		return playerEventPointsOnBench;
	}

	public void setPlayerEventPointsOnBench(Long playerEventPointsOnBench) {
		this.playerEventPointsOnBench = playerEventPointsOnBench;
	}

	public Long getPlayerTotalPointsOnBench() {
		return playerTotalPointsOnBench;
	}

	public void setPlayerTotalPointsOnBench(Long playerTotalPointsOnBench) {
		this.playerTotalPointsOnBench = playerTotalPointsOnBench;
	}

	public Integer getPlayerGwStandingsOrder() {
		return playerGwStandingsOrder;
	}

	public void setPlayerGwStandingsOrder(Integer playerGwStandingsOrder) {
		this.playerGwStandingsOrder = playerGwStandingsOrder;
	}

	public Integer getPlayerPrevGwStandingsOrder() {
		return playerPrevGwStandingsOrder;
	}

	public void setPlayerPrevGwStandingsOrder(Integer playerPrevGwStandingsOrder) {
		this.playerPrevGwStandingsOrder = playerPrevGwStandingsOrder;
	}

	public Integer getPlayerGwStandingsPositionGain() {
		return playerGwStandingsPositionGain;
	}

	public void setPlayerGwStandingsPositionGain(Integer playerGwStandingsPositionGain) {
		this.playerGwStandingsPositionGain = playerGwStandingsPositionGain;
	}

	public Integer getPlayerGwStandingsRank() {
		return playerGwStandingsRank;
	}

	public void setPlayerGwStandingsRank(Integer playerGwStandingsRank) {
		this.playerGwStandingsRank = playerGwStandingsRank;
	}

	public Integer getPlayerPrevGwStandingsRank() {
		return playerPrevGwStandingsRank;
	}

	public void setPlayerPrevGwStandingsRank(Integer playerPrevGwStandingsRank) {
		this.playerPrevGwStandingsRank = playerPrevGwStandingsRank;
	}

	public Integer getPlayerGwStandingsRankPositionGain() {
		return playerGwStandingsRankPositionGain;
	}

	public void setPlayerGwStandingsRankPositionGain(Integer playerGwStandingsRankPositionGain) {
		this.playerGwStandingsRankPositionGain = playerGwStandingsRankPositionGain;
	}

	public Boolean getPlayerIsGwStandingsLastPos() {
		return playerIsGwStandingsLastPos;
	}

	public void setPlayerIsGwStandingsLastPos(Boolean playerIsGwStandingsLastPos) {
		this.playerIsGwStandingsLastPos = playerIsGwStandingsLastPos;
	}

	public Integer getPlayerOverallStandingsOrder() {
		return playerOverallStandingsOrder;
	}

	public void setPlayerOverallStandingsOrder(Integer playerOverallStandingsOrder) {
		this.playerOverallStandingsOrder = playerOverallStandingsOrder;
	}

	public Integer getPlayerPrevOverallStandingsOrder() {
		return playerPrevOverallStandingsOrder;
	}

	public void setPlayerPrevOverallStandingsOrder(Integer playerPrevOverallStandingsOrder) {
		this.playerPrevOverallStandingsOrder = playerPrevOverallStandingsOrder;
	}

	public Integer getPlayerOverallStandingsPositionGain() {
		return playerOverallStandingsPositionGain;
	}

	public void setPlayerOverallStandingsPositionGain(Integer playerOverallStandingsPositionGain) {
		this.playerOverallStandingsPositionGain = playerOverallStandingsPositionGain;
	}

	public Boolean getPlayerIsOverallStandingsLastPos() {
		return playerIsOverallStandingsLastPos;
	}

	public void setPlayerIsOverallStandingsLastPos(Boolean playerIsOverallStandingsLastPos) {
		this.playerIsOverallStandingsLastPos = playerIsOverallStandingsLastPos;
	}

	public Integer getPlayerGwBenchPointStandingsOrder() {
		return playerGwBenchPointStandingsOrder;
	}

	public void setPlayerGwBenchPointStandingsOrder(Integer playerGwBenchPointStandingsOrder) {
		this.playerGwBenchPointStandingsOrder = playerGwBenchPointStandingsOrder;
	}

	public Integer getPlayerPrevGwBenchPointStandingsOrder() {
		return playerPrevGwBenchPointStandingsOrder;
	}

	public void setPlayerPrevGwBenchPointStandingsOrder(Integer playerPrevGwBenchPointStandingsOrder) {
		this.playerPrevGwBenchPointStandingsOrder = playerPrevGwBenchPointStandingsOrder;
	}

	public Integer getPlayerGwBenchPointStandingsPositionGain() {
		return playerGwBenchPointStandingsPositionGain;
	}

	public void setPlayerGwBenchPointStandingsPositionGain(Integer playerGwBenchPointStandingsPositionGain) {
		this.playerGwBenchPointStandingsPositionGain = playerGwBenchPointStandingsPositionGain;
	}

	public Integer getPlayerGwBenchPointStandingsRank() {
		return playerGwBenchPointStandingsRank;
	}

	public void setPlayerGwBenchPointStandingsRank(Integer playerGwBenchPointStandingsRank) {
		this.playerGwBenchPointStandingsRank = playerGwBenchPointStandingsRank;
	}

	public Integer getPlayerPrevGwBenchPointStandingsRank() {
		return playerPrevGwBenchPointStandingsRank;
	}

	public void setPlayerPrevGwBenchPointStandingsRank(Integer playerPrevGwBenchPointStandingsRank) {
		this.playerPrevGwBenchPointStandingsRank = playerPrevGwBenchPointStandingsRank;
	}

	public Integer getPlayerGwBenchPointStandingsRankPositionGain() {
		return playerGwBenchPointStandingsRankPositionGain;
	}

	public void setPlayerGwBenchPointStandingsRankPositionGain(Integer playerGwBenchPointStandingsRankPositionGain) {
		this.playerGwBenchPointStandingsRankPositionGain = playerGwBenchPointStandingsRankPositionGain;
	}

	public Boolean getPlayerIsGwBenchPointStandingsLastPos() {
		return playerIsGwBenchPointStandingsLastPos;
	}

	public void setPlayerIsGwBenchPointStandingsLastPos(Boolean playerIsGwBenchPointStandingsLastPos) {
		this.playerIsGwBenchPointStandingsLastPos = playerIsGwBenchPointStandingsLastPos;
	}

	public Integer getPlayerOverallBenchPointStandingsOrder() {
		return playerOverallBenchPointStandingsOrder;
	}

	public void setPlayerOverallBenchPointStandingsOrder(Integer playerOverallBenchPointStandingsOrder) {
		this.playerOverallBenchPointStandingsOrder = playerOverallBenchPointStandingsOrder;
	}

	public Integer getPlayerPrevOverallBenchPointStandingsOrder() {
		return playerPrevOverallBenchPointStandingsOrder;
	}

	public void setPlayerPrevOverallBenchPointStandingsOrder(Integer playerPrevOverallBenchPointStandingsOrder) {
		this.playerPrevOverallBenchPointStandingsOrder = playerPrevOverallBenchPointStandingsOrder;
	}

	public Integer getPlayerOverallBenchPointStandingsPositionGain() {
		return playerOverallBenchPointStandingsPositionGain;
	}

	public void setPlayerOverallBenchPointStandingsPositionGain(Integer playerOverallBenchPointStandingsPositionGain) {
		this.playerOverallBenchPointStandingsPositionGain = playerOverallBenchPointStandingsPositionGain;
	}

	public Boolean getPlayerIsOverallBenchPointStandingsLastPos() {
		return playerIsOverallBenchPointStandingsLastPos;
	}

	public void setPlayerIsOverallBenchPointStandingsLastPos(Boolean playerIsOverallBenchPointStandingsLastPos) {
		this.playerIsOverallBenchPointStandingsLastPos = playerIsOverallBenchPointStandingsLastPos;
	}

	public Integer getPlayerGwTransferCostStandingsOrder() {
		return playerGwTransferCostStandingsOrder;
	}

	public void setPlayerGwTransferCostStandingsOrder(Integer playerGwTransferCostStandingsOrder) {
		this.playerGwTransferCostStandingsOrder = playerGwTransferCostStandingsOrder;
	}

	public Integer getPlayerPrevGwTransferCostStandingsOrder() {
		return playerPrevGwTransferCostStandingsOrder;
	}

	public void setPlayerPrevGwTransferCostStandingsOrder(Integer playerPrevGwTransferCostStandingsOrder) {
		this.playerPrevGwTransferCostStandingsOrder = playerPrevGwTransferCostStandingsOrder;
	}

	public Integer getPlayerGwTransferCostStandingsPositionGain() {
		return playerGwTransferCostStandingsPositionGain;
	}

	public void setPlayerGwTransferCostStandingsPositionGain(Integer playerGwTransferCostStandingsPositionGain) {
		this.playerGwTransferCostStandingsPositionGain = playerGwTransferCostStandingsPositionGain;
	}

	public Integer getPlayerGwTransferCostStandingsRank() {
		return playerGwTransferCostStandingsRank;
	}

	public void setPlayerGwTransferCostStandingsRank(Integer playerGwTransferCostStandingsRank) {
		this.playerGwTransferCostStandingsRank = playerGwTransferCostStandingsRank;
	}

	public Integer getPlayerPrevGwTransferCostStandingsRank() {
		return playerPrevGwTransferCostStandingsRank;
	}

	public void setPlayerPrevGwTransferCostStandingsRank(Integer playerPrevGwTransferCostStandingsRank) {
		this.playerPrevGwTransferCostStandingsRank = playerPrevGwTransferCostStandingsRank;
	}

	public Integer getPlayerGwTransferCostStandingsRankPositionGain() {
		return playerGwTransferCostStandingsRankPositionGain;
	}

	public void setPlayerGwTransferCostStandingsRankPositionGain(Integer playerGwTransferCostStandingsRankPositionGain) {
		this.playerGwTransferCostStandingsRankPositionGain = playerGwTransferCostStandingsRankPositionGain;
	}

	public Boolean getPlayerIsGwTransferCostStandingsLastPos() {
		return playerIsGwTransferCostStandingsLastPos;
	}

	public void setPlayerIsGwTransferCostStandingsLastPos(Boolean playerIsGwTransferCostStandingsLastPos) {
		this.playerIsGwTransferCostStandingsLastPos = playerIsGwTransferCostStandingsLastPos;
	}

	public Integer getPlayerOverallTransferCostStandingsOrder() {
		return playerOverallTransferCostStandingsOrder;
	}

	public void setPlayerOverallTransferCostStandingsOrder(Integer playerOverallTransferCostStandingsOrder) {
		this.playerOverallTransferCostStandingsOrder = playerOverallTransferCostStandingsOrder;
	}

	public Integer getPlayerPrevOverallTransferCostStandingsOrder() {
		return playerPrevOverallTransferCostStandingsOrder;
	}

	public void setPlayerPrevOverallTransferCostStandingsOrder(Integer playerPrevOverallTransferCostStandingsOrder) {
		this.playerPrevOverallTransferCostStandingsOrder = playerPrevOverallTransferCostStandingsOrder;
	}

	public Integer getPlayerOverallTransferCostStandingsPositionGain() {
		return playerOverallTransferCostStandingsPositionGain;
	}

	public void setPlayerOverallTransferCostStandingsPositionGain(Integer playerOverallTransferCostStandingsPositionGain) {
		this.playerOverallTransferCostStandingsPositionGain = playerOverallTransferCostStandingsPositionGain;
	}

	public Boolean getPlayerIsOverallTransferCostStandingsLastPos() {
		return playerIsOverallTransferCostStandingsLastPos;
	}

	public void setPlayerIsOverallTransferCostStandingsLastPos(Boolean playerIsOverallTransferCostStandingsLastPos) {
		this.playerIsOverallTransferCostStandingsLastPos = playerIsOverallTransferCostStandingsLastPos;
	}

	public Integer getPlayerGwTransferCountStandingsOrder() {
		return playerGwTransferCountStandingsOrder;
	}

	public void setPlayerGwTransferCountStandingsOrder(Integer playerGwTransferCountStandingsOrder) {
		this.playerGwTransferCountStandingsOrder = playerGwTransferCountStandingsOrder;
	}

	public Integer getPlayerPrevGwTransferCountStandingsOrder() {
		return playerPrevGwTransferCountStandingsOrder;
	}

	public void setPlayerPrevGwTransferCountStandingsOrder(Integer playerPrevGwTransferCountStandingsOrder) {
		this.playerPrevGwTransferCountStandingsOrder = playerPrevGwTransferCountStandingsOrder;
	}

	public Integer getPlayerGwTransferCountStandingsPositionGain() {
		return playerGwTransferCountStandingsPositionGain;
	}

	public void setPlayerGwTransferCountStandingsPositionGain(Integer playerGwTransferCountStandingsPositionGain) {
		this.playerGwTransferCountStandingsPositionGain = playerGwTransferCountStandingsPositionGain;
	}

	public Integer getPlayerGwTransferCountStandingsRank() {
		return playerGwTransferCountStandingsRank;
	}

	public void setPlayerGwTransferCountStandingsRank(Integer playerGwTransferCountStandingsRank) {
		this.playerGwTransferCountStandingsRank = playerGwTransferCountStandingsRank;
	}

	public Integer getPlayerPrevGwTransferCountStandingsRank() {
		return playerPrevGwTransferCountStandingsRank;
	}

	public void setPlayerPrevGwTransferCountStandingsRank(Integer playerPrevGwTransferCountStandingsRank) {
		this.playerPrevGwTransferCountStandingsRank = playerPrevGwTransferCountStandingsRank;
	}

	public Integer getPlayerGwTransferCountStandingsRankPositionGain() {
		return playerGwTransferCountStandingsRankPositionGain;
	}

	public void setPlayerGwTransferCountStandingsRankPositionGain(Integer playerGwTransferCountStandingsRankPositionGain) {
		this.playerGwTransferCountStandingsRankPositionGain = playerGwTransferCountStandingsRankPositionGain;
	}

	public Boolean getPlayerIsGwTransferCountStandingsLastPos() {
		return playerIsGwTransferCountStandingsLastPos;
	}

	public void setPlayerIsGwTransferCountStandingsLastPos(Boolean playerIsGwTransferCountStandingsLastPos) {
		this.playerIsGwTransferCountStandingsLastPos = playerIsGwTransferCountStandingsLastPos;
	}

	public Integer getPlayerOverallTransferCountStandingsOrder() {
		return playerOverallTransferCountStandingsOrder;
	}

	public void setPlayerOverallTransferCountStandingsOrder(Integer playerOverallTransferCountStandingsOrder) {
		this.playerOverallTransferCountStandingsOrder = playerOverallTransferCountStandingsOrder;
	}

	public Integer getPlayerPrevOverallTransferCountStandingsOrder() {
		return playerPrevOverallTransferCountStandingsOrder;
	}

	public void setPlayerPrevOverallTransferCountStandingsOrder(Integer playerPrevOverallTransferCountStandingsOrder) {
		this.playerPrevOverallTransferCountStandingsOrder = playerPrevOverallTransferCountStandingsOrder;
	}

	public Integer getPlayerOverallTransferCountStandingsPositionGain() {
		return playerOverallTransferCountStandingsPositionGain;
	}

	public void setPlayerOverallTransferCountStandingsPositionGain(
			Integer playerOverallTransferCountStandingsPositionGain) {
		this.playerOverallTransferCountStandingsPositionGain = playerOverallTransferCountStandingsPositionGain;
	}

	public Boolean getPlayerIsOverallTransferCountStandingsLastPos() {
		return playerIsOverallTransferCountStandingsLastPos;
	}

	public void setPlayerIsOverallTransferCountStandingsLastPos(Boolean playerIsOverallTransferCountStandingsLastPos) {
		this.playerIsOverallTransferCountStandingsLastPos = playerIsOverallTransferCountStandingsLastPos;
	}

	public Integer getPlayerGwBankValueStandingsOrder() {
		return playerGwBankValueStandingsOrder;
	}

	public void setPlayerGwBankValueStandingsOrder(Integer playerGwBankValueStandingsOrder) {
		this.playerGwBankValueStandingsOrder = playerGwBankValueStandingsOrder;
	}

	public Integer getPlayerPrevGwBankValueStandingsOrder() {
		return playerPrevGwBankValueStandingsOrder;
	}

	public void setPlayerPrevGwBankValueStandingsOrder(Integer playerPrevGwBankValueStandingsOrder) {
		this.playerPrevGwBankValueStandingsOrder = playerPrevGwBankValueStandingsOrder;
	}

	public Integer getPlayerGwBankValueStandingsPositionGain() {
		return playerGwBankValueStandingsPositionGain;
	}

	public void setPlayerGwBankValueStandingsPositionGain(Integer playerGwBankValueStandingsPositionGain) {
		this.playerGwBankValueStandingsPositionGain = playerGwBankValueStandingsPositionGain;
	}

	public Integer getPlayerGwBankValueStandingsRank() {
		return playerGwBankValueStandingsRank;
	}

	public void setPlayerGwBankValueStandingsRank(Integer playerGwBankValueStandingsRank) {
		this.playerGwBankValueStandingsRank = playerGwBankValueStandingsRank;
	}

	public Integer getPlayerPrevGwBankValueStandingsRank() {
		return playerPrevGwBankValueStandingsRank;
	}

	public void setPlayerPrevGwBankValueStandingsRank(Integer playerPrevGwBankValueStandingsRank) {
		this.playerPrevGwBankValueStandingsRank = playerPrevGwBankValueStandingsRank;
	}

	public Integer getPlayerGwBankValueStandingsRankPositionGain() {
		return playerGwBankValueStandingsRankPositionGain;
	}

	public void setPlayerGwBankValueStandingsRankPositionGain(Integer playerGwBankValueStandingsRankPositionGain) {
		this.playerGwBankValueStandingsRankPositionGain = playerGwBankValueStandingsRankPositionGain;
	}

	public Boolean getPlayerIsGwBankValueStandingsLastPos() {
		return playerIsGwBankValueStandingsLastPos;
	}

	public void setPlayerIsGwBankValueStandingsLastPos(Boolean playerIsGwBankValueStandingsLastPos) {
		this.playerIsGwBankValueStandingsLastPos = playerIsGwBankValueStandingsLastPos;
	}

	public Integer getPlayerGwTeamValueStandingsOrder() {
		return playerGwTeamValueStandingsOrder;
	}

	public void setPlayerGwTeamValueStandingsOrder(Integer playerGwTeamValueStandingsOrder) {
		this.playerGwTeamValueStandingsOrder = playerGwTeamValueStandingsOrder;
	}

	public Integer getPlayerPrevGwTeamValueStandingsOrder() {
		return playerPrevGwTeamValueStandingsOrder;
	}

	public void setPlayerPrevGwTeamValueStandingsOrder(Integer playerPrevGwTeamValueStandingsOrder) {
		this.playerPrevGwTeamValueStandingsOrder = playerPrevGwTeamValueStandingsOrder;
	}

	public Integer getPlayerGwTeamValueStandingsPositionGain() {
		return playerGwTeamValueStandingsPositionGain;
	}

	public void setPlayerGwTeamValueStandingsPositionGain(Integer playerGwTeamValueStandingsPositionGain) {
		this.playerGwTeamValueStandingsPositionGain = playerGwTeamValueStandingsPositionGain;
	}

	public Integer getPlayerGwTeamValueStandingsRank() {
		return playerGwTeamValueStandingsRank;
	}

	public void setPlayerGwTeamValueStandingsRank(Integer playerGwTeamValueStandingsRank) {
		this.playerGwTeamValueStandingsRank = playerGwTeamValueStandingsRank;
	}

	public Integer getPlayerPrevGwTeamValueStandingsRank() {
		return playerPrevGwTeamValueStandingsRank;
	}

	public void setPlayerPrevGwTeamValueStandingsRank(Integer playerPrevGwTeamValueStandingsRank) {
		this.playerPrevGwTeamValueStandingsRank = playerPrevGwTeamValueStandingsRank;
	}

	public Integer getPlayerGwTeamValueStandingsRankPositionGain() {
		return playerGwTeamValueStandingsRankPositionGain;
	}

	public void setPlayerGwTeamValueStandingsRankPositionGain(Integer playerGwTeamValueStandingsRankPositionGain) {
		this.playerGwTeamValueStandingsRankPositionGain = playerGwTeamValueStandingsRankPositionGain;
	}

	public Boolean getPlayerIsGwTeamValueStandingsLastPos() {
		return playerIsGwTeamValueStandingsLastPos;
	}

	public void setPlayerIsGwTeamValueStandingsLastPos(Boolean playerIsGwTeamValueStandingsLastPos) {
		this.playerIsGwTeamValueStandingsLastPos = playerIsGwTeamValueStandingsLastPos;
	}


}
