package com.thomas.personal.fpl.funfpl.persistence;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "process")
public class TblProcess {

	@Id
	private Long processId;

	private Timestamp lastProcess;
	
	public TblProcess() {
		// do nothing
	}

	public Long getProcessId() {
		return processId;
	}

	public void setProcessId(Long processId) {
		this.processId = processId;
	}

	public Timestamp getLastProcess() {
		return lastProcess;
	}

	public void setLastProcess(Timestamp lastProcess) {
		this.lastProcess = lastProcess;
	}

}
