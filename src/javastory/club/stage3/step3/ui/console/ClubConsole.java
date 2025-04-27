package javastory.club.stage3.step3.ui.console;

import javastory.club.stage3.step3.logic.ServiceLogicLycler;
import javastory.club.stage3.step3.service.ClubService;
import javastory.club.stage3.step3.service.dto.TravelClubDto;
import javastory.club.stage3.step3.util.ClubDuplicationException;
import javastory.club.stage3.step3.util.NoSuchClubException;
import javastory.club.stage3.step4.util.NoSuchMembershipException;
import javastory.club.stage3.util.ConsoleUtil;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.TalkingAt;

public class ClubConsole {
	//
	private ClubService clubService;

	private ConsoleUtil consoleUtil;
	private Narrator narrator;

	public ClubConsole() {
		this.clubService = ServiceLogicLycler.shareInstance().createClubService();

		this.narrator = new Narrator(this, TalkingAt.Left);
		this.consoleUtil = new ConsoleUtil(narrator);
	}

	public void register() {
		//
		while (true) {
			//
			String clubName = consoleUtil.getValueOf("\n club name(0.Club menu)");
			if (clubName.equals("0")) {
				return;
			}
			String intro = consoleUtil.getValueOf(" club intro(0.Club menu)");
			if (intro.equals("0")) {
				return;
			}

			try {
				//
				TravelClubDto clubDto = new TravelClubDto(clubName, intro);
				clubService.register(clubDto);
				narrator.say("\n Registered club: " + clubDto.toString());
			} catch (ClubDuplicationException e) {
				//
				narrator.sayln(e.getMessage());
			}
		}
	}

	//break 없는 거
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
				//break없음 -> 계속 진행
			} catch (NoSuchClubException e) {
				narrator.sayln(e.getMessage());
			}
		}
		return clubFound;
	}

	//break?
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
				break; //break;
			} catch (NoSuchClubException e) {
				narrator.sayln(e.getMessage());
			}
		}
		return clubFound;
	}

	public void modify() {
		//1. findOne으로 객체 얻기
		TravelClubDto targetClub = findOne();
		//만약 club이 null이면 return;
		if (targetClub == null) {
			return;
		}

		//아니면 newName 받고 검증
		String newName = consoleUtil.getValueOf("\n new club name(0.Club menu, Enter. no change)");

		//만약 newName이 "0"이면 return;
		if (newName.equals("0")) {
			return;
		}


		//만약 newName이 비어있지 않으면 setName
		if (!newName.isEmpty()) {
			targetClub.setName(newName);
		}

		//새로운 intro 받고 검증
		String newIntro = consoleUtil.getValueOf(" new club intro(Enter. no change)");
		if (!newIntro.isEmpty()) {
			targetClub.setIntro(newIntro);
		}


		//modify-> IllegalArgumentException| NoSuchClubException -> 잘못 적거나, 수정하려는 클래스 없을 수 있으니까
		try {
			clubService.modify(targetClub);
			narrator.sayln("\t > Modified club: " + targetClub);
		} catch (IllegalArgumentException | NoSuchClubException e) {
			narrator.sayln(e.getMessage());
		}
	}

	public void remove() {
		//findOne으로 객체 찾기
		TravelClubDto targetClub = findOne();

		//만약 찾은 객체가 null이면 return
		if (targetClub == null) {
			return;
		}

		//try-catch (NoSuchMembershipException)
    try {
		//확인 문구 받기 (yes,y)
      String confirmStr = consoleUtil.getValueOf("Remove this club? (Y:yes, N:no)");
		//만약 yes,y이면
	  if (confirmStr.toLowerCase().equals("y") || confirmStr.toLowerCase().equals("yes")) {
        narrator.sayln("Removing a club --> " + targetClub.getName());
		  //clubService.remove(clubDTO.getUsid()); -> id로 삭제..
		clubService.remove(targetClub.getUsid());
      } else {
        narrator.sayln("Remove cancelled, your club is safe. --> " + targetClub.getName());
      }
    } catch (NoSuchMembershipException e) {
      narrator.sayln(e.getMessage());
    }
	}
}
