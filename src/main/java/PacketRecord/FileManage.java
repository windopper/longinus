package PacketRecord;

import org.bukkit.Bukkit;

import java.io.File;

public class FileManage {

    public void List() {

        Bukkit.broadcastMessage("====== 녹화파일 리스트 ======");
        for(File file : (new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder().getAbsolutePath()+"\\Records")).listFiles()) {
            Bukkit.broadcastMessage("- "+file.getName());
        }
        Bukkit.broadcastMessage("=========================");
    }

    public void Erase(String filename) {
        try {
            File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder().getAbsolutePath()+"\\Records", filename+".yml");
            if(file.delete())
                Bukkit.broadcastMessage("파일 삭제 완료");

        }
        catch(NullPointerException e) {
            Bukkit.broadcastMessage("없는 파일입니다");
        }

    }
}
