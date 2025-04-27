package javastory.club.stage3.step3.logic;

import javastory.club.stage3.step3.service.BoardService;
import javastory.club.stage3.step3.service.ClubService;
import javastory.club.stage3.step3.service.MemberService;
import javastory.club.stage3.step3.service.PostingService;
import javastory.club.stage3.step3.service.ServiceLycler;

public class ServiceLogicLycler implements ServiceLycler {
	//
	private static ServiceLycler lycler;

	private ClubService clubService;
	private MemberService memberService;
	private BoardService boardService;
	private PostingService postingService;

	//생성자 없으면 기본 생성자 만들기
	private ServiceLogicLycler() {
		//
	}

	//싱글톤
	//synchronized 붙이는 이유 : 여러 스레드가 동시에 shareInstance를 호출하면
	//동시에 lycler == null을 판단하고 여러 인스턴스 생성할 수 있는 위험
	//한번에 하나의 스레드만 이 메서드를 실행할 수 있게 > 딱 하나의 인스턴스만 생성되도록 보장

	//없으면? -> 싱글톤 깨질 수 있음 -> 객체 여러개 생성
	public synchronized static ServiceLycler shareInstance() { //멀티스레드 환경에서 동시접근 막기
		//
		if (lycler == null) {
			lycler = new ServiceLogicLycler();
		}

		return lycler;
	}


	//지연 초기화(Lazy Initialization)
	@Override
	public ClubService createClubService() {
		//처음 요청될때만 생성
		if (clubService == null) {
			clubService = new ClubServiceLogic();
		}


		//이후에는 이미 만들어진 객체 반환 -> 재사용
		return clubService;
	}

	@Override
	public MemberService createMemberService() {
		//
		if (memberService == null) {
			memberService = new MemberServiceLogic();
		}
		return memberService;
	}

	@Override
	public BoardService createBoardService() {
		//
		if (boardService == null) {
			boardService = new BoardServiceLogic();
		}

		return boardService;
	}

	@Override
	public PostingService createPostingService() {
		//
		if (postingService == null) {
			postingService = new PostingServiceLogic();
		}

		return postingService;
	}
}
