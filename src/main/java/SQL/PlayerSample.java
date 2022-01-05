package SQL;

import Mob.EntityManager;
import Mob.MobListManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import static SQL.Connector.getConnection;

public class PlayerSample {

    private Player player;
    private String uuid;

    public PlayerSample(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public YamlConfiguration getSampleFile() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select samples from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("samples");
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

    public void updateSampleList() {

    }

    private void sendToSQLServer(String encodedYaml) {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set samples = '"+encodedYaml+"' where uuid = '"+uuid+"'");

            stmt.close();
            //conn.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNewSampleList() {

        YamlConfiguration yaml = getSampleFile();

        for(MobListManager.MobList mobList : MobListManager.MobList.values()) {
            if(mobList.isScannable()) {
                if(yaml.contains(mobList.getPlanet()+"."+mobList.name())) continue;
                yaml.set(mobList.getPlanet()+"."+mobList.name()+".count", 0);
                yaml.set(mobList.getPlanet()+"."+mobList.name()+".firstSeen", " ");
                yaml.set(mobList.getPlanet()+"."+mobList.name()+".lastSeen", " ");
            }
        }

        sendToSQLServer((new Converter()).encodeYaml(yaml));

    }

    public void updateAnalyzedEntity(LivingEntity entity) {

        YamlConfiguration yaml = getSampleFile();
        if(yaml == null) return;

        String name = EntityManager.getinstance(entity).getCustomName();

        SimpleDateFormat date = new SimpleDateFormat("yy년 MM월 dd일 HH시 mm분", Locale.KOREA);
        String datestr = date.format(new Date());

        Arrays.stream(MobListManager.MobList.values()).forEach(value -> {
            if(value.getName().equals(name) && value.isScannable()) {
                int intvalue = yaml.getInt(value.getPlanet()+"."+value.name()+".count");
                intvalue ++;

                checkAnalyzed(intvalue, value.getRawName());
                yaml.set(value.getPlanet()+"."+value.name()+".count", intvalue);
                if(intvalue == 1) yaml.set(value.getPlanet()+"."+value.name()+".firstSeen", datestr);
                yaml.set(value.getPlanet()+"."+value.name()+".lastSeen", datestr);
            }
        });

        String encodedyaml = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encodedyaml);
    }

    private void checkAnalyzed(int intvalue, String name) {
        if(intvalue == 1) {
            player.sendMessage("§a>> "+"§e처음으로 §6["+name+"] §e관찰에 성공하셨습니다. 표본도감에 등록됩니다");
        }
    }

}
