package javastory.club.stage3.step4.logic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.club.ClubMembership;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step1.entity.club.RoleInClub;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step4.service.ClubService;
import javastory.club.stage3.step4.service.dto.ClubMembershipDto;
import javastory.club.stage3.step4.service.dto.TravelClubDto;
import javastory.club.stage3.step4.store.ClubStore;
import javastory.club.stage3.step4.store.MemberStore;
import javastory.club.stage3.step4.util.*;
import javastory.club.stage3.util.StringUtil;
import javastory.club.stage3.step4.da.map.ClubStoreMapLycler;

public class ClubServiceLogic implements ClubService {
	//
	private ClubStore clubStore;
	private MemberStore memberStore;

	public ClubServiceLogic() {
		//
		this.clubStore = ClubStoreMapLycler.getInstance().requestClubStore();
		this.memberStore = ClubStoreMapLycler.getInstance().requestMemberStore();
	}

	@Override
	public void register(TravelClubDto clubDto) { //등록할 DTO 전달 받음
		//클럽 이름 중복되었는지 확인 - 복붙하기
		Optional.ofNullable(clubStore.retrieveByName(clubDto.getName()))
			.ifPresent(club-> {throw new ClubDuplicationException("club already exists");});


		//아니면 등록 -> autoId값 반환
		String clubId =  clubStore.create(clubDto.toTravelClub());
		clubDto.setUsid(clubId);
	}



	@Override
	public TravelClubDto findClubByName(String name) {
		//
		return Optional.ofNullable(clubStore.retrieveByName(name)) //복붙
			.map(club -> new TravelClubDto(club))
			.orElseThrow(() -> new NoSuchClubException("No such club with name: " + name));
	}



	@Override
	public TravelClubDto findClub(String clubId) {


		return Optional.ofNullable(clubStore.retrieve(clubId))
		.map(club -> new TravelClubDto(club))
			.orElseThrow(() -> new NoSuchClubException("No such club with id: " + clubId));
	}


	@Override
	public void modify(TravelClubDto clubDto) {

		TravelClub club = Optional.ofNullable(clubStore.retrieve(clubDto.getUsid())) //복붙
			.orElseThrow(()-> new NoSuchClubException(""));


		//클럽 이름 중복
		Optional.ofNullable(clubStore.retrieveByName(clubDto.getName()))
			.ifPresent(clubFound->{throw new ClubDuplicationException("club exists");});

		//객체 값 입력 x
		if(StringUtil.isEmpty(clubDto.getName())){
			clubDto.setName(club.getName());
		}

		if(StringUtil.isEmpty(clubDto.getIntro())){
			clubDto.setIntro(club.getIntro());
		}

		clubStore.update(clubDto.toTravelClub());


//
//		//1. 수정할 객체 존재?
//		TravelClub targetClub = Optional.ofNullable(clubStore.retrieve(clubDto.getUsid()))
//			.orElseThrow(() -> new NoSuchClubException("No such club with id: " + clubDto.getUsid()));
//
//
//		//2. 클럽 이름 중복 체크 -> 아무것도 입력안했으면? 걍 넘기고
//		Optional.ofNullable(clubStore.retrieveByName(clubDto.getName())) //이름 중복 검사
//			.ifPresent(club -> {
//				throw new ClubDuplicationException("Club already exists with name:" + clubDto.getName());
//			});
//
//
//		//3. 전달 받은 값 없을 경우 대체
//		if (StringUtil.isEmpty(clubDto.getName())) {
//			clubDto.setName(targetClub.getName());
//		}
//
//		if (StringUtil.isEmpty(clubDto.getIntro())) {
//			clubDto.setIntro(targetClub.getIntro());
//		}
//
//
//		//전달 받은 값 -> TravelClub으로 만듬
//		clubStore.update(clubDto.toTravelClub());

	}


	@Override
	public void remove(String clubId) { //clubRemove할 떄 exist
		//삭제하려는 클럽 있는지 확인 -> ★ 중복되지 않는지?

		//삭제하려는 클럽 존재하나?
		if (!clubStore.exists(clubId)) {//생각
			throw new NoSuchClubException("No such club with id: " + clubId);
		}

		//삭제할 clubId의 모든 멤버쉽 리스트 찾아서 반환
		List<ClubMembershipDto> membershipList = findAllMembershipsIn(clubId);

		//member에서 멤버쉽 삭제하기
		removeAllMembershipsOfMember(clubId, membershipList);


	    clubStore.delete(clubId); //클럽 자체를 삭제
    }



	//club객체 -> club의 멤버 리스트 -> Dto-> List
	@Override
	public List<ClubMembershipDto> findAllMembershipsIn(String clubId) {
		//

		//클럭 객체 찾기
		TravelClub club = clubStore.retrieve(clubId);

		return club.getMembershipList().stream()
			.map(membership -> new ClubMembershipDto(membership))
			.collect(Collectors.toList());
	}


