package javastory.club.stage3.step3.logic;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.board.Posting;
import javastory.club.stage3.step1.entity.board.SocialBoard;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step3.logic.storage.MapStorage;
import javastory.club.stage3.step3.service.PostingService;
import javastory.club.stage3.step3.service.dto.PostingDto;
import javastory.club.stage3.step3.util.NoSuchBoardException;
import javastory.club.stage3.step3.util.NoSuchMemberException;
import javastory.club.stage3.step3.util.NoSuchPostingException;
import javastory.club.stage3.util.StringUtil;


public class PostingServiceLogic implements PostingService {
	//
	private Map<String, SocialBoard> boardMap;
	private Map<String, Posting> postingMap;
	private Map<String, TravelClub> clubMap;

	public PostingServiceLogic() {
		//
		this.boardMap = MapStorage.getInstance().getBoardMap();
		this.postingMap = MapStorage.getInstance().getPostingMap();
		this.clubMap = MapStorage.getInstance().getClubMap();
	}

	@Override
	public String register(String boardId, PostingDto postingDto) {
		//
		Optional.ofNullable(clubMap.get(boardId)).
				map(club -> club.getMembershipBy(postingDto.getWriterEmail()))
				.orElseThrow(() -> new NoSuchMemberException("In the club, No such member with admin's email -->" + postingDto.getWriterEmail()));

		Posting newPosting = Optional.ofNullable(boardMap.get(boardId))
				.map(foundBoard -> postingDto.toPostingIn(foundBoard))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId));

		postingMap.put(newPosting.getId(), newPosting);
		return newPosting.getId();
	}

	@Override
	public PostingDto find(String postingId) {
		//
    Posting foundPosting = Optional.ofNullable(postingMap.get(postingId))
      .orElseThrow(() -> new NoSuchPostingException("No such posting with id --> " + postingId));

    foundPosting.setReadCount(foundPosting.getReadCount() + 1);

    return new PostingDto(foundPosting);
	}

	@Override
	public List<PostingDto> findByBoardId(String boardId) {
		//
		Optional.ofNullable(boardMap.get(boardId))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId));

		return postingMap.values().stream()
				.filter(posting -> posting.getBoardId().equals(boardId))
				.map(targetPosting -> new PostingDto(targetPosting))
				.collect(Collectors.toList());
	}

	@Override
	public void modify(PostingDto postingDto) {
		//
		String postingId = postingDto.getUsid();

		Posting targetPosting = Optional.ofNullable(postingMap.get(postingId))
				.orElseThrow(() -> new NoSuchMemberException("No such posting with id --> " + postingId));

		// modify only if user inputs some value.
		if (StringUtil.isEmpty(postingDto.getTitle())) {
			postingDto.setTitle(targetPosting.getTitle());
		}
		if (StringUtil.isEmpty(postingDto.getContents())) {
			postingDto.setContents(targetPosting.getContents());
		}

		Posting newPosting = postingDto.toPostingIn(postingDto.getUsid(), targetPosting.getBoardId());

		postingMap.put(postingId, newPosting);
	}

	@Override
	public void remove(String postingId) {
		//
		Optional.ofNullable(postingMap.get(postingId))
				.orElseThrow(() -> new NoSuchMemberException("No such posting with id --> " + postingId));

		postingMap.remove(postingId);
	}

	@Override
	public void removeAllIn(String boardId) {
		//
		postingMap.values().stream()
				.forEach(posting -> postingMap.remove(posting.getId()));
	}
}
