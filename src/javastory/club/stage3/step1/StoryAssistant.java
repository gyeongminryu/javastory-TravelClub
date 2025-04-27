package javastory.club.stage3.step1;

import java.util.List;

import javastory.club.stage3.step1.entity.board.Posting;
import javastory.club.stage3.step1.entity.board.SocialBoard;
import javastory.club.stage3.step1.entity.club.ClubMembership;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.TalkingAt;

public class StoryAssistant {
	//
	private Narrator narrator;

	public StoryAssistant() {
		//
		this.narrator = new Narrator(this, TalkingAt.Left);
	}

	public static void main(String[] args) {
		//
		StoryAssistant assistant = new StoryAssistant();
		assistant.showMemberDemo();
		assistant.showBoardDemo();
	}

	private void showMemberDemo() {
		//
		TravelClub club = TravelClub.getSample(true);
		CommunityMember member = CommunityMember.getSample();
		ClubMembership membership = new ClubMembership(club, member);

		narrator.sayln("> club: " + club.toString());
		narrator.sayln("> member: " + member.toString());
		narrator.sayln("> membership: " + membership.toString());
	}



	private void showBoardDemo() {
		//
		TravelClub club = TravelClub.getSample(true);
		SocialBoard board = SocialBoard.getSample(club);
		List<Posting> postings = Posting.getSamples(board);

		narrator.sayln("> board: " + board.toString());
		narrator.say("> posting: " + postings.toString());
	}
}
