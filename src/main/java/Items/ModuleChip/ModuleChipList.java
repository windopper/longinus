package Items.ModuleChip;

import java.util.Arrays;
import java.util.List;

public enum ModuleChipList {

    NULL("NULL", Arrays.asList("NULL"), 0),
    테스트_칩("테스트 칩", Arrays.asList("§7프로토타입"), 1),
    프로토타입("프로토타입V2", Arrays.asList("§7YEEEEEEEE"), 2),
    고성능수냉쿨러("고성능 수냉 쿨러", Arrays.asList("§6스킬의 과부하를 제거합니다"), 3),
    과충전("과충전", Arrays.asList("플레이어의 쉴드를 모두 소모하여", "최대 실드 비율에 비례하여 무기 능력치를 일정 시간 동안 강화"), 4),
    긴급탈출모듈("긴급 탈출 모듈", Arrays.asList("보조 추진기를 사용하여 상승"), 5),
    에너지환원모듈("에너지 환원 모듈", Arrays.asList("무기의 능력치를 일정 시간 동안 모두 잃고", "에너지를 전부 회복"), 6),
    동력전환모듈("동력 전환 모듈", Arrays.asList("자신의 체력을 1로 만들고, 보호막을 잃은 체력만큼 부여"), 7),
    시스템폭주("시스템 폭주", Arrays.asList("체력이 30%이하 일때 무기의 능력치가 2배 강화"), 101),
    키네틱에너니전환("키네틱 에너지 전환", Arrays.asList("플레이어가 뛰고 있을때 초당 ?에너지 회복"), 102),
    에너지하베스팅모듈("에너지 하베스팅 모듈", Arrays.asList("자신 또는 주변의 플레이어가 사용한 스킬에 사용된 일부 에너지를 포집"), 103);

    String name;
    List<String> lore;
    int code;

    ModuleChipList(String name, List<String> lore, int code) {
        this.name = name;
        this.lore = lore;
        this.code = code;
    }

    public String getName() { return name; }
    public List<String> getLore() { return lore; }
    public int getCode() { return code; }

}
