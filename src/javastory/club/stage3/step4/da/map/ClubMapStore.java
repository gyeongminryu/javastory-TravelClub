package javastory.club.stage3.step4.da.map;

import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import javastory.club.stage3.step1.entity.AutoIdEntity;
import javastory.club.stage3.step1.entity.club.TravelClub;
import javastory.club.stage3.step4.da.map.io.MemoryMap;
import javastory.club.stage3.step4.store.ClubStore;
import javastory.club.stage3.step4.util.ClubDuplicationException;
import javastory.club.stage3.step4.util.NoSuchClubException;

public class ClubMapStore implements ClubStore {
	private Map<String,TravelClub> clubMap;
	private Map<String,Integer> autoIdMap;

	public ClubMapStore() {
		//
		this.clubMap = MemoryMap.getInstance().getClubMap();
		this.autoIdMap = MemoryMap.getInstance().getAutoIdMap();
	}


	//retrieveByName




	@Override
	public TravelClub retrieveByName(String name) { //iterator- break;
		return clubMap.values().stream()
			.filter(club-> club.getName().equals(name))
			.findAny() //★
			.orElseThrow(()-> new NoSuchClubException("no such club")); //
	}

	@Override
	public String create(TravelClub club) {

		//instanceof-> 대문자 옶음

		if(club instanceof AutoIdEntity) { //club 객체 - instanceof AutoIdEntity

			//클래스 이름 뽑기
			String className = TravelClub.class.getSimpleName();
			//만약 클래스 이름이 null이면 새로 넣어주기
				if (autoIdMap.get(className) == null) { //만약 없으면 새로 넣기
					autoIdMap.put(className, 1);
				}

				//autoIdMap에 저장된 id를 빼오기
				int sequenceKey = autoIdMap.get(className);

				//id-> format하기
				String autoId = String.format(club.getIdFormat(), sequenceKey);

				//autoIdMap에는 다음 순서 넣어두기 -> 같은 키 넣으면 계속 중복되므로....
				autoIdMap.put(className, ++sequenceKey);

				//clubMap에서 autoId를 얻었을 때 있으면 중복임
				Optional.ofNullable(clubMap.get(autoId))
					.ifPresent((storedClub) -> {
						throw new ClubDuplicationException("Club id already exists --> " + autoId);
					});

				//통과하면 club에 autoId set하고
				club.setAutoId(autoId);
			}


			//club 변한거 갱신
			clubMap.put(club.getId(), club); //근데 만약 위에 못 수행해서 getId()가 없으면?
			return club.getId();
	}



	//		//얘는 아이디 중복 체크
//
//		//클럽이 clubMap에 있으면..이미 존재함
//		Optional.ofNullable(clubMap.get(club.getId()))
//				.ifPresent(targetClub -> {
//					throw new ClubDuplicationException("Club already exists with id --> " + targetClub.getId());
//				});
//
//		if (club instanceof AutoIdEntity) { //만약 AutoIdEntity의 요소이면
//			String className = TravelClub.class.getSimpleName(); //클래스 이름 가져와서
//			//★★★int keySequence에 바로 안넣어주는 이유 -> null이 있을 수 있는데 바로 int에 넣으면 NPE 발생 가능ㄴ
//			if (autoIdMap.get(className) == null) { //만약 없으면 새로 넣기
//				autoIdMap.put(className, 1);
//			}
//			int keySequence = autoIdMap.get(className); //class이름으로 저장된거 가져와서
//			String autoId = String.format("%05d", keySequence); //format하고
//			club.setAutoId(autoId); //autoId로 넣기
//			autoIdMap.put(className, ++keySequence); //map에는 하나 증가시켜서 넣기
//		}
//
//		clubMap.put(club.getId(),  club);//club id까지 넣은 거 clubMap에 넣기
//
//		return club.getId(); //club의 id 반환




	@Override
	public void update(TravelClub club) {
		clubMap.put(club.getId(), club);
	}

	@Override
	public void delete(String clubId) {
		clubMap.remove(clubId);
	}


	@Override
	public TravelClub retrieve(String clubId) {
		return clubMap.get(clubId);
	}


	@Override
	public boolean exists(String clubId) {
		//club이 존재하면
		return Optional.ofNullable(clubMap.get(clubId)).isPresent();
	}
}