	//clubid에 해당하는 멤버쉽
	@Override
	public void removeAllMembershipsOfMember(String clubId, List<ClubMembershipDto> clubMembershipDtos) {


		//전달받은 (clubId가 일치하는 멤버쉽 리스트의) memberEmail 가져옴
		List<String> memberEmails = clubMembershipDtos.stream()
			.map(membership -> membership.getMemberEmail()) //★
			.collect(Collectors.toList());



		//멤버 이메일은 forEach로 돌리기
		memberEmails.forEach(email -> {

			//멤버 찾기
			CommunityMember foundMember = memberStore.retrieve(email);

			//멤버의 멤버쉽 리스트 중 clubId가 같지 않은 것만 선별해서 가져옴
			List<ClubMembership> updatedMemberships = foundMember.getMembershipList().stream()
				.filter(membership -> !membership.getClubId().equals(clubId))
				.collect(Collectors.toList());


			//멤버의 멤버쉽 리스트 새로 설정
			foundMember.setMembershipList(updatedMemberships);

			//member 객체 업데이트
			memberStore.update(foundMember); //update
		});
	}
















	@Override
	public void removeMembership(String clubId, String memberId) {
		//클럽
		TravelClub club = clubStore.retrieve(clubId); //nullable 추가
		//멤버
		CommunityMember member = memberStore.retrieve(memberId);//nullable 추가

		//클럽의 멤버쉽 중 memberId가 일치하는 멤버쉽
		ClubMembership membership = getMembershipOfClub(club, memberId);

		//클럽 멤버쉽 리스트에서 제거
		//클럽 업데이트


	}

	//private
	private ClubMembership getMembershipOfClub(TravelClub club, String memberEmail) {
		return club.getMembershipList().stream()
			.filter(memberShip -> memberShip.getMemberEmail().equals(memberEmail))
			.findAny()
			.orElseThrow(() -> new NoSuchMemberException("no such member"));
	}





	// Membership
	@Override
	public void addMembership(ClubMembershipDto membershipDto) {
		//
		// 1. 이메일
		String memberId = membershipDto.getMemberEmail();

		//2. 클럽
		TravelClub club = clubStore.retrieve(membershipDto.getClubId());


		//3. 멤버 존재?
		CommunityMember member = Optional.ofNullable(memberStore.retrieve(memberId))
				.orElseThrow(() -> new NoSuchMemberException("No such member with email: " + memberId)); //★틀림- NoSuchMember아닌지?

		//3.멤버쉽이 클럽에 존재하나?
		for (ClubMembership membership : club.getMembershipList()) {
			if (memberId.equals(membership.getMemberEmail())) {
				throw new MemberDuplicationException("Member already exists in the club -->" + memberId);
			}
		}

		// add membership
		ClubMembership clubMembership = membershipDto.toMembership();
		club.getMembershipList().add(clubMembership);
		clubStore.update(club);
		member.getMembershipList().add(clubMembership);
		memberStore.update(member);
	}




	//클럽 찾고

	@Override
	public ClubMembershipDto findMembershipIn(String clubId, String memberId) {
		//
		TravelClub club = clubStore.retrieve(clubId); // # club에서 NPE 발생 가능?


		ClubMembership membership = getMembershipOfClub(club, memberId);

		return new ClubMembershipDto(membership);
	}



	@Override
	public void modifyMembership(String clubId, ClubMembershipDto newMembership) {

		//수정할 role
		RoleInClub newRole = newMembership.getRole();

		//클럽
		TravelClub targetClub = clubStore.retrieve(clubId);//# clubId 없을 경우?  없는 경우 예외 처리 안되어있음

		//이메일
		String targetEmail = newMembership.getMemberEmail();

		//이메일의 멤버
		CommunityMember targetMember = memberStore.retrieve(targetEmail); //# targetEmail 없는 경우 예외 처리 안되어있음




		//1. 클럽 멤버쉽 수정
		//1. 이메일 일치하는 클럽 멤버쉽 - 하나 
		ClubMembership membershipOfClub = getMembershipOfClub(targetClub, targetEmail);
		membershipOfClub.setRole(newRole); //멤버쉽 세팅 
		clubStore.update(targetClub);//업데이트



		//2. 멤버 멤버쉽 수정
		//멤버의 멤버쉽 리스트 -> 각각 clubId와 일치하면 setRole하기
		targetMember.getMembershipList().stream().forEach(membershipOfMember->{
			if (membershipOfMember.getClubId().equals(clubId)) {
				membershipOfMember.setRole(newRole);
			}
		});

		memberStore.update(targetMember); //멤버 업데이트

	}



}








































