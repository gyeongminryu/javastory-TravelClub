package javastory.club.stage3.step3.logic;

import java.util.*;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.club.ClubMembership;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step1.util.InvalidEmailException;
import javastory.club.stage3.step3.logic.storage.MapStorage;
import javastory.club.stage3.step3.service.MemberService;
import javastory.club.stage3.step3.service.dto.MemberDto;
import javastory.club.stage3.step3.util.MemberDuplicationException;
import javastory.club.stage3.step3.util.NoSuchMemberException;
import javastory.club.stage3.step4.service.dto.ClubMembershipDto;
import javastory.club.stage3.step4.util.NoSuchMembershipException;
import javastory.club.stage3.util.StringUtil;

public class MemberServiceLogic implements MemberService {
	//
  private Map<String, TravelClub> clubMap;
  private Map<String, CommunityMember> memberMap;

	public MemberServiceLogic() {
		//
    this.clubMap = MapStorage.getInstance().getClubMap();
    this.memberMap = MapStorage.getInstance().getMemberMap();
	}

	@Override
	public void register(MemberDto memberDto) throws InvalidEmailException {
		//1. 이메일 받기
		String memberEmail = memberDto.getEmail();

		//2. member객체 -> 이미 memberMap에 있으면 예외 -> MemberDuplicationException
		Optional.ofNullable(memberMap.get(memberEmail))
				.ifPresent(foundMember -> {
					throw new MemberDuplicationException("It is already exist the member email: " + foundMember.getEmail());
				});

		//memberMap.put(email,memberDTO.toMember()) (memberDTO-> member로 변환)
		memberMap.put(memberEmail, memberDto.toMember());
	}

	@Override
	public MemberDto find(String memberEmail) {
		//
		return Optional.ofNullable(memberMap.get(memberEmail))
				.map(foundMember -> new MemberDto(foundMember))
				.orElseThrow(() -> new NoSuchMemberException("No such member with email: " + memberEmail));
	}

	@Override
	public List<MemberDto> findByName(String memberName) {
		//
		Collection<CommunityMember> members = memberMap.values();
		if (members.isEmpty()) {
			return new ArrayList<>();
		}
		return members.stream()
				.filter(member -> member.getName().equals(memberName))
				.map(targetMember -> new MemberDto(targetMember))
				.collect(Collectors.toList());
	}

	@Override
	public void modify(MemberDto memberDto) throws InvalidEmailException {
		//1. 멤버 이메일
		String memberEmail = memberDto.getEmail();

		//2. 존재하는지 확인
		CommunityMember targetMember = Optional.ofNullable(memberMap.get(memberEmail))
				.orElseThrow(() -> new NoSuchMemberException("No such member with email: " + memberDto.getEmail()));

		// 3. 새로 받은 값들에 값이 없으면 -> 기존 값들에서 가져와서 세팅 (name,nickName, phoneNumber,
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

		//memberMap에 memberEmail과 memeberDTO를 member로 변환해 넣음
		memberMap.put(memberEmail, memberDto.toMember());
	}

	@Override
	public void remove(String memberEmail) {
		//
		Optional.ofNullable(memberMap.get(memberEmail))
				.orElseThrow(() -> new NoSuchMemberException("No such member with email: " + memberEmail));

    List<ClubMembershipDto> membershipList = findAllMembershipsIn(memberEmail);

    removeAllMembershipsOfClub(memberEmail, membershipList);

    memberMap.remove(memberEmail);
	}

  @Override
  public List<ClubMembershipDto> findAllMembershipsIn(String memberEmail) {
    //
    return memberMap.get(memberEmail).getMembershipList()
        .stream()
        .map(membership -> new ClubMembershipDto(membership))
        .collect(Collectors.toList());
  }



  @Override
  public void removeAllMembershipsOfClub(String memberId, List<ClubMembershipDto> clubMembershipDtos) {
    //
    List<String> clubs = clubMembershipDtos.stream()
      .map(membership -> membership.getClubId())
      .collect(Collectors.toList());

    for (int i=0; i < clubs.size(); i++) {
      TravelClub foundClub = this.clubMap.get(clubs.get(i));

      ClubMembership targetMembership = foundClub.getMembershipList().stream()
        .filter(membership -> membership.getMemberEmail().equals(memberId))
        .findAny()
        .orElseThrow(() -> new NoSuchMembershipException("No such membership with memberId --> " + memberId));

      foundClub.setMembershipList(foundClub.getMembershipList().stream()
        .filter(membership -> !membership.equals(targetMembership))
        .collect(Collectors.toList()));
    }
  }




  //아직 안씀
	@Override
	public void removeMembership(String clubId, String memberEmail) {
		//
		TravelClub foundClub = clubMap.get(clubId);
		CommunityMember foundMember = memberMap.get(memberEmail);
		ClubMembership clubMembership = getMembershipIn(foundClub, memberEmail);

		// remove membership
		foundClub.getMembershipList().remove(clubMembership);
		foundMember.getMembershipList().remove(clubMembership);
	}


  private ClubMembership getMembershipIn(TravelClub club, String memberEmail) {
    //
    for (ClubMembership membership : club.getMembershipList()) {
      if (memberEmail.equals(membership.getMemberEmail())) {
        return membership;
      }
    }

    String message = String.format("No such member[email:%s] in club[name:%s]", memberEmail, club.getName());
    throw new javastory.club.stage3.step4.util.NoSuchMemberException(message);
  }

}
