package javastory.club.stage3.step4.ui.console;

import javastory.club.stage3.step1.entity.club.RoleInClub;
import javastory.club.stage3.step4.logic.ServiceLogicLycler;
import javastory.club.stage3.step4.service.ClubService;
import javastory.club.stage3.step4.service.ServiceLycler;
import javastory.club.stage3.step4.service.dto.ClubMembershipDto;
import javastory.club.stage3.step4.service.dto.TravelClubDto;
import javastory.club.stage3.step4.util.MemberDuplicationException;
import javastory.club.stage3.step4.util.NoSuchClubException;
import javastory.club.stage3.step4.util.NoSuchMemberException;
import javastory.club.stage3.util.ConsoleUtil;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.StringUtil;
import javastory.club.stage3.util.TalkingAt;

import java.util.Optional;

public class ClubMembershipConsole {
	//
	private TravelClubDto currentClub;

	private ClubService clubService;

	private ConsoleUtil consoleUtil;
	private Narrator narrator;

	public ClubMembershipConsole() {
		//
		ServiceLycler serviceFactory = ServiceLogicLycler.shareInstance();
		this.clubService = serviceFactory.createClubService();

		this.narrator = new Narrator(this, TalkingAt.Left);
		this.consoleUtil = new ConsoleUtil(narrator);
	}

	public boolean hasCurrentClub() {
		//
		return Optional.ofNullable(currentClub).isPresent();
	}

	public String requestCurrentClubName() {
		//
		String clubName = null;

		if (hasCurrentClub()) {
			clubName = currentClub.getName();
		}

		return clubName;
	}

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

	public void add() {
		//
		if (!hasCurrentClub()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		while(true) {
			String email = consoleUtil.getValueOf("\n member's email to add(0.Member menu)");
			if (email.equals("0")) {
				return;
			}


			String memberRole = consoleUtil.getValueOf(" President|Member");



			try {
				ClubMembershipDto clubMembershipDto = new ClubMembershipDto(currentClub.getUsid(), email);
				clubMembershipDto.setRole(RoleInClub.valueOf(memberRole));

				//★★★★안 구현되어있음 확인해보기
				//아무도 없으므로 무조건 PRESIDENT로 추가
				if (currentClub.getMembershipList().isEmpty()) {
					clubMembershipDto.setRole(RoleInClub.President);
				}

       			 clubService.addMembership(clubMembershipDto);
				narrator.sayln(String.format("\n Add a member[email:%s] in club[name:%s]", email, currentClub.getName()));

			} catch (MemberDuplicationException | NoSuchClubException e) {
				narrator.sayln(e.getMessage());
			} catch (IllegalArgumentException e) {
				narrator.sayln("The Role in club should be president or member.");
			}
		}

	}

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

		String newRole = consoleUtil.getValueOf("\n new President|Member(0.Membership menu, Enter. no change)");//엔터 누르면 안 바뀜...
		if (newRole.equals("0")) {
			return;
		}

		if (!StringUtil.isEmpty(newRole)) {
			targetMembership.setRole(RoleInClub.valueOf(newRole));
		}

		String clubId = targetMembership.getClubId();

		//#try-catch 안되어있음

		try{
			clubService.modifyMembership(clubId, targetMembership);
			ClubMembershipDto modifiedMembership = clubService.findMembershipIn(clubId, targetMembership.getMemberEmail());
			narrator.sayln("\t > Modified membership information: " + modifiedMembership);

		}catch(NoSuchClubException|NoSuchMemberException e){
			narrator.sayln(e.getMessage());
		}

	}

	//try- catch어디서 해야하는지 확인
	public void remove() {
		//club이 있는지 확인
		if (!hasCurrentClub()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		//삭제할 객체 찾기
		ClubMembershipDto targetMembership = findOne();
		if (targetMembership == null) {
			return;
		}

		//확인 받음
		String confirmStr = consoleUtil.getValueOf("\n Remove this member in the club? (Y:yes, N:no)");
		if (confirmStr.toLowerCase().equals("y") || confirmStr.toLowerCase().equals("yes")) {
			//
			narrator.sayln("Removing a membership -->" + targetMembership.getMemberEmail());


			//멤버쉽은 삭제할 때 clubid와 memberEmail 모두 전달
			clubService.removeMembership(currentClub.getUsid(), targetMembership.getMemberEmail());
		} else {
			narrator.sayln("Remove cancelled, the member is safe. --> " + targetMembership.getMemberEmail());
		}
	}
}
