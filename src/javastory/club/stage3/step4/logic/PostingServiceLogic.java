package javastory.club.stage3.step4.logic;

import java.security.Provider.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.board.Posting;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step4.service.PostingService;
import javastory.club.stage3.step4.service.dto.PostingDto;
import javastory.club.stage3.step4.store.BoardStore;
import javastory.club.stage3.step4.store.ClubStore;
import javastory.club.stage3.step4.store.ClubStoreLycler;
import javastory.club.stage3.step4.store.PostingStore;
import javastory.club.stage3.step4.util.NoSuchBoardException;
import javastory.club.stage3.step4.util.NoSuchClubException;
import javastory.club.stage3.step4.util.NoSuchMemberException;
import javastory.club.stage3.step4.util.NoSuchPostingException;
import javastory.club.stage3.util.StringUtil;
import javastory.club.stage3.step4.da.map.ClubStoreMapLycler;


public class PostingServiceLogic implements PostingService {
	//
	private BoardStore boardStore;
	private PostingStore postingStore;
	private ClubStore clubStore;

	public PostingServiceLogic() {
		//개선
		ClubStoreLycler lycler = ClubStoreMapLycler.getInstance(); //왜 Map으로 받는지
		this.boardStore = lycler.requestBoardStore();
		this.postingStore = lycler.requestPostingStore();
		this.clubStore = lycler.requestClubStore();
	}

	//★
	@Override
	public String register(String boardId, PostingDto postingDto) {

		//클럽부터 있는지 확인
		TravelClub club = Optional.ofNullable(clubStore.retrieve(boardId))//boardId의 클럽가져오기 -> currentBoard에서 가져오고 있는데 -> currentBoard가 도중에 삭제되면...
			.orElseThrow(()-> new NoSuchClubException("no such club"));


		Optional.ofNullable(club.getMembershipBy(postingDto.getWriterEmail()))
			.orElseThrow(() -> new NoSuchMemberException("In the club, No such member with email -->" + postingDto.getWriterEmail()));



		//boardId의 board가 존재하면 postingStore.create가 진행됨
		return Optional.ofNullable(boardStore.retrieve(boardId))
				.map(board -> postingStore.create(postingDto.toPostingIn(board))) //이 시점에 post가 postId 생성
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId)); //얘는 boardException이니까 괜찮음
	}

	//boardId의 클럽에 writerEmail의 멤버쉽이 존재하는지
		//Optional.ofNullable(clubStore.retrieve(boardId))//boardId의 클럽가져오기
	//.map(clubFound -> club.getMembershipBy(postingDto.getWriterEmail())) //클럽의 이메일에 해당하는 멤버십
	//	.orElseThrow(() -> new NoSuchMemberException("In the club, No such member with email -->" + postingDto.getWriterEmail()));



	//찾기 개선
	@Override
	public PostingDto find(String postingId) {
		//posting있는지 확인
    Posting foundPosting = Optional.ofNullable(postingStore.retrieve(postingId))
      .orElseThrow(() -> new NoSuchPostingException("No such posting with id --> " + postingId));

	//조회수 증가 -> posting에
    foundPosting.setReadCount(foundPosting.getReadCount() + 1);

	//조회수증가 //posting 저장 -> post
	postingStore.update(foundPosting);//#추가 조회수

    return new PostingDto(foundPosting); //postingDto로 반환
	}

	@Override
	public List<PostingDto> findByBoardId(String boardId) {
		//board가 존재하는지 확인
		Optional.ofNullable(boardStore.retrieve(boardId))
				.orElseThrow(() -> new NoSuchBoardException("No such board with id --> " + boardId));

		//#post를 boardId로 가져오고
		List<Posting> post =  postingStore.retrieveByBoardId(boardId);


		//postList가 비어있는지 확인
		if(post==null||post.isEmpty()){
			throw new NoSuchPostingException("no posting");
		}


		//post->DTO->LIST
			return post.stream()
				.map(posting -> new PostingDto(posting)).collect(Collectors.toList());
	}

	/*
	return postingStore.retrieveByBoardId(boardId).stream()
				.map(posting -> new PostingDto(posting)).collect(Collectors.toList());
	 */

	@Override
	public void modify(PostingDto postingDto) {
		//
		String postingId = postingDto.getUsid();

		Posting targetPosting = Optional.ofNullable(postingStore.retrieve(postingId))
				.orElseThrow(() -> new NoSuchPostingException("No such posting with id --> " + postingId));

		// modify only if user inputs some value.
		if (StringUtil.isEmpty(postingDto.getTitle())) {
			postingDto.setTitle(targetPosting.getTitle());
		}
		if (StringUtil.isEmpty(postingDto.getContents())) {
			postingDto.setContents(targetPosting.getContents());
		}

		postingStore.update(postingDto.toPostingIn(postingId, targetPosting.getBoardId())); //여기선 postingId와 boardId 넣어줌 -> 확인
	}

	@Override
	public void remove(String postingId) {
		//
		if (!postingStore.exists(postingId)) {
			throw new NoSuchPostingException("No such posting with id --> " + postingId);
		}

		postingStore.delete(postingId);
	}

	@Override
	public void removeAllIn(String boardId) {
		//
		postingStore.retrieveByBoardId(boardId).stream()
				.forEach(posting->postingStore.delete(posting.getId()));
	}
}
