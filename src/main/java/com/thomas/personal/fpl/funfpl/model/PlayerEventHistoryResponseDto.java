package com.thomas.personal.fpl.funfpl.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PlayerEventHistoryResponseDto {

	@JsonAlias("active_chip")
	private String activeChip;
	
	@JsonAlias("entry_history")
	private PlayerEventHistoryDto entryHistory;

	public String getActiveChip() {
		return activeChip;
	}

	public void setActiveChip(String activeChip) {
		this.activeChip = activeChip;
	}

	public PlayerEventHistoryDto getEntryHistory() {
		return entryHistory;
	}

	public void setEntryHistory(PlayerEventHistoryDto entryHistory) {
		this.entryHistory = entryHistory;
	}

	
}
