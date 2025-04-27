package javastory.club.stage3.step4.da.map;

import javastory.club.stage3.step4.store.BoardStore;
import javastory.club.stage3.step4.store.ClubStore;
import javastory.club.stage3.step4.store.ClubStoreLycler;
import javastory.club.stage3.step4.store.MemberStore;
import javastory.club.stage3.step4.store.PostingStore;

public class ClubStoreMapLycler implements ClubStoreLycler {
	//
	private static ClubStoreLycler lycler;
	private ClubStore clubStore;


	//클럽 store여기 안
	private ClubStoreMapLycler() {
    //

	}

	//여기 synchronized하나만 붙이는건?
	public static ClubStoreLycler getInstance() {
		//
		if (lycler == null) {
			lycler = new ClubStoreMapLycler();
		}

		return lycler;
	}


	@Override
	public ClubStore requestClubStore() {
		if (clubStore == null) {
			clubStore = new ClubMapStore();
		}
		return clubStore;
	}


	@Override
	public MemberStore requestMemberStore() {
		return new MemberMapStore();
	}



	@Override
	public BoardStore requestBoardStore() {
		//
		return new BoardMapStore();
	}

	@Override
	public PostingStore requestPostingStore() {
		return new PostingMapStore();//하나로 합친다?
	}
}
