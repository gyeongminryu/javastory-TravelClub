package javastory.club.stage3.step4.da.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.board.Posting;
import javastory.club.stage3.step4.da.map.io.MemoryMap;
import javastory.club.stage3.step4.store.PostingStore;
import javastory.club.stage3.step4.util.PostingDuplicationException;

public class PostingMapStore implements PostingStore {
	//
	private Map<String,Posting> postingMap;

	public PostingMapStore() {
		//
		this.postingMap = MemoryMap.getInstance().getPostingMap();
	}

	@Override
	public String create(Posting posting) {
		//
		Optional.ofNullable(postingMap.get(posting.getId()))
				.ifPresent((postingId) -> {
					throw new PostingDuplicationException("Posting already exists with postingId --> " + postingId);
				});

		postingMap.put(posting.getId(), posting);
		return posting.getId();
	}

	//find
	@Override
	public Posting retrieve(String postingId) {
		//
		return postingMap.get(postingId);
	}

	@Override
	public List<Posting> retrieveByBoardId(String boardId){
		//postingMap의 value 모두 가져와서 -> 만약 boardId가 같으면 -> List로 반환
		return postingMap.values().stream()
				.filter(posting->posting.getBoardId().equals(boardId))
				.collect(Collectors.toList());
	}

	@Override
	public List<Posting> retrieveByTitle(String title) {
		//
		return postingMap.values().stream()
				.filter(posting->posting.getTitle().equals(title))
				.collect(Collectors.toList());
	}

	@Override
	public void update(Posting posting) {
		//
		postingMap.put(posting.getId(), posting);
	}

	@Override
	public void delete(String postingId) {
		//
		postingMap.remove(postingId);
	}

	@Override
	public boolean exists(String postingId) {
		//
		return Optional.ofNullable(postingMap.get(postingId)).isPresent();
	}
}
