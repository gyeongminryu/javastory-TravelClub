package javastory.club.stage3.step3.ui.console;

import java.util.List;

import javastory.club.stage3.step3.logic.ServiceLogicLycler;
import javastory.club.stage3.step3.service.BoardService;
import javastory.club.stage3.step3.service.PostingService;
import javastory.club.stage3.step3.service.ServiceLycler;
import javastory.club.stage3.step3.service.dto.BoardDto;
import javastory.club.stage3.step3.service.dto.PostingDto;
import javastory.club.stage3.step3.util.NoSuchBoardException;
import javastory.club.stage3.step3.util.NoSuchClubException;
import javastory.club.stage3.step3.util.NoSuchMemberException;
import javastory.club.stage3.step3.util.NoSuchPostingException;
import javastory.club.stage3.util.ConsoleUtil;
import javastory.club.stage3.util.Narrator;
import javastory.club.stage3.util.TalkingAt;

public class PostingConsole {
	//
	private BoardDto currentBoard; //현재 board 값

	private BoardService boardService; //보드 서비스
	private PostingService postingService; //포스팅 서비스

	private ConsoleUtil consoleUtil;
	private Narrator narrator;

	public PostingConsole() {
		//
		this.boardService = ServiceLogicLycler.shareInstance().createBoardService();
		this.postingService = ServiceLogicLycler.shareInstance().createPostingService();

		this.narrator = new Narrator(this, TalkingAt.Left);
		this.consoleUtil = new ConsoleUtil(narrator);
	}




	public void findBoard() {
		//
		BoardDto boardFound = null;
		while (true) {

			//클럽 이름 받고 검증
			String clubName = consoleUtil.getValueOf("\n club name to find a board(0.Posting menu) ");
			if (clubName.equals("0")) {
				break;
			}
			try {
				//findByClubName으로 BoardDTO 얻기
				boardFound = boardService.findByClubName(clubName);
				narrator.sayln("\t > Found board: " + boardFound);
				break;
			} catch (NoSuchClubException e) {
				narrator.sayln(e.getMessage());
			}
			boardFound = null;
		}
		this.currentBoard = boardFound;
	}

	public void register() {
		//
		if (!hasCurrentBoard()) {//currentBoard가 없으면
			//
			narrator.sayln("No target board yet. Find target board first.");
			return;
		}

		while (true) {
			//포스팅 이름 얻기
			String title = consoleUtil.getValueOf("\n posting title(0.Posting menu)");
			if (title.equals("0")) {
				return;
			}

			//적은 사람 이메일 얻기
			String writerEmail = consoleUtil.getValueOf(" posting writerEmail");

			//포스팅 내용 얻기
			String contents = consoleUtil.getValueOf(" posting contents");

			try {
				//포스팅DTO 세팅
				PostingDto postingDto = new PostingDto(title, writerEmail, contents);

				//★포스팅 DTO의 usid set할건데 / register(현재 boardId, posting할 내용) > 값 받음
				postingDto.setUsid(postingService.register(currentBoard.getId(), postingDto));

				narrator.sayln("\n Registered a posting --> " + postingDto);

			} catch (NoSuchBoardException | NoSuchMemberException e) {
				narrator.sayln(e.getMessage());
			}
		}

	}










	public boolean hasCurrentBoard() {
		//
		return currentBoard != null;
	}

	public String requestCurrentBoardName() {
		//
		String clubName = null;

		if (hasCurrentBoard()) {
			clubName = currentBoard.getName();
		}

		return clubName;
	}












	public void findByBoardId() {
		//
		if (!hasCurrentBoard()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		try {
			List<PostingDto> postings = postingService.findByBoardId(currentBoard.getId());
			int index = 0;
			for (PostingDto postingDto : postings) {
				narrator.sayln(String.format("[%d] ", index) + postingDto);
				index++;
			}
		} catch (NoSuchBoardException e) {
			narrator.sayln(e.getMessage());
		}
	}

	//메뉴
	public void find() {
		//
		if (!hasCurrentBoard()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return;
		}

		PostingDto postingDto = null;
		while (true) {
			String postingId = consoleUtil.getValueOf("\n posting id to find (0.Posting menu) ");
			if (postingId.equals("0")) {
				break;
			}

			try {
				postingDto = postingService.find(postingId);
				narrator.sayln("\t > Found posting : " + postingDto);
			} catch (NoSuchPostingException e) {
				narrator.sayln(e.getMessage());
			}
		}
	}

	public PostingDto findOne() {
		//
		if (!hasCurrentBoard()) {
			//
			narrator.sayln("No target club yet. Find target club first.");
			return null;
		}

		PostingDto postingDto = null;
		while (true) {
			String postingId = consoleUtil.getValueOf("\n posting id to find (0.Posting menu) ");
			if (postingId.equals("0")) {
				break;
			}

			try {
				postingDto = postingService.find(postingId);
				narrator.sayln("\t > Found posting : " + postingDto);
				break;
			} catch (NoSuchPostingException e) {
				narrator.sayln(e.getMessage());
			}
			postingDto = null;
		}
		return postingDto;
	}

	public void modify() {
		//
		PostingDto targetPosting = findOne();
		if (targetPosting == null) {
			return;
		}

		String newTitle = consoleUtil.getValueOf("\n new posting title(0.Posting menu, Enter. no change)");
		if (newTitle.equals("0")) {
			return;
		}
		if (!newTitle.isEmpty()) {
			targetPosting.setTitle(newTitle);
		}

		String newContents = consoleUtil.getValueOf(" new posting contents(Enter. no change))");
		if (!newContents.isEmpty()) {
			targetPosting.setContents(newContents);
		}

		try {
			postingService.modify(targetPosting);
			narrator.sayln("\t > Modified Posting: " + targetPosting);
		} catch (NoSuchPostingException e) {
			narrator.sayln(e.getMessage());
		}

	}

	public void remove() {
		//
		PostingDto targetPosting = findOne();
		if (targetPosting == null) {
			return;
		}

		String confirmStr = consoleUtil.getValueOf("Remove this posting in the board? (Y:yes, N:no)");
		if (confirmStr.toLowerCase().equals("y") || confirmStr.toLowerCase().equals("yes")) {
			//
			narrator.sayln("Removing a posting --> " + targetPosting.getTitle());
			postingService.remove(targetPosting.getUsid());
		} else {
			narrator.sayln("Remove cancelled, the posting is safe. --> " + targetPosting.getTitle());
		}
	}

}
