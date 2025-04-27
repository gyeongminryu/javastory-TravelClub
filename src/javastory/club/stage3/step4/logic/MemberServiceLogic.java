package javastory.club.stage3.step4.logic;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.club.ClubMembership;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step1.util.InvalidEmailException;
import javastory.club.stage3.step4.service.MemberService;
import javastory.club.stage3.step4.service.dto.ClubMembershipDto;
import javastory.club.stage3.step4.service.dto.MemberDto;
import javastory.club.stage3.step4.store.ClubStore;
import javastory.club.stage3.step4.store.MemberStore;
import javastory.club.stage3.step4.util.MemberDuplicationException;
import javastory.club.stage3.step4.util.NoSuchMemberException;
import javastory.club.stage3.step4.util.NoSuchMembershipException;
import javastory.club.stage3.util.StringUtil;
import javastory.club.stage3.step4.da.map.ClubStoreMapLycler;

public class MemberServiceLogic implements MemberService {
	//
  private ClubStore clubStore;
  private MemberStore memberStore;

  public MemberServiceLogic() {
		//
    clubStore = ClubStoreMapLycler.getInstance().requestClubStore();
    memberStore = ClubStoreMapLycler.getInstance().requestMemberStore();
	}

	@Override
	public void register(MemberDto memberDto) throws InvalidEmailException {
		//멤버 id 중복
		Optional.ofNullable(memberStore.retrieve(memberDto.getEmail())) //email로임
			.ifPresent(targetMember -> {
				throw new MemberDuplicationException("Member already exists with email: " + targetMember.getId());
			});


		//member 저장
		memberStore.create(memberDto.toMember());
	}

	@Override
	public MemberDto find(String memberEmail) {
		//
		return Optional.ofNullable(memberStore.retrieve(memberEmail))
				.map(member -> new MemberDto(member))
				.orElseThrow(() -> new NoSuchMemberException("No such member with email: " + memberEmail));
	}

	@Override
	public List<MemberDto> findByName(String memberName) {
		//
		List<CommunityMember> members = memberStore.retrieveByName(memberName);
		if (members.isEmpty()) {
			throw new NoSuchMemberException("No such member with name: "+memberName);
		}

		return members.stream()
				.map(member -> new MemberDto(member))
				.collect(Collectors.toList());
	}

	@Override //이메일과 role은 안바꿈
	public void modify(MemberDto memberDto) throws InvalidEmailException {
		//이메일은 수정할 수 없음
		CommunityMember targetMember = Optional.ofNullable(memberStore.retrieve(memberDto.getEmail()))
				.orElseThrow(() -> new NoSuchMemberException("No such member with email: " + memberDto.getEmail()));

		// modify only if user inputs some value.
		if (StringUtil.isEmpty(memberDto.getName())) {
			memberDto.setName(targetMember.getName());
		}
		if (StringUtil.isEmpty(memberDto.getNickName())) {
			memberDto.setNickName(targetMember.getNickName());
		}
		if (StringUtil.isEmpty(memberDto.getPhoneNumber())) {
			memberDto.setPhoneNumber(targetMember.getPhoneNumber());
		}
		if (StringUtil.isEmpty(memberDto.getBirthDay())) {
			memberDto.setBirthDay(targetMember.getBirthDay());
		}

		memberStore.update(memberDto.toMember());
	}

	@Override
	public void remove(String memberId) {
		//
		if (!memberStore.exists(memberId)) {
			throw new NoSuchMemberException("No such member with email: " + memberId);
		}

  		//삭제할 멤버id의 멤버쉽 리스트들 찾기
		List<ClubMembershipDto> membershipList= findAllMembershipsIn(memberId);

		//클럽의 멤버쉽 삭제
		removeAllMembershipsOfClub(memberId,membershipList);

		memberStore.delete(memberId);
	}


  @Override
  public List<ClubMembershipDto> findAllMembershipsIn(String memberId) {
    //
    CommunityMember member = memberStore.retrieve(memberId);

    return member.getMembershipList().stream()
      .map(membership -> new ClubMembershipDto(membership))
      .collect(Collectors.toList());
  }


  @Override
  public void removeAllMembershipsOfClub(String memberId,List<ClubMembershipDto> membershipList){
		//멤버쉽 리스트에서 clubId 뽑기
	 List<String> clubIds = membershipList.stream()
		  .map(membership-> membership.getClubId())
		  .collect(Collectors.toList());

	  //clubId로 club 찾고 club의 멤버쉽 찾은 다음 memberId와 일치하지 않는 멤버쉽 반환
	  clubIds.stream().forEach(clubId->{
		  TravelClub club = clubStore.retrieve(clubId);

		  List<ClubMembership> membershipNew
			  = club.getMembershipList().stream()
			  .filter(memberShip-> !memberShip.getMemberEmail().equals(memberId))//★
			  .collect(Collectors.toList());

		  club.setMembershipList(membershipNew);

		  clubStore.update(club);
	  });


	  //새로운 멤버쉽 set

	  //club업데이트


  }


  @Override
  public void removeMembership(String clubId, String memberId) {
    //
    TravelClub foundClub = clubStore.retrieve(clubId);
    CommunityMember foundMember = memberStore.retrieve(memberId);
    ClubMembership clubMembership = getMembershipIn(foundClub, memberId);

    // remove membership
    foundClub.getMembershipList().remove(clubMembership);
    clubStore.update(foundClub);
    foundMember.getMembershipList().remove(clubMembership);
    memberStore.update(foundMember);
  }

  private ClubMembership getMembershipIn(TravelClub club, String memberEmail) {
    //
    for (ClubMembership membership : club.getMembershipList()) {
      if (memberEmail.equals(membership.getMemberEmail())) {
        return membership;
      }
    }

    String message = String.format("No such member[email:%s] in club[name:%s]", memberEmail, club.getName());
    throw new NoSuchMemberException(message);
  }

}
