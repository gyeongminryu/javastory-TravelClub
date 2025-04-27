package javastory.club.stage3.step4.service;

import java.util.List;

import javastory.club.stage3.step4.service.dto.ClubMembershipDto;
import javastory.club.stage3.step4.service.dto.TravelClubDto;


public interface ClubService {
	void register(TravelClubDto clubDto);
	TravelClubDto findClub(String clubId); 
	TravelClubDto findClubByName(String name);
	void modify(TravelClubDto clubDto);
	void remove(String clubId);

	void removeAllMembershipsOfMember(String clubId, List<ClubMembershipDto> clubMembershipDtos);


	void addMembership(ClubMembershipDto membershipDto);
	ClubMembershipDto findMembershipIn(String clubId, String memberId);
	List<ClubMembershipDto> findAllMembershipsIn(String clubId);
	void modifyMembership(String clubId, ClubMembershipDto member);
	void removeMembership(String clubId, String memberId);
}
