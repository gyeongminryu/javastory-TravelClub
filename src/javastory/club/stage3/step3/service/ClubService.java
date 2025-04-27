package javastory.club.stage3.step3.service;

import  java.util.List;

import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step3.service.dto.ClubMembershipDto;
import javastory.club.stage3.step3.service.dto.TravelClubDto;

public interface ClubService {
	//
	void register(TravelClubDto clubDto);
	TravelClubDto findClub(String clubId);
	TravelClubDto findClubByName(String name);
	void modify(TravelClubDto clubDto);
	void remove(String clubId);


	//serviceLogic에서 사용
	List<ClubMembershipDto> findAllMembershipsIn(String clubId);



	void addMembership(ClubMembershipDto membershipDto);
	ClubMembershipDto findMembershipIn(String clubId, String memberId);
	void modifyMembership(String clubId, ClubMembershipDto membershipDto);
	void removeMembership(String clubId, String memberId);
  void removeAllMembershipsOfMember(String clubId, List<ClubMembershipDto> clubMembershipDtos);
  void checkPresidentRole(TravelClub club, String memberId);
}
