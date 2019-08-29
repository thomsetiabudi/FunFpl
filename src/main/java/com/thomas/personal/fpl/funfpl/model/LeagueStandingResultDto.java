package com.thomas.personal.fpl.funfpl.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class LeagueStandingResultDto {

	private Long id;
	
	@JsonAlias("event_total")
	private Long eventTotal;
	
	@JsonAlias("player_name")
	private String playerName;
	
	private Long rank;
	
	@JsonAlias("last_rank")
	private Long lastRank;
	
	@JsonAlias("rank_sort")
	private Long rankSort;
	
	private Long total;
	
	private Long entry;
	
	@JsonAlias("entry_name")
	private String entryName;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getEventTotal() {
		return eventTotal;
	}

	public void setEventTotal(Long eventTotal) {
		this.eventTotal = eventTotal;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public Long getRank() {
		return rank;
	}

	public void setRank(Long rank) {
		this.rank = rank;
	}

	public Long getLastRank() {
		return lastRank;
	}

	public void setLastRank(Long lastRank) {
		this.lastRank = lastRank;
	}

	public Long getRankSort() {
		return rankSort;
	}

	public void setRankSort(Long rankSort) {
		this.rankSort = rankSort;
	}

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getEntry() {
		return entry;
	}

	public void setEntry(Long entry) {
		this.entry = entry;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String entryName) {
		this.entryName = entryName;
	}
}
