package javastory.club.stage3.step3.service;

import java.util.List;

import javastory.club.stage3.step3.service.dto.BoardDto;

public interface BoardService {
	//
	String register(BoardDto boardDto);
	BoardDto find(String boardId);
	List<BoardDto> findByName(String boardName);
	BoardDto findByClubName(String clubName);
	void modify(BoardDto board);
	void remove(String boardId);
}
