package com.thomas.personal.fpl.funfpl.persistence;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "playerEvent")
public class TblPlayerEvent {

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long playerEventId;

	private Long playerEntryId;
	
	private Long eventId;
	
	private Long playerEventScore;
	
	private Long playerTotalScore;
	
	private String activeChip;
	
	private Long bank;
	
	private Long value;
	
	private Long transfer;
	
	private Long totalTransfer;
	
	private Long transferCost;
	
	private Long totalTransferCost;
	
	private Long pointsOnBench;
	
	private Long totalPointsOnBench;
	
	public TblPlayerEvent() {
		// do nothing
	}

	public Long getPlayerEventId() {
		return playerEventId;
	}

	public void setPlayerEventId(Long playerEventId) {
		this.playerEventId = playerEventId;
	}

	public Long getPlayerEntryId() {
		return playerEntryId;
	}

	public void setPlayerEntryId(Long playerEntryId) {
		this.playerEntryId = playerEntryId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
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

	public String getActiveChip() {
		return activeChip;
	}

	public void setActiveChip(String activeChip) {
		this.activeChip = activeChip;
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

	public Long getTransfer() {
		return transfer;
	}

	public void setTransfer(Long transfer) {
		this.transfer = transfer;
	}

	public Long getTotalTransfer() {
		return totalTransfer;
	}

	public void setTotalTransfer(Long totalTransfer) {
		this.totalTransfer = totalTransfer;
	}

	public Long getTransferCost() {
		return transferCost;
	}

	public void setTransferCost(Long transferCost) {
		this.transferCost = transferCost;
	}

	public Long getTotalTransferCost() {
		return totalTransferCost;
	}

	public void setTotalTransferCost(Long totalTransferCost) {
		this.totalTransferCost = totalTransferCost;
	}

	public Long getPointsOnBench() {
		return pointsOnBench;
	}

	public void setPointsOnBench(Long pointsOnBench) {
		this.pointsOnBench = pointsOnBench;
	}

	public Long getTotalPointsOnBench() {
		return totalPointsOnBench;
	}

	public void setTotalPointsOnBench(Long totalPointsOnBench) {
		this.totalPointsOnBench = totalPointsOnBench;
	}

}
