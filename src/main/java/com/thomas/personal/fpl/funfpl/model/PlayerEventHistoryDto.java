package com.thomas.personal.fpl.funfpl.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class PlayerEventHistoryDto {

	private Long event;
	
	private Long points;
	
	@JsonAlias("total_points")
	private Long totalPoints;
	
	private Long bank;
	
	private Long value;
	
	@JsonAlias("event_transfers")
	private Long eventTransfers;
	
	@JsonAlias("event_transfers_cost")
	private Long eventTransfersCost;

	@JsonAlias("points_on_bench")
	private Long pointsOnBench;

	public Long getEvent() {
		return event;
	}

	public void setEvent(Long event) {
		this.event = event;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public Long getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Long totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Long getBank() {
		return bank;
	}

	public void setBank(Long bank) {
		this.bank = bank;
	}

	public Long getValue() {
		return value;
	}

	public void setValue(Long value) {
		this.value = value;
	}

	public Long getEventTransfers() {
		return eventTransfers;
	}

	public void setEventTransfers(Long eventTransfers) {
		this.eventTransfers = eventTransfers;
	}

	public Long getEventTransfersCost() {
		return eventTransfersCost;
	}

	public void setEventTransfersCost(Long eventTransfersCost) {
		this.eventTransfersCost = eventTransfersCost;
	}

	public Long getPointsOnBench() {
		return pointsOnBench;
	}

	public void setPointsOnBench(Long pointsOnBench) {
		this.pointsOnBench = pointsOnBench;
	}
	
}
