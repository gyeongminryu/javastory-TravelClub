package javastory.club.stage3.step3.logic;

import java.util.*;
import java.util.stream.Collectors;

import javastory.club.stage3.step1.entity.AutoIdEntity;
import javastory.club.stage3.step1.entity.club.ClubMembership;
import javastory.club.stage3.step1.entity.club.CommunityMember;
import javastory.club.stage3.step1.entity.club.RoleInClub;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step3.logic.storage.MapStorage;
import javastory.club.stage3.step3.service.ClubService;
import javastory.club.stage3.step3.service.dto.ClubMembershipDto;
import javastory.club.stage3.step3.service.dto.TravelClubDto;
import javastory.club.stage3.step3.util.ClubDuplicationException;
import javastory.club.stage3.step3.util.MemberDuplicationException;
import javastory.club.stage3.step3.util.NoSuchClubException;
import javastory.club.stage3.step3.util.NoSuchMemberException;
import javastory.club.stage3.step4.util.NoSuchMembershipException;
import javastory.club.stage3.util.StringUtil;

public class ClubServiceLogic implements ClubService {
	//
	private Map<String,CommunityMember> memberMap;
	private Map<String,TravelClub> clubMap; //clubId, club객체
	private Map<String,Integer> autoIdMap; //클럽 번호와 이름을 저장?

	public ClubServiceLogic() {
		//
		this.memberMap = MapStorage.getInstance().getMemberMap();
		this.clubMap = MapStorage.getInstance().getClubMap();
		this.autoIdMap = MapStorage.getInstance().getAutoIdMap();
	}

	@Override
	public void register(TravelClubDto clubDto) {
		//클럽 이름 중복 검사
		//Optional.ofNullable() : null일 수 있는 값을 Optional로 감싸서 null-safe하게 처리하겠다는 뜻
		//.ifPresent : 값이 존재하면 (null이 아니면) 안에 있는 람다식 진행
		//
		Optional.ofNullable(retrieveByName(clubDto.getName()))
				.ifPresent((club) -> {
					throw new ClubDuplicationException("Club already exists with name:" + club.getName());
				});

		//TravelClub club 만들기
		TravelClub club = clubDto.toTravelClub();

		//만약 AutoIdEntity 상속 받으면
		if (club instanceof AutoIdEntity) {
			//TravelClub.class.getSimpleName();
			//클래스 이름 얻기
			String className = TravelClub.class.getSimpleName();

			//만약 autoIdMap에서 TravelClub의 value가 없으면 -> 자동 ID 어디까지 썼는지 확인하는 부분
			if (autoIdMap.get(className) == null) { //만약 클럽에 해당하는 클럽 id가 없으면
				autoIdMap.put(className, 1); //
			}

			//autoIdMap에서 클래스 이름의 마지막 자동 ID 가져오기
			int keySequence = autoIdMap.get(className);
			//자동 id 만들기
			String autoId = String.format("%05d", keySequence);

			//클럽의 autoId로 설정하기
			club.setAutoId(autoId);

			//autoIdMap에 마지막에 사용한 id값 다음 걸 넣어주는 작업하기
			autoIdMap.put(className, ++keySequence);
		}

		//clubMap에도 id값과 club객체 넣기
		clubMap.put(club.getId(),  club);

		clubDto.setUsid(club.getId());
	}

	@Override
	public TravelClubDto findClub(String clubId) {
		//
		return Optional.ofNullable(clubMap.get(clubId))
				.map(foundClub -> new TravelClubDto(foundClub))
				.orElseThrow(() -> new NoSuchClubException("No such club with id --> " + clubId));
	}

	@Override
	public TravelClubDto findClubByName(String name) {
		//
		return Optional.ofNullable(retrieveByName(name))
				.map(foundClub -> new TravelClubDto(foundClub))
				.orElseThrow(() -> new NoSuchClubException("No such club with name: " + name));
	}

	@Override
	public void modify(TravelClubDto clubDto) {

		//이름 중복체크가 안들어감


		//전달 받은 clubDTO에서 clubId 얻어오기
		String clubId = clubDto.getUsid(); //clubDto에서 Usid 받기

		//clubMap
		//clubMap에서 'id값을 얻은 club객체' -> Optional.ofNullable
		TravelClub targetClub = Optional.ofNullable(clubMap.get(clubId))
				.orElseThrow(() -> new NoSuchClubException("No such club with id --> " + clubDto.getUsid()));



		//StringUtil -> null이거나 ""면 true 반환 아니면 false
		if (StringUtil.isEmpty(clubDto.getName())){ //수정할 값이 든 clubDto에 이름이 비어있으면
			clubDto.setName(targetClub.getName()); //얻은 클래스(기존 클래스) 에서 이름을 가져와서 set
		}


		//만약
		if (StringUtil.isEmpty(clubDto.getIntro())){ //수정할 값 clubDto에서 intro가 없으면 -> 수정 안한다는 뜻
			clubDto.setIntro(targetClub.getIntro());// //얻은 클래스(기존 클래스) 에서 이름을 가져와서 set
		}

		//만약 안 비어있으면 그냥 id, Dto-> TravelClub으로 만든 값 넣기
		clubMap.put(clubId, clubDto.toTravelClub());
	}

