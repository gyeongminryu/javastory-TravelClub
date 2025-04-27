package javastory.club.stage3.step4.store;

public interface ClubStoreLycler {
	//
	MemberStore requestMemberStore();
	ClubStore requestClubStore();
	BoardStore requestBoardStore();
	PostingStore requestPostingStore();
}