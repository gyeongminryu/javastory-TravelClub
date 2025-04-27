package javastory.club.stage3.step3.logic;

import java.util.*;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.board.SocialBoard;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step3.logic.storage.MapStorage;
import javastory.club.stage3.step3.service.BoardService;
import javastory.club.stage3.step3.service.dto.BoardDto;
import javastory.club.stage3.step3.util.BoardDuplicationException;
import javastory.club.stage3.step3.util.NoSuchBoardException;
import javastory.club.stage3.step3.util.NoSuchMemberException;
import javastory.club.stage3.step3.util.NoSuchClubException;
import javastory.club.stage3.util.StringUtil;

public class BoardServiceLogic implements BoardService {
	//
	private Map<String,SocialBoard> boardMap;
	private Map<String,TravelClub> clubMap;

	public BoardServiceLogic() {
		//
		this.boardMap = MapStorage.getInstance().getBoardMap();
		this.clubMap = MapStorage.getInstance().getClubMap();
	}

	@Override
	public String register(BoardDto boardDto) {
		//
		String boardId = boardDto.getId();


		//1. Board Duplication -> boardId를 통해 중복되는지
		Optional.ofNullable(boardMap.get(boardId))
				.ifPresent((targetBoard) -> {
					throw new BoardDuplicationException("Board already exists in the club --> " + targetBoard.getName());
				});


		//2.NoSuchClub
		TravelClub clubFound = Optional.ofNullable(clubMap.get(boardId))
				.orElseThrow(() -> new NoSuchClubException("No such club with id --> " + boardId));

		//3.NoSuchMember
		Optional.ofNullable(clubFound.getMembershipBy(boardDto.getAdminEmail()))
				.orElseThrow(() -> new NoSuchMemberException("In the club, No such member with admin's email -->" + boardDto.getAdminEmail()));


		//위에 충족하면 전달 받은 boardDTO를 board로 만들어서 boardMap에 넣기
		SocialBoard board = boardDto.toBoard();
		boardMap.put(boardId, board);
		return boardId;//★굳이 반환해줘야 하는지..
	}

	@Override
	public BoardDto find(String boardId) {
		//
		return Optional.ofNullable(boardMap.get(boardId))
				.map(board -> new BoardDto(board))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId));
	}

	@Override
	public List<BoardDto> findByName(String boardName) {
		//
		Collection<SocialBoard> boards = boardMap.values();
		if (boards.isEmpty()) {
			throw new NoSuchBoardException("No boards in the storage.");
		}

		List<BoardDto> boardDtos = boards.stream()
				.filter(board -> board.getName().equals(boardName))
				.map(targetBoard -> new BoardDto(targetBoard))
				.collect(Collectors.toList());

		if (boardDtos.isEmpty()) {
			throw new NoSuchBoardException("No such board with name --> " + boardName);
		}

		return boardDtos;
	}

	@Override
	public BoardDto findByClubName(String clubName) {
		//
		TravelClub foundClub = null;
		for (TravelClub club : clubMap.values()) {
			if (club.getName().equals(clubName)) {
				foundClub = club;
				break;
			}
		}

		return Optional.ofNullable(foundClub)
				.map(club -> boardMap.get(club.getId()))
				.map(board -> new BoardDto(board))
				.orElseThrow(() -> new NoSuchClubException("No such club with name: " + clubName));
	}

	@Override
	public void modify(BoardDto boardDto) {
		//
		String boardId = boardDto.getId();

		SocialBoard targetBoard = Optional.ofNullable(boardMap.get(boardId))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardDto.getId()));

		if (StringUtil.isEmpty(boardDto.getName())){
			boardDto.setName(targetBoard.getName());
		}
		if (StringUtil.isEmpty(boardDto.getAdminEmail())){
			boardDto.setAdminEmail(targetBoard.getAdminEmail());
		} else { //★★★★★★★★★★★★★★★★★★★★★★★★★★★★
			Optional.ofNullable(clubMap.get(boardDto.getClubId())) //클럽 가져와서
					.map(club -> club.getMembershipBy(boardDto.getAdminEmail())) //클럽의 어드민 이메일을 가진 멤버쉽
					.orElseThrow(() -> new NoSuchMemberException("In the club, No such member with admin's email -->" + boardDto.getAdminEmail()));
		}

		boardMap.put(boardId, boardDto.toBoard());
	}

	@Override
	public void remove(String boardId) {
		//
		Optional.ofNullable(boardMap.get(boardId))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId));
		boardMap.remove(boardId);
	}
}
