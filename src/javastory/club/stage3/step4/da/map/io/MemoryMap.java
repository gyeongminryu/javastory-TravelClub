package javastory.club.stage3.step4.da.map.io;

import java.util.LinkedHashMap;
import java.util.Map;

import javastory.club.stage3.step1.entity.board.Posting;
import javastory.club.stage3.step1.entity.board.SocialBoard;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step1.entity.club.TravelClub;

public class MemoryMap {
	//
	private static MemoryMap singletonMap;

	private Map<String,CommunityMember>  memberMap;
	private Map<String,TravelClub>  clubMap;
	private Map<String,SocialBoard> boardMap;
	private Map<String,Posting> postingMap;
	private Map<String,Integer> autoIdMap;

	private MemoryMap() {
		//본인은 생성자로 초기화하면 안됨
		this.memberMap = new LinkedHashMap<>();
		this.clubMap = new LinkedHashMap<>();
		this.boardMap = new LinkedHashMap<>();
		this.postingMap = new LinkedHashMap<>();
		this.autoIdMap = new LinkedHashMap<>();
	}

	public static MemoryMap getInstance() {
		//
		if (singletonMap == null) {
			singletonMap = new MemoryMap();
		}

		return singletonMap;
	}

	public Map<String,Integer> getAutoIdMap() {
		//
		return this.autoIdMap;
	}

	public Map<String,CommunityMember> getMemberMap() {
    //
		return this.memberMap;
	}

	public Map<String,TravelClub> getClubMap() {
		//
    return this.clubMap;
	}

	public Map<String,SocialBoard> getBoardMap() {
    //
		return this.boardMap;
	}

	public Map<String,Posting> getPostingMap() {
    //
		return this.postingMap;
	}
}
