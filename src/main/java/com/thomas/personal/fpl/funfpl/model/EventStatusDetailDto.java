package com.thomas.personal.fpl.funfpl.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class EventStatusDetailDto {
	
	@JsonAlias("bonus_added")
	private Boolean bonusAdded;
	
	private String date;
	
	private Long event;
	
	private String points;

	public Boolean getBonusAdded() {
		return bonusAdded;
	}

	public void setBonusAdded(Boolean bonusAdded) {
		this.bonusAdded = bonusAdded;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getEvent() {
		return event;
	}

	public void setEvent(Long event) {
		this.event = event;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}
	
}
