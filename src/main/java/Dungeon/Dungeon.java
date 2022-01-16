package Dungeon;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import spellinteracttest.Main;

import java.io.*;
import java.util.*;

public class Dungeon {

    private static HashMap<Player, Dungeon> activeDungeons = new HashMap<>();

    private Set<Player> players;
    private World instanceDungeon;
    private String instanceName;

    private Dungeon(Set<Player> players, World instanceDungeon, String instanceName) {
        this.players = players;
        this.instanceDungeon = instanceDungeon;
        this.instanceName = instanceName;
    }

    public static void init(Set<Player> players, World world) {
        List<Player> alreadyInDungeon = players.stream().filter((p)->activeDungeons.containsKey(p)).toList();
        String var = "";
        if(alreadyInDungeon.size()>=1) {
            for(Player p : alreadyInDungeon) {
                var += p.getName()+" ";
            }
            final String var1 = var;
            players.forEach((p)->p.sendMessage("§6플레이어 §e"+var1+"§6(이)가 이미 던전에 있습니다"));
            return;
        }
        else {
            players.forEach((p)->{
                p.sendMessage("§6플레이어 §e"+players.size()+"§6명 확인됨");
                p.sendMessage("§6인던 생성 중...");
            });
        }

        File worldFile = new File(world.getWorldFolder().getParent()+"\\dungeons\\"+world.getName());
        String randomName = world.getName() + System.currentTimeMillis();
        try {

            FileUtils.copyDirectory(worldFile, new File(worldFile.getParent(), randomName));

//            WorldCreator creator = new WorldCreator(randomName);
//            World dungeon = Bukkit.createWorld(creator);


            World dungeon = Bukkit.getWorld(randomName);

            players.forEach((p)-> {
                Dungeon IDungeon = new Dungeon(players, dungeon, randomName);
                activeDungeons.put(p, IDungeon);
                p.sendMessage("§a생성 완료!");
                p.teleport(new Location(dungeon, 400, 110, -100));
                p.sendTitle("§5"+randomName, " ", 10, 40, 10);

                Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
                    p.teleport(new Location(world, 400, 110, -100));
                }, 200);
            });
        }
        catch(IOException e) {
            players.forEach((p)->{
                p.sendMessage("§c인던 생성에 실패하였습니다");
                activeDungeons.remove(p);
            });
            e.printStackTrace();
        }
    }

    private static void copyWorld(File source, File target){
        try {
            List<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.dat"));
            if(!ignore.contains(source.getName())) {
                if(source.isDirectory()) {
                    if(!target.exists())
                        target.mkdirs();
                    String files[] = source.list();
                    for (String file : files) {
                        File srcFile = new File(source, file);
                        File destFile = new File(target, file);
                        copyWorld(srcFile, destFile);
                    }
                } else {
                    InputStream in = new FileInputStream(source);
                    OutputStream out = new FileOutputStream(target);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0)
                        out.write(buffer, 0, length);
                    in.close();
                    out.close();
                }
            }
        } catch (IOException e) {

        }
    }
}
