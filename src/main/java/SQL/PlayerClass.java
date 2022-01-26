package SQL;

import PlayerChip.Maingui;
import PlayerManager.PlayerManager;
import QuestFunctions.QuestList;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static SQL.Connector.getConnection;

public class PlayerClass {

    final static String[] data = {".str", ".dex", ".def", ".agi", ".lvl", ".exp", ".coord"};

    private Player player;
    private String uuid;

    public PlayerClass(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public void sendToSQLServer(String encodedYaml) {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set classes = '"+encodedYaml+"' where uuid = '"+uuid+"'");

            stmt.close();
            conn.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration getClassFile() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select classes from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("classes");
                YamlConfiguration config = (new SQL.Converter()).decodeYaml(yaml);

                set.close();
                stmt.close();
                conn.close();

                return config;
            }

            set.close();
            stmt.close();
            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String classRegister(String className) {

        YamlConfiguration yaml;
        if(getClassesAmount() >= 10) {
            player.sendMessage("§c최대 클래스 생성 제한에 도달했습니다");
            return null;
        }


        if(!isExist()) {
            yaml = new YamlConfiguration();
        }
        else {
            yaml = getClassFile();
        }

        for(int i = 1; i<=10; i++) {
            if(!yaml.contains(className+"/"+i)) {

                String path = className+"/"+i;

                for(String d : data) {
                    if(!yaml.contains(path+"."+d)) yaml.set(path+"."+d,0);
                }
                for(int j=0; j<40; j++) { // 인벤토리 저장
                    yaml.set(path+".inv."+j, 0); // 인벤토리 저장
                }
                Arrays.stream(QuestList.values()).forEach(value-> {
                    yaml.set(path+".quests."+value.name()+".progress", 0);
                    //Bukkit.broadcastMessage(value.name()+"  "+value.getLevelReq());
                });

                List<Integer> list = Arrays.asList(0, 0, 0, 0);
                yaml.set(path+".rrtalent", list);
                yaml.set(path+".rltalent", list);
                yaml.set(path+".srtalent", list);
                yaml.set(path+".frtalent", list);

                String encoded = (new SQL.Converter()).encodeYaml(yaml);
                sendToSQLServer(encoded);

                return path;
            }
        }

        return null;
    }

    public void classCall(String className) {

        //Bukkit.broadcastMessage(className);
        if(className.equals("없음")) return;
        if(className.equals("null")) return;

        if(!isExist()) {
            classRegister(className);
            return;
        }

        YamlConfiguration yaml = getClassFile();

        if(!yaml.contains(className)) {
            player.sendMessage("§c파일 오류로 선택한 클래스의 정보를 불러오지 못하였습니다");
            return;
        }

        player.updateInventory();
        player.getInventory().clear();
        player.updateInventory();

        PlayerManager pm = PlayerManager.getinstance(player);

        PlayerManager.getinstance(player).CurrentClass = className.split("/")[0];
        PlayerManager.getinstance(player).CurrentClassNumber = Integer.parseInt(className.split("/")[1]);
        PlayerManager.getinstance(player).setStr(yaml.getInt(className+".str"));
        PlayerManager.getinstance(player).setDex(yaml.getInt(className+".dex"));
        PlayerManager.getinstance(player).setDef(yaml.getInt(className+".def"));
        PlayerManager.getinstance(player).setAgi(yaml.getInt(className+".agi"));
        PlayerManager.getinstance(player).setlvl(yaml.getInt(className+".lvl") <= 0 ? 1 : yaml.getInt(className+".lvl"));
        PlayerManager.getinstance(player).setexp(yaml.getInt(className+".exp"));
        List<Integer> rrlist = yaml.getIntegerList(className+".rrtalent");
        List<Integer> rllist = yaml.getIntegerList(className+".rltalent");
        List<Integer> frlist = yaml.getIntegerList(className+".frtalent");
        List<Integer> srlist = yaml.getIntegerList(className+".srtalent");
        pm.setTalent(rrlist, "RR");
        pm.setTalent(frlist, "FR");
        pm.setTalent(srlist, "SR");
        pm.setTalent(rllist, "RL");

        Location location = (new Converter()).stringToCoord(player, className);
        player.teleport(location);

        ItemStack air = new ItemStack(org.bukkit.Material.AIR, 1);
        ItemStack[] items = new ItemStack[40];
        Inventory inv = player.getInventory();
        Arrays.fill(items, air);

        for(int i=0; i<40; i++) {
            if(yaml.getItemStack(className+".inv."+i) == null){
                items[i] = air;
            }
            else {
                items[i] = yaml.getItemStack(className+".inv."+i);
            }
        }
        inv.setContents(items);
        player.getInventory().setItem(8, Maingui.getinstance().chipitemget(player));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Bukkit.getPluginManager().getPlugin("spellinteract"), () -> {
            player.updateInventory();
        }, 1);

    }

