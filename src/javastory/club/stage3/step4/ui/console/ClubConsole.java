package javastory.club.stage3.step4.ui.console;

import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step4.logic.ServiceLogicLycler;
import javastory.club.stage3.step4.service.ClubService;
import javastory.club.stage3.step4.service.dto.TravelClubDto;
import javastory.club.stage3.step4.util.ClubDuplicationException;
import javastory.club.stage3.step4.util.NoSuchClubException;
import javastory.club.stage3.util.ConsoleUtil;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.TalkingAt;

public class ClubConsole {
	//
	private ClubService clubService;

	private ConsoleUtil consoleUtil;
	private Narrator narrator;

	public ClubConsole() {
		//
		this.clubService = ServiceLogicLycler.shareInstance().createClubService();
		this.narrator = new Narrator(this, TalkingAt.Left);
		this.consoleUtil = new ConsoleUtil(narrator);
	}

	public void register() {
		//클럽 이름
		while(true){
			String name = consoleUtil.getValueOf("insert name(0.menu)");
			if(name.equals("0")){
				break;
			}


			String intro = consoleUtil.getValueOf("insert intro(0.menu)");

			//객체 만들기 -> 보내는 객체는 항상 DTO

			try{
				TravelClubDto club = new TravelClubDto(name, intro);
				clubService.register(club);
			}catch(ClubDuplicationException e){
				narrator.sayln(e.getMessage());
			}
		}

	}

	public TravelClubDto find() {
		//
		TravelClubDto clubFound = null;
		while (true) {
			//
			String clubName = consoleUtil.getValueOf("\n club name to find(0.Club menu) ");
			if (clubName.equals("0")) {
				break;
			}

			try {
				clubFound = clubService.findClubByName(clubName);
				narrator.sayln("\t > Found club: " + clubFound);
			} catch (NoSuchClubException e) {
				narrator.sayln(e.getMessage());
			}
		}
		return clubFound;
	}

	public TravelClubDto findOne() {
		//
		TravelClubDto clubFound = null;
		while (true) {
			//
			String clubName = consoleUtil.getValueOf("\n club name to find(0.Club menu) ");
			if (clubName.equals("0")) {
				break;
			}

			try {
				clubFound = clubService.findClubByName(clubName);
				narrator.sayln("\t > Found club: " + clubFound);
				break;
			} catch (NoSuchClubException e) {
				narrator.sayln(e.getMessage());
			}
		}
		return clubFound;
	}

	public void modify() {
		//
		TravelClubDto targetClub = findOne();
		if (targetClub == null) {
			return;
		}

		String newName = consoleUtil.getValueOf("\n new club name(0.Club menu, Enter. no change)");
		if (newName.equals("0")) {
			return;
		}
		targetClub.setName(newName);

		String newIntro = consoleUtil.getValueOf(" new club intro(Enter. no change)");
		targetClub.setIntro(newIntro);

		try {
			clubService.modify(targetClub);
			narrator.sayln("\t > Modified club: " + targetClub);
		} catch (IllegalArgumentException | NoSuchClubException e) {
			narrator.sayln(e.getMessage());
		}



	}

	public void remove() {
		//
		TravelClubDto targetClub = findOne();
		if (targetClub == null) {
			return;
		}

		String confirmStr = consoleUtil.getValueOf("Remove this club? (Y:yes, N:no)");
		if (confirmStr.toLowerCase().equals("y") || confirmStr.toLowerCase().equals("yes")) {
			narrator.sayln("Removing a club --> " + targetClub.getName());
			clubService.remove(targetClub.getUsid());
		} else {
			narrator.sayln("Remove cancelled, your club is safe. --> " + targetClub.getName());
		}
	}
}
