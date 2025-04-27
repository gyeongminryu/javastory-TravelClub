package javastory.club.stage3.step4.store;

import java.util.List;

import javastory.club.stage3.step1.entity.board.Posting;

public interface PostingStore {
	//
	String create(Posting posting);
	Posting retrieve(String postingId);
	List<Posting> retrieveByBoardId(String boardId);
	List<Posting> retrieveByTitle(String title);
	void update(Posting posting);
	void delete(String posingId);

	boolean exists(String postingId);
}