    public void classSave() {

        PlayerManager pM = PlayerManager.getinstance(player);
        if(pM.CurrentClass.equals("없음")) return;
        String className = pM.CurrentClass+"/"+pM.CurrentClassNumber;
        YamlConfiguration yaml = getClassFile();
        yaml.set(className+".str", pM.getStr());
        yaml.set(className+".dex", pM.getDex());
        yaml.set(className+".def", pM.getDef());
        yaml.set(className+".agi", pM.getAgi());
        yaml.set(className+".lvl", pM.getlvl());
        yaml.set(className+".exp", pM.getexp());
        yaml.set(className+".coord", (new Converter()).coordToString(player.getLocation()));
        yaml.set(className+".rltalent", pM.getTalentList("RL"));
        yaml.set(className+".rrtalent", pM.getTalentList("RR"));
        yaml.set(className+".frtalent", pM.getTalentList("FR"));
        yaml.set(className+".srtalent", pM.getTalentList("SR"));

        for(int i=0; i<41; i++) {
            if(player.getInventory().getItem(i) != null) {
                if(player.getInventory().getItem(i).getType() == Material.AIR) {
                    yaml.set(className+".inv."+i, 0);
                }
                else {
                    yaml.set(className+".inv."+i, player.getInventory().getItem(i));
                }
            }
            else {
                yaml.set(className+".inv."+i, 0);
            }
        }

        String encoded = (new SQL.Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set previousclass = '"+className+"' where uuid = '"+uuid+"'");

            stmt.close();
            //conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void classDelete(String className) {

        YamlConfiguration yaml = getClassFile();
        if(yaml.contains(className)) {
            yaml.set(className, null);
            player.sendMessage("§c성공적으로 클래스를 삭제하였습니다");
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);

    }

    public int getClassesAmount() {

        YamlConfiguration yaml;

        if(!isExist()) {
            return 0;
        }
        else {
            yaml = getClassFile();
            int count = 0;
            for(String s : yaml.getConfigurationSection("").getKeys(false)) {
                count++;
            }
            return count;
        }
    }

    public Set<String> getClasses() {

        YamlConfiguration yaml = getClassFile();
        return yaml.getConfigurationSection("").getKeys(false);
    }

    public String getPreviousClass() {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select previousclass from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String previousClass = set.getString("previousclass");

                set.close();
                stmt.close();
                //conn.close();

                if(previousClass == null) return "없음";
                return previousClass;
            }
            set.close();
            stmt.close();
            //conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return "없음";
    }

    public boolean isExist() {
        try {
            String uuid = player.getUniqueId().toString();
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select classes from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                if(set.getString("classes").equals("null")) {

                    set.close();
                    stmt.close();
                    //conn.close();

                    return false;
                }
                else if(set.getString("classes") == null) {

                    set.close();
                    stmt.close();
                    //conn.close();
                    return false;
                }
            }
            set.close();
            stmt.close();
            //conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return true;
    }



}
