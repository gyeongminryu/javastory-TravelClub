package javastory.club.stage3.step1.entity.club;

import javastory.club.stage3.util.DateUtil;

public class ClubMembership {
	//
	private String clubId;
	private String memberEmail;
	private RoleInClub role;
	private String joinDate;

	public ClubMembership(TravelClub club, CommunityMember member) {
		//
		this.clubId = club.getUsid();
		this.memberEmail = member.getEmail();

		this.role = RoleInClub.Member;
		this.joinDate = DateUtil.today();
	}

	public ClubMembership(String clubId, String memberEmail) {
		//
		this.clubId = clubId;
		this.memberEmail = memberEmail;

		this.role = RoleInClub.Member;
		this.joinDate = DateUtil.today();
	}


	public static void main(String[] args) {
		System.out.println(ClubMembership.getSample(TravelClub.getSample(false), CommunityMember.getSample()));
	}


	public static ClubMembership getSample(TravelClub club, CommunityMember member) {
		//
		ClubMembership membership = new ClubMembership(club, member);
		membership.setRole(RoleInClub.Member);

		return membership;
	}


	public RoleInClub getRole() {
		return role;
	}

	public void setRole(RoleInClub role) {
		this.role = role;
	}

	public String getJoinDate() {
		return joinDate;
	}

	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}

	public String getClubId() {
		return clubId;
	}

	public String getMemberEmail() {
		return memberEmail;
	}


	@Override
	public String toString() {
		//
		StringBuilder builder = new StringBuilder();

		builder.append("club Id: ").append(clubId);
		builder.append(", member email: ").append(memberEmail);
		builder.append(", role: ").append(role.name());
		builder.append(", join date: ").append(joinDate);

		return builder.toString();
	}


}
