package SQL;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PlayerAlarm {

    private Player player;
    private String uuid;

    public PlayerAlarm(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public YamlConfiguration getAlarmFile() {
        try {
            Connection conn = (new SQL.sqlData()).getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select classes from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("alarms");
                YamlConfiguration config = (new SQL.Converter()).decodeYaml(yaml);

                set.close();
                stmt.close();
                conn.close();

                return config;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addAlarm(String contents, String type) {

        YamlConfiguration yaml = getAlarmFile();
        SimpleDateFormat date = new SimpleDateFormat("발신 날짜 yy년 MM월 dd일 HH시 mm분", Locale.KOREA);
        String datestr = date.format(new Date());
        datestr = "§7"+datestr;

        for(int i=49; i>=0; i-- ) {
            int j = i+1;
            yaml.set(j+".content", yaml.getString(i+".content"));
            yaml.set(j+".type", yaml.getString(i+".type"));
            yaml.set(j+".date", yaml.getString(i+".date"));

            if(i==0) {
                yaml.set(i+".content", contents);
                yaml.set(i+".type", type);
                yaml.set(i+".date", datestr);
            }
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);
    }

    public void removeOldAlarm() {

        YamlConfiguration yaml = getAlarmFile();

        for(int i=49; i>=0; i-- ) {

            if(yaml.getString(i+".content").equals("")) continue;

            yaml.set(i+".content", "");
            yaml.set(i+".type", "");
            yaml.set(i+".date", "");
            break;
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);
    }

    public void removeAllAlarm() {

        YamlConfiguration yaml = getAlarmFile();

        for(int i=49; i>=0; i--) {
            yaml.set(i+".content", "");
            yaml.set(i+".type", "");
            yaml.set(i+".date", "");
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);
    }

    //TODO 알람데이터 sql 전환

    private void sendToSQLServer(String encodedYaml) {
        try {
            Connection conn = (new sqlData()).getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set alarms = '"+encodedYaml+"' where = uuid+'"+uuid+"'");

            stmt.close();
            conn.close();

        }
        catch(Exception e) {

        }
    }



}
