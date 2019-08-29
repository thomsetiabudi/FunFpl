package com.thomas.personal.fpl.funfpl.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;

public class StandingsDto {

	@JsonAlias("has_next")
	private Boolean hasNext;
	
	private Integer page;
	
	private List<LeagueStandingResultDto> results;

	public Boolean getHasNext() {
		return hasNext;
	}

	public void setHasNext(Boolean hasNext) {
		this.hasNext = hasNext;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public List<LeagueStandingResultDto> getResults() {
		return results;
	}

	public void setResults(List<LeagueStandingResultDto> results) {
		this.results = results;
	}
	
}
