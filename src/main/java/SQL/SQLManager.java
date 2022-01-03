package SQL;

import Mob.MobListManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLManager {

    private Player player;
    private String uuid;

    public SQLManager(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public void initData() {

        SQL.sqlData sqlData = new SQL.sqlData();
        SQL.Converter converter = new SQL.Converter();

        if(!(new PlayerStorage(player)).isDataExist()) {
            (new PlayerStorage(player)).initUserStorages();
        }

        if(isDataExist()) return;

        String name = player.getName();
        String uuid = player.getUniqueId().toString();
        int altera = 0;

        String classes = null;
        String quests = null;

        YamlConfiguration alarmYaml = new YamlConfiguration();
        for(int i=0; i<=100; i++) {
            alarmYaml.set(i+".content", " ");
            alarmYaml.set(i+".type", " ");
            alarmYaml.set(i+".date", " ");
        }

        String alarms = converter.encodeYaml(alarmYaml);

        YamlConfiguration sampleYaml = new YamlConfiguration();

        for(MobListManager.MobList mobList : MobListManager.MobList.values()) {
            if(mobList.isScannable()) {
                sampleYaml.set(mobList.getPlanet()+"."+mobList.name()+".count", 0);
                sampleYaml.set(mobList.getPlanet()+"."+mobList.name()+".firstSeen", " ");
                sampleYaml.set(mobList.getPlanet()+"."+mobList.name()+".lastSeen", " ");
            }
        }

        String samples = converter.encodeYaml(sampleYaml);
        String previousclass = null;

        double locx = player.getLocation().getX();
        double locy = player.getLocation().getY();
        double locz = player.getLocation().getZ();

        YamlConfiguration marketitemsYaml = new YamlConfiguration();
        marketitemsYaml.set("mainMarket", "");
        String marketitems = converter.encodeYaml(marketitemsYaml);

        try {
            Connection conn = sqlData.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("insert into longinus.user values ('"+name+"', '"+uuid+"', '"+altera+"', '"
                    +classes+"', '"+quests+"', '"+alarms+"', '"+samples+"', '"+previousclass+"', '"
                    +locx+"', '"+locy+"', '"+locz+"', '"+marketitems+"')");
            stmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateData() {

        SQL.sqlData sqlData = new SQL.sqlData();

        if(!isDataExist()) return;

        (new PlayerSample(player)).updateNewSampleList(); // 새로운 몬스터가 업데이트 된것이 있으면 업데이트


        try {

            String name = player.getName();
            String uuid = player.getUniqueId().toString();

            Connection conn = sqlData.getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set name = '"+name+"' where uuid = '"+uuid+"'");

            stmt.close();
            conn.close();

        }
        catch(Exception e) {

        }
    }
    public boolean isDataExist() {

        SQL.sqlData sqlData = new SQL.sqlData();

        try {
            String uuid = player.getUniqueId().toString();
            Connection conn = sqlData.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select exists ( select * from longinus.user where uuid = '"+uuid+
                    "' ) as success");
            if(set.next()) {
                if(set.getInt("success") == 1) {

                    set.close();
                    stmt.close();
                    conn.close();

                    return true;
                }
            }

            set.close();
            stmt.close();
            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
