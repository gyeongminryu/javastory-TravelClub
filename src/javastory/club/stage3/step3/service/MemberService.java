package javastory.club.stage3.step3.service;

import java.util.List;

import javastory.club.stage3.step1.util.InvalidEmailException;
import javastory.club.stage3.step3.service.dto.MemberDto;
import javastory.club.stage3.step4.service.dto.ClubMembershipDto;

public interface MemberService {
	//
	void register(MemberDto memberDto) throws InvalidEmailException;
	MemberDto find(String memberId);
	List<MemberDto> findByName(String memberName);
	void modify(MemberDto memberDto) throws InvalidEmailException;
	void remove(String memberId);

  List<ClubMembershipDto> findAllMembershipsIn(String memberId);
  void removeMembership(String clubId, String memberId);
  void removeAllMembershipsOfClub(String memberId, List<ClubMembershipDto> clubMembershipDtos);
}
