package com.thomas.personal.fpl.funfpl.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import com.thomas.personal.fpl.funfpl.bl.DataViewBl;
import com.thomas.personal.fpl.funfpl.model.LeagueGwStandingsDataDownloadDto;
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

	@GetMapping("/csv/availableGwStandings")
	public void getCsvAvailableLeagueGwStandings(@RequestParam("leagueid") Long leagueid, HttpServletResponse response)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
		// set file name and content type
		String filename = leagueid + "-" + "leagueAllGwStandingsDataDownloadDto.csv";

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		// create a csv writer
		StatefulBeanToCsv<LeagueGwStandingsDataDownloadDto> writer = new StatefulBeanToCsvBuilder<LeagueGwStandingsDataDownloadDto>(
				response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

		// write all users to csv file
		writer.write(dataViewBl.getAvailableLeagueGwStandingsDownload(leagueid));
	}

	@GetMapping("/leaguegwstandings")
	public String getLeagueGwStandings(@RequestParam("leagueid") Long leagueid, @RequestParam("event") Long event,
			Model model) {
		model.addAttribute("leagueid", leagueid);
		model.addAttribute("event", event);

		List<LeagueGwStandingsDataDto> leagueStandingsData = dataViewBl.getLeagueGwStandings(leagueid, event);
		model.addAttribute("leagueGwStandings", leagueStandingsData);

		if (leagueStandingsData.isEmpty()) {
			return "leaguegwstandings";
		}

		model.addAttribute("leagueName", leagueStandingsData.get(0).getLeagueName());
		model.addAttribute("leagueGwPointRankCopyText",
				dataViewBl.createLeagueGwPointRankCopyText(leagueStandingsData, event));
		model.addAttribute("leagueGwSummaryCopyText",
				dataViewBl.createLeagueGwSummaryCopyText(leagueStandingsData, event, leagueid));
		model.addAttribute("leagueLeaguePointRankCopyText",
				dataViewBl.createLeagueLeaguePointRankCopyText(event, leagueid));

		return "leaguegwstandings";
	}

	@GetMapping("/csv/leaguegwstandings")
	public void getCsvLeagueGwStandings(@RequestParam("leagueid") Long leagueid, @RequestParam("event") Long event,
			HttpServletResponse response)
			throws IOException, CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {

		// set file name and content type
		String filename = leagueid + "-" + event + "-" + "leagueGwStandingsDataDownloadDto.csv";

		response.setContentType("text/csv");
		response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");

		// create a csv writer
		StatefulBeanToCsv<LeagueGwStandingsDataDownloadDto> writer = new StatefulBeanToCsvBuilder<LeagueGwStandingsDataDownloadDto>(
				response.getWriter()).withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
						.withSeparator(CSVWriter.DEFAULT_SEPARATOR).withOrderedResults(false).build();

		// write all users to csv file
		writer.write(dataViewBl.getLeagueGwStandingsDownload(event, leagueid));
	}

	@GetMapping("/leaguegwrecord")
	public String getLeagueGwRecord(@RequestParam("leagueid") Long leagueid, @RequestParam("event") Long event,
			HttpServletResponse response, Model model) {

		model.addAttribute("leagueid", leagueid);
		model.addAttribute("event", event);

		model.addAttribute("leagueGwRecordCopyText", dataViewBl.createLeagueGwRecordCopyText(leagueid, event));

		return "leaguegwrecord";
	}
}
