package Items.ModuleChip;

import java.util.Arrays;
import java.util.List;

public enum ModuleChipList {

    NULL("NULL", Arrays.asList("NULL"), 0),
    테스트_칩("테스트 칩", Arrays.asList("§7프로토타입"), 1),
    프로토타입("프로토타입V2", Arrays.asList("§7YEEEEEEEE"), 2),
    고성능수냉쿨러("고성능 수냉 쿨러", Arrays.asList("§6스킬의 과부하를 제거합니다"), 3);

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
