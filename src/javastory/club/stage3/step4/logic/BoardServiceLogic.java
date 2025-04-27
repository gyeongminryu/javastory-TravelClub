package javastory.club.stage3.step4.logic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.board.SocialBoard;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step4.service.BoardService;
import javastory.club.stage3.step4.service.dto.BoardDto;
import javastory.club.stage3.step4.store.BoardStore;
import javastory.club.stage3.step4.store.ClubStore;
import javastory.club.stage3.step4.util.BoardDuplicationException;
import javastory.club.stage3.step4.util.NoSuchBoardException;
import javastory.club.stage3.step4.util.NoSuchClubException;
import javastory.club.stage3.step4.util.NoSuchMemberException;
import javastory.club.stage3.util.StringUtil;
import javastory.club.stage3.step4.da.map.ClubStoreMapLycler;

public class BoardServiceLogic implements BoardService {
	//
	private BoardStore boardStore;
	private ClubStore clubStore;

	public BoardServiceLogic() {
		//
		this.boardStore = ClubStoreMapLycler.getInstance().requestBoardStore();
		this.clubStore = ClubStoreMapLycler.getInstance().requestClubStore();
	}

	@Override
	public String register(BoardDto boardDto) { //여긴 그냥 void 쓰기
		//
		String boardId = boardDto.getId(); //클럽 id

		//board가 있는지 확인
		Optional.ofNullable(boardStore.retrieve(boardId))
				.ifPresent((boardFound) -> {
					throw new BoardDuplicationException("Board already exists in the club --> " + boardFound.getName());
				});


		//클럽 있는지 확인
		TravelClub clubFound = Optional.ofNullable(clubStore.retrieve(boardId))
				.orElseThrow(() -> new NoSuchClubException("No such club with id --> " + boardId));

		return Optional.ofNullable(clubFound.getMembershipBy(boardDto.getAdminEmail()))
				.map(adminEmail -> boardStore.create(boardDto.toBoard())) //★★★★★여기선 만약 존재하면 안의 내용이 실행되는 것임
				.orElseThrow(() -> new NoSuchMemberException("In the club, No such member with admin's email --> " + boardDto.getAdminEmail()));
	}

	@Override
	public BoardDto find(String boardId) {
		//
		return Optional.ofNullable(boardStore.retrieve(boardId))
				.map(board -> new BoardDto(board))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId));
	}

	@Override
	public List<BoardDto> findByName(String boardName) {
		List<SocialBoard> boardList = boardStore.retrieveByName(boardName);

		if(boardList.isEmpty()){
			throw new NoSuchBoardException("no such board");
		}


		return boardList.stream()
			.map(board-> new BoardDto(board))
			.collect(Collectors.toList());
	}

	@Override //-> List
	public BoardDto findByClubName(String clubName) {

		//클럽찾기 -> clubId로 board 찾기
		TravelClub club = clubStore.retrieveByName(clubName);

		String boardId = club.getBoardId();

		SocialBoard board = Optional.ofNullable(boardStore.retrieve(boardId))
			.orElseThrow(()-> new NoSuchBoardException("no such board"));

		return new BoardDto(board);
	}

	/*
	return Optional.ofNullable(clubStore.retrieveByName(clubName)) //클럽 얻어오기
				.map(club -> boardStore.retrieve(club.getId()))
				.map(board -> new BoardDto(board))
				.orElseThrow(() -> new NoSuchClubException("No such club with name --> " + clubName));
	 */




	@Override
	public void modify(BoardDto boardDto) {
		//수정할 값 있나확인
		SocialBoard targetBoard = Optional.ofNullable(boardStore.retrieve(boardDto.getId()))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardDto.getId()));

		if (StringUtil.isEmpty(boardDto.getName())) {
			boardDto.setName(targetBoard.getName());
		}
		if (StringUtil.isEmpty(boardDto.getAdminEmail())) {

			boardDto.setAdminEmail(targetBoard.getAdminEmail());
		} else {
			Optional.ofNullable(clubStore.retrieve(boardDto.getClubId()))
                    .map(club -> club.getMembershipBy(boardDto.getAdminEmail()))
                    .orElseThrow(() -> new NoSuchMemberException("In the club, No such member with admin's email -->" + boardDto.getAdminEmail()));
		}

		boardStore.update(boardDto.toBoard());
	}

	@Override
	public void remove(String boardId) {
		//
		if (!boardStore.exists(boardId)) {
			throw new NoSuchBoardException("No such board with id --> " + boardId);
		}

		boardStore.delete(boardId);
	}
}
