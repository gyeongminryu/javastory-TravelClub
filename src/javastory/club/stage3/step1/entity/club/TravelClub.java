package javastory.club.stage3.step1.entity.club;

import java.util.ArrayList;
import java.util.List;

import javastory.club.stage3.step1.entity.AutoIdEntity;
import javastory.club.stage3.util.DateUtil;

public class TravelClub implements AutoIdEntity {
	//
	private static final int MINIMUM_NAME_LENGTH =  3;
	private static final int MINIMUM_INTRO_LENGTH =  10;
	public static final String ID_FORMAT = "%05d";

	private String usid; 		// auto incremental style
	private String name;
	private String intro;
	private String foundationDay;

	private String boardId;
	private List<ClubMembership> membershipList;

	private TravelClub() {
		this.membershipList = new ArrayList<ClubMembership>();
	}

	public TravelClub(String name, String intro) {
		//
		this();
		this.setName(name); //stage2는 this.name = name
		this.setIntro(intro);//stage2는 this.intro = intro
		this.foundationDay = DateUtil.today();//오늘 날짜 format해서 반환
	}


	@Override
	public String getId() {
		return usid;
	}//현재 usid 가져옴

	@Override
	public String getIdFormat() {
		return ID_FORMAT;
	} //현재 ID_FORMAT 가져옴

	@Override
	public void setAutoId(String autoId) {
		//
		this.usid = autoId; //현재 usid에 id set
	}

	@Override
	public String toString() {
		//
		StringBuilder builder = new StringBuilder();

		builder.append("TravelClub id: ").append(usid);
		builder.append(", name: ").append(name);
		builder.append(", intro: ").append(intro);
		builder.append(", foundation day: ").append(foundationDay);

		return builder.toString();
	}

	public static TravelClub getSample(boolean keyIncluded) {
		//
		String name = "JavaTravelClub";
		String intro = "Travel club to the Java island.";
		TravelClub club = new TravelClub(name, intro);

		if (keyIncluded) {
			int sequence = 21;
			club.setAutoId(String.format(ID_FORMAT, sequence));
		}

		return club;
	}

	//email과 일치하는 클럽 멤버쉽 있으면 반환
	public ClubMembership getMembershipBy(String email) {

		//이메일 -> 만약 이메일이 null이거나 비어있으면 null 반환
		if (email == null || email.isEmpty()) {
			return null;
		}

		//
		for (ClubMembership clubMembership : this.membershipList) {
			if (email.equals(clubMembership.getMemberEmail())){
				return clubMembership;
			}
		}
		return null;
	}

	public List<ClubMembership> getMembershipList() {
		return this.membershipList;
	}

  public void setMembershipList(List<ClubMembership> membershipList) {
    this.membershipList = membershipList;
  }

  public String getUsid() {
		return usid;
	}

	public void setUsid(String usid) {
		this.usid = usid;
	}

	public String getName() {
		return name;
	}

	public String getIntro() {
		return intro;
	}

	public String getFoundationDay() {
		return foundationDay;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

	public void setName(String name) {
		//
		if (name.length() < MINIMUM_NAME_LENGTH) {
			//
			throw new IllegalArgumentException("Name should be longer than " + MINIMUM_NAME_LENGTH);
		}

		this.name = name;
	}

	public void setIntro(String intro) {
		//
		if (intro.length() < MINIMUM_INTRO_LENGTH) {
			//
			throw new IllegalArgumentException("Intro should be longer than " + MINIMUM_INTRO_LENGTH);
		}

		this.intro = intro;
	}

	public void setFoundationDay (String foundationDay) {
		//
		this.foundationDay = foundationDay;
	}

}