  @Override
  public void remove(String clubId) {
    //1.삭제할 클럽 id있는지 확인
    Optional.ofNullable(clubMap.get(clubId))
      .orElseThrow(() -> new NoSuchClubException("No such club with id --> " + clubId));

	//2.모든 membership List 찾기
    List<ClubMembershipDto> membershipList = findAllMembershipsIn(clubId);

	//3. membershipList에서 모든 사원의 이메일 찾아서
	  //해당 이메일로 club객체 찾은 다음
	  //club객체 중에서 삭제할 clubId와 clubId가 일치하는 clubMembership 객체 중 첫번째 찾기
	  //삭제할 clubMembership 객체 제외한 membership들 List로 반환
    removeAllMembershipsOfMember(clubId, membershipList); //특정 member의 모든 멤버쉽 remove

    clubMap.remove(clubId);
  }

  //List<ClubMembership> -> List<ClubMembershipDTO> //클럽의 모든 멤버쉽들 -> List<ClubMembershipDTO>형태로 변환
	@Override
	public List<ClubMembershipDto> findAllMembershipsIn(String clubId) {
		//club객체 얻은 것의 멤버쉽 리스트-> ClubMembershipDTO에 넣고 -> List 작업
		return clubMap.get(clubId).getMembershipList()
			.stream()
			.map(membership -> new ClubMembershipDto(membership))
			.collect(Collectors.toList());
	}


	//모든 사원에게서 특정 clubId 가진 멤버쉽 객체 찾아서 -> 해당 멤버쉽 객체 제외한 멤버쉽 리스트 반환
	@Override
	public void removeAllMembershipsOfMember(String clubId, List<ClubMembershipDto> clubMembershipDtos) {
		//clubMembershipDtos.stream()돌려서 membership -> membershp.getMemberEmail
		List<String> members = clubMembershipDtos.stream()
			.map(membership -> membership.getMemberEmail()) //★
			.collect(Collectors.toList());

		//위에서 만든 List를 하나씩 돌리기
		for (int i=0; i < members.size(); i++) {
			CommunityMember foundMember = this.memberMap.get(members.get(i)); //memberMap.get(email);해서 member얻어오기

			//CommunityMember에서 getMembershipList()
			//clubid가 맞는 membership 찾기 -> 없으면 예외처리
			ClubMembership targetMembership = foundMember.getMembershipList().stream()
				.filter(membership -> membership.getClubId().equals(clubId))
				.findAny() //findAny(): 멀티 스레드에서 Stream을 처리할 때 가장 먼저 차은 요소를 리턴 //왜 findAny?
				.orElseThrow(() -> new NoSuchMembershipException("No such membership with clubId --> " + clubId ));


			//멤버쉽 중 targetMembership이랑 일치하지 않는 멤버쉽 객체만 모아서 다시 List로 변환
			foundMember.setMembershipList(foundMember.getMembershipList().stream()
				.filter(membership -> !membership.equals(targetMembership))
				.collect(Collectors.toList()));
		}
	}



	//그냥 일반 메서드 ==================================

	// Private
	private TravelClub retrieveByName(String name) {
		//clubMap에서 values (클럽 객체들) 구한 다음
		//클럽 객체 하나하나 돌면서 getName했을 때 이름이 같으면 return

		//1.클럽들 있는지 확인
		Collection<TravelClub> clubs = clubMap.values();

		//2. 만약 비어있으면 return null;
		if (clubs.isEmpty()) {
			return null;
		}

		//찾는 클럽
		TravelClub foundClub = null;
		for (TravelClub club : clubs) {
			if (club.getName().equals(name)) {
				foundClub = club;
				break;
			}
		}

		return foundClub;
	}



