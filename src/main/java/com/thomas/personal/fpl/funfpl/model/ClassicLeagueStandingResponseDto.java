package com.thomas.personal.fpl.funfpl.model;

public class ClassicLeagueStandingResponseDto {

	private LeagueDto league;
	
	private StandingsDto standings;

	public LeagueDto getLeague() {
		return league;
	}

	public void setLeague(LeagueDto league) {
		this.league = league;
	}

	public StandingsDto getStandings() {
		return standings;
	}

	public void setStandings(StandingsDto standings) {
		this.standings = standings;
	}
	
}
