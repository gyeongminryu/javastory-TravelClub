package javastory.club.stage3.step4.da.map;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step4.da.map.io.MemoryMap;
import javastory.club.stage3.step4.store.MemberStore;
import javastory.club.stage3.step4.util.MemberDuplicationException;

public class MemberMapStore implements MemberStore {
	//
	private Map<String,CommunityMember> memberMap;

	public MemberMapStore() {
		//
		this.memberMap = MemoryMap.getInstance().getMemberMap();
	}


	@Override
	public String create(CommunityMember member) {

		//멤버 업데이트
		memberMap.put(member.getId(), member);

		//멤버 id 반환
		return member.getId();
	}

	@Override
	public CommunityMember retrieve(String memberId) {
		//
		return memberMap.get(memberId);
	}

	@Override
	public List<CommunityMember> retrieveByName(String name) {
		//
		return memberMap.values().stream()
				.filter(member -> member.getName().equals(name))
				.collect(Collectors.toList());
	}

	@Override
	public void update(CommunityMember member) {
		//
		memberMap.put(member.getId(), member);
	}

	@Override
	public void delete(String memberId) {
		//
		memberMap.remove(memberId);
	}

	@Override
	public boolean exists(String memberId) {
		//
		return Optional.ofNullable(memberMap.get(memberId)).isPresent();
	}
}

