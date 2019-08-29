package com.thomas.personal.fpl.funfpl.model;

import java.util.List;

public class EventStatusResponseDto {
	
	private String leagues;
	
	private List<EventStatusDetailDto> status;

	public String getLeagues() {
		return leagues;
	}

	public void setLeagues(String leagues) {
		this.leagues = leagues;
	}

	public List<EventStatusDetailDto> getStatus() {
		return status;
	}

	public void setStatus(List<EventStatusDetailDto> status) {
		this.status = status;
	}
}
