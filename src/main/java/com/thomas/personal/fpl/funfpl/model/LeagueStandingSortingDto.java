package com.thomas.personal.fpl.funfpl.model;

import com.thomas.personal.fpl.funfpl.persistence.TblPlayer;
import com.thomas.personal.fpl.funfpl.persistence.TblPlayerEvent;

public class LeagueStandingSortingDto {
	
	private TblPlayer player;
	
	private TblPlayerEvent playerEvent;

	public TblPlayer getPlayer() {
		return player;
	}

	public void setPlayer(TblPlayer player) {
		this.player = player;
	}

	public TblPlayerEvent getPlayerEvent() {
		return playerEvent;
	}

	public void setPlayerEvent(TblPlayerEvent playerEvent) {
		this.playerEvent = playerEvent;
	}

}
