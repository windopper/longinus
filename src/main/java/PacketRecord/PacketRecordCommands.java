package PacketRecord;

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
         * create [fileName]
         * load [fileName]
         * save
         * close
         *
         * deletefile [fileName]
         *
         * watch [fileName]
         *
         * play
         * stop
         * skip [Integer] 시간 이동
         * skipto [Integer] 특정 시간으로 이동
         *
         *
         * destroy [entityName or Camera]
         * appear [entityType] actor [playerName] 엔티티 추가 그리고 조작할 유저 선택
         * setskin [HumanType's entityName] [texture] [signature]
         *
         *
         *
         */
    }
}