	//멤버쉽 ============================================================================================
	@Override
	public void addMembership(ClubMembershipDto membershipDto) {

		//멤버가 있나, 이메일이 없나?

		//[1] 멤버 있는지 확인 + 클럽에 중복된 이메일이  있는지 확인(멤버 있는지..)
		//1.memberId로 memberEmail 가져오기
		String memberId = membershipDto.getMemberEmail();

		//memberMap에서 email의 member객체 있나 확인
		CommunityMember foundMember = Optional.ofNullable(memberMap.get(memberId))
				.orElseThrow(() -> new NoSuchClubException("No such member with email: " + memberId));

		// 2.clubId 가져오기
		String clubId = membershipDto.getClubId();


		//3.이메일 중복 체크
		//클럽 찾아서
		//클럽의 멤버쉽 중 전달된 memberId와 membership의 이메일이 같으면 memberDuplicate
		TravelClub foundClub = clubMap.get(clubId); //club은 이미 추가된 듯
		for (ClubMembership membership : foundClub.getMembershipList()) { //찾은 클럽에서 memberShipList 돌면서 만약 추가할 email 이미 있으면 중복된 팀원
			if (memberId.equals(membership.getMemberEmail())) {
				throw new MemberDuplicationException("Member already exists in the club -->" + memberId);
			}
		}


		//[2]만약 멤버 존재하고 + 클럽에 중복된 멤버 없으면?
		// -> club에 membership객체 추가
		//MemberShipDTO -> memberShip으로 바꾸기
		ClubMembership clubMembership = membershipDto.toMembership();

		//찾은 클럽 객체의 멤버쉽 리스트에 멤버쉽 추가
		foundClub.getMembershipList().add(clubMembership);


		//clubMap에 왜 클럽 아이디와 foundClub? 넣지? -> 수정된 foundClub으로 갱신하기 위해?
		clubMap.put(clubId, foundClub); //clubMap에 클럽 아이디와 찾은 클럽 넣기//★체크: 만들지 않지 않았나?(꼭 해줘야하는건지)



		//멤버쉽 리스트에 멤버쉽 추가
		foundMember.getMembershipList().add(clubMembership);

		//memberMap에 memberId,foundMember 넣기 ->//★체크: 만들지 않지 않았나?(꼭 해줘야하는건지)
		memberMap.put(memberId, foundMember);
	}





	@Override
	public ClubMembershipDto findMembershipIn(String clubId, String memberId) {
		//클럽 객체 찾기
		TravelClub foundClub = clubMap.get(clubId);

		//memberId와 일치하는 클럽 멤버쉽 찾기
		ClubMembership membership = getMembershipOfClub(foundClub, memberId);

		//해당 클럽 멤버쉽 반환
		return new ClubMembershipDto(membership);
	}



	//클럽의 멤버쉽 구하기 -> 클럽의 멤버쉽 중 멤버 이메일이 같은 멤버쉽 반환
	private ClubMembership getMembershipOfClub(TravelClub club, String memberId) {
		//
		for (ClubMembership membership : club.getMembershipList()) {
			if (memberId.equals(membership.getMemberEmail())) {
				return membership;
			}
		}

		throw new NoSuchMemberException(String.format("No such member[email:%s] in club[name:%s]", memberId, club.getName()));
	}

	@Override
	public void modifyMembership(String clubId, ClubMembershipDto membershipDto) {


		//[1] clubId로 (clubMembership)의 role바꿈 =========================================================
		// 클럽 얻어오기
		TravelClub targetClub = clubMap.get(clubId);

		//이메일 얻어오기
		String targetEmail = membershipDto.getMemberEmail();

		//새로운 역할 얻어오기
		RoleInClub newRole = membershipDto.getRole();



		//[club멤버쉽 수정]
		//Club의 멤버쉽 중 email이 일치하는 것 가져오기 (targetClub,email)
		ClubMembership membershipOfClub = getMembershipOfClub(targetClub, targetEmail);

		//해당 멤버쉽에 setRole()
		membershipOfClub.setRole(newRole);


		//[멤버 멤버쉽 수정]
		//★ clubMap의 role은 안 바꿔도 되는지? -> 확인
		//[2] (memberMap) 의 member에서 membershipList 중 clubId가 같은 것들의 role을 바꿈 =========================================================
		memberMap.get(targetEmail).getMembershipList()
			.stream()
			.filter(membershipOfMember -> membershipOfMember.getClubId().equals(clubId))
			.forEach(membershipOfMember -> membershipOfMember.setRole(newRole));//★

	}

	@Override
	public void removeMembership(String clubId, String memberId) {
		//클럽 객체 찾기
		TravelClub foundClub = clubMap.get(clubId);

		//멤버 객체 찾기
		CommunityMember foundMember = memberMap.get(memberId);


		//클럽의 이메일에 해당하는 멤버십 찾기
		ClubMembership clubMembership = getMembershipOfClub(foundClub, memberId);

		//클럽 객체의 멤버쉽 리스트 중에서 클럽 멤버쉽 remove
		foundClub.getMembershipList().remove(clubMembership);

		//멤버 객체의 멤버쉽 리스트 중에서 클럽 멤버쉽 remove
		foundMember.getMembershipList().remove(clubMembership);

		//remove해서 바뀐 클럽 객체 다시 clubMap에 넣어주기
		clubMap.put(clubId, foundClub);

		//remove해서 바뀐 클럽 객체 다시 memberMap에 넣어주기
		memberMap.put(foundMember.getId(),foundMember);
	}



	//★안 나옴
  @Override
  public void checkPresidentRole(TravelClub club, String memberId) {
    //
    List<ClubMembershipDto> memberships = findAllMembershipsIn(club.getId());

    Optional.ofNullable(memberships.stream()
      .filter(membership -> membership.getRole().equals(RoleInClub.President)))
      .ifPresent(memberOfPresident -> {
        throw new Error("President role already exists.");
      });
  }
  
  


}
