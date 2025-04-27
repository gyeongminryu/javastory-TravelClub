package javastory.club.stage3.step3.ui.console;

import javastory.club.stage3.step1.entity.club.RoleInClub;
import javastory.club.stage3.step3.logic.ServiceLogicLycler;
import javastory.club.stage3.step3.service.ClubService;
import javastory.club.stage3.step3.service.dto.ClubMembershipDto;
import javastory.club.stage3.step3.service.dto.TravelClubDto;
import javastory.club.stage3.step3.util.MemberDuplicationException;
import javastory.club.stage3.step3.util.NoSuchClubException;
import javastory.club.stage3.step3.util.NoSuchMemberException;
import javastory.club.stage3.util.ConsoleUtil;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.TalkingAt;

public class ClubMembershipConsole {
	//
	private TravelClubDto currentClub;//currentClub? -> ★TravelClubDTO에 저장된 거 사용?

	private ClubService clubService;

	private ConsoleUtil consoleUtil;
	private Narrator narrator;

	public ClubMembershipConsole() {
		//서비스는 그대로 clubService 사용
		this.clubService = ServiceLogicLycler.shareInstance().createClubService();

		this.narrator = new Narrator(this, TalkingAt.Left);
		this.consoleUtil = new ConsoleUtil(narrator);
	}


	//현재 클럽 있는지 확인
	public boolean hasCurrentClub() {
		return currentClub != null;
	}


	//현재 클럽 이름 요청
	public String requestCurrentClubName() {
		//
		String clubName = null;

		if (hasCurrentClub()) { //만약에 currentClub이 있으면
			clubName = currentClub.getName();
		}

		return clubName;
	}


	//클럽 찾기
	public void findClub() {
		//
		TravelClubDto clubFound = null;
		while (true) {
			String clubName = consoleUtil.getValueOf("\n club name to find(0.Membership menu) ");
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
			clubFound = null;
		}
		this.currentClub = clubFound;
	}


	//멤버쉽 찾기
	public void add() {
		//
		if (!hasCurrentClub()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		while(true) {
			
			//이메일 입력 + 검증
			String email = consoleUtil.getValueOf("\n member's email to add(0.Member menu)");
			if (email.equals("0")) {
				return;
			}

			//memberRole 입력받기
			String memberRole = consoleUtil.getValueOf(" President|Member");

			try {
				//추가할 객체 생성
				//ClubMembershipDTO 생성자 + setRole
				ClubMembershipDto clubMembershipDto = new ClubMembershipDto(currentClub.getUsid(), email);
				clubMembershipDto.setRole(RoleInClub.valueOf(memberRole));



				//clubService에 addMembership
		        clubService.addMembership(clubMembershipDto);


				narrator.sayln(String.format("\n Add a member[email:%s] in club[name:%s]", email, currentClub.getName()));


				//다른 방식의 예외처리
			} catch (MemberDuplicationException | NoSuchClubException e) { //멤버가 두명일 경우. 그런 클럽이 없을 떄
				narrator.sayln(e.getMessage());
			} catch (IllegalArgumentException e) { //입력이 잘못됐을 경우
				narrator.sayln("The Role in club should be president or member.");
			}
		}

	}


	//멤버쉽 찾기
	public void find() {
		//
		if (!hasCurrentClub()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		ClubMembershipDto membershipDto = null;
		while (true) {
			String memberEmail = consoleUtil.getValueOf("\n email to find(0.Membership menu) ");
			if (memberEmail.equals("0")) {
				break;
			}
			try {
				membershipDto = clubService.findMembershipIn(currentClub.getUsid(), memberEmail);
				narrator.sayln("\t > Found membership information: " + membershipDto);
			} catch (NoSuchMemberException e) {
				narrator.sayln(e.getMessage());
			}
		}
	}

	public ClubMembershipDto findOne() {
		//
		ClubMembershipDto membershipDto = null;
		while (true) {
			String memberEmail = consoleUtil.getValueOf("\n member email to find(0.Membership menu) ");
			if (memberEmail.equals("0")) {
				break;
			}

			try {
				membershipDto = clubService.findMembershipIn(currentClub.getUsid(), memberEmail);
				narrator.sayln("\t > Found membership information: " + membershipDto);
				break;
			} catch (NoSuchMemberException e) {
				narrator.sayln(e.getMessage());
			}
		}
		return membershipDto;
	}

	public void modify() {
		//
		if (!hasCurrentClub()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		ClubMembershipDto targetMembership = findOne();
		if (targetMembership == null) {
			return;
		}

		String newRole = consoleUtil.getValueOf("\n new President|Member(0.Membership menu, Enter. no change)");
		if (newRole.equals("0")) {
			return;
		}
		if (!newRole.equals("")) {
			targetMembership.setRole(RoleInClub.valueOf(newRole));
		}



		String clubId = targetMembership.getClubId();

		//여긴 try-catch 안해주는데 이유?
		clubService.modifyMembership(clubId, targetMembership);

		ClubMembershipDto modifiedMembership = clubService.findMembershipIn(clubId, targetMembership.getMemberEmail());
		narrator.sayln("\t > Modified membership information: " + modifiedMembership);
	}

	public void remove() {
		//
		if (!hasCurrentClub()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		ClubMembershipDto targetMembership = findOne();
		if (targetMembership == null) {
			return;
		}

		String confirmStr = consoleUtil.getValueOf("\n Remove this member in the club? (Y:yes, N:no)");
		if (confirmStr.toLowerCase().equals("y") || confirmStr.toLowerCase().equals("yes")) {
			//
			narrator.sayln("Removing a membership -->" + targetMembership.getMemberEmail());
			clubService.removeMembership(currentClub.getUsid(), targetMembership.getMemberEmail());
		} else {
			narrator.sayln("Remove cancelled, the member is safe. --> " + targetMembership.getMemberEmail());
		}
	}
}
