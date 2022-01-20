package PacketRecord.Recording;

import org.bukkit.entity.Player;

public class PacketRecordCommands {

    private final Player player;

    public PacketRecordCommands(Player player) {
        this.player = player;
    }

    public void commandListener(String cmdName, String[] args) {
        if(!cmdName.equals("packetrecord")) return;

        /**TODO
         *
         * -c [fileName] create file
         * -l [fileName] load file
         * -s save file
         * -ex exit file
         *
         * -d [fileName]
         *
         * -see [fileName]
         *
         * -p play file
         * -s stop file
         * skip [Integer] 시간 이동
         * skipto [Integer] 특정 시간으로 이동
         *
         *
         *
         * destroy [entityName or Camera] // 해당 엔티티 삭제 모든 모션도 삭제됨
         * hide [entityName] [duringTick] // duringTick 만큼 해당 엔티티 숨기기
         * appear [entityType] [playerName] 엔티티 추가 그리고 조작할 유저 선택
         * focus [entityName] [playerName] 엔티티 선택 그리고 조작할 유저 선택
         * move [entityName] [playerName] 엔티티를 해당 유저위치로 이동
         * copy [entityName] [playerName] 엔티티를 복사하여 해당 유저 위치로 이동 !모든 애니메이션도 복사됨!
         * setskin [HumanType's entityName] [texture] [signature] // 스킨 설정
         * setcamera [playerName] // set camera
         * optimize // 저장파일 최적화하기
         *
         *
         */

        if(args.length==1) {
            switch(args[0]) {
                case "-s": {
                    Record.getInstance(player).save();
                    break;
                }
                case "-ex": {
                    if(Record.hasInstance(player)) {
                        Record.getInstance(player).removeInstance();
                        notify.Successful("인스턴스를 성공적으로 제거하였습니다", player);
                    }
                    else {
                        notify.Warning("현재 활성화된 인스턴스가 없습니다", player);
                    }
                }
            }
        }
        else if(args.length==2) {

        }

    }
}
