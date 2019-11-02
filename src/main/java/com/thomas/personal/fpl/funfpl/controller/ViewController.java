package com.thomas.personal.fpl.funfpl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.thomas.personal.fpl.funfpl.bl.DataViewBl;
import com.thomas.personal.fpl.funfpl.model.LeagueGwStandingsDataDto;

@Controller
@RequestMapping(path = "/view")
public class ViewController {

	private DataViewBl dataViewBl;

	@Autowired
	public ViewController(DataViewBl dataViewBl) {
		this.dataViewBl = dataViewBl;
	}

	@GetMapping("/gw")
	public String getLeagueList(Model model) {
		model.addAttribute("leagueList", dataViewBl.getLeagueList());
		return "leagueselection";
	}
	
	@GetMapping("/availableGwStandings")
	public String getAvailableLeagueGwStandings(@RequestParam("leagueid") Long leagueid, Model model) {
		model.addAttribute("leagueid", leagueid);
		model.addAttribute("availableGwStandings", dataViewBl.getAvailableGwStandings());
		return "availablegwstandings";
	}
	
	@GetMapping("/leaguegwstandings")
	public String getLeagueGwStandings(@RequestParam("leagueid") Long leagueid, @RequestParam("event") Long event, Model model) {
		model.addAttribute("leagueid", leagueid);
		model.addAttribute("event", event);
		
		List<LeagueGwStandingsDataDto> leagueStandingsData = dataViewBl.getLeagueGwStandings(leagueid, event);
		model.addAttribute("leagueGwStandings", leagueStandingsData);
		
		if(leagueStandingsData.isEmpty()) {
			return "leaguegwstandings";
		}
		
		model.addAttribute("leagueName", leagueStandingsData.get(0).getLeagueName());
		model.addAttribute("leagueGwPointRankCopyText", dataViewBl.createLeagueGwPointRankCopyText(leagueStandingsData, event));
		model.addAttribute("leagueGwSummaryCopyText", dataViewBl.createLeagueGwSummaryCopyText(leagueStandingsData, event, leagueid));
		model.addAttribute("leagueLeaguePointRankCopyText", dataViewBl.createLeagueLeaguePointRankCopyText(event, leagueid));
		
		return "leaguegwstandings";
	}
}
