package com.thomas.personal.fpl.funfpl.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "league_player")
public class TblLeaguePlayer {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long leaguePlayerId;

	private Long leagueId;
	
	private Long playerEntryId;
	
	public TblLeaguePlayer() {
		// do nothing
	}

	public Long getLeaguePlayerId() {
		return leaguePlayerId;
	}

	public void setLeaguePlayerId(Long leaguePlayerId) {
		this.leaguePlayerId = leaguePlayerId;
	}

	public Long getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(Long leagueId) {
		this.leagueId = leagueId;
	}

	public Long getPlayerEntryId() {
		return playerEntryId;
	}

	public void setPlayerEntryId(Long playerEntryId) {
		this.playerEntryId = playerEntryId;
	}

}
