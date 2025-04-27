package javastory.club.stage3.step4.store;

import java.util.List;

import javastory.club.stage3.step1.entity.board.SocialBoard;

public interface BoardStore {
	//
	String create(SocialBoard board);
	SocialBoard retrieve(String boardId);
	List<SocialBoard> retrieveByName(String boardId);
	void update(SocialBoard board);
	void delete(String boardId);
	
	boolean exists(String boardId);
}