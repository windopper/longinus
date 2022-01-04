package SQL;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.*;

import static SQL.sqlData.getConnection;

public class PlayerAlarm {

    private Player player;
    private String uuid;

    public PlayerAlarm(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public YamlConfiguration getAlarmFile() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select alarms from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("alarms");
                if(yaml.equals("null")) {
                    set.close();
                    stmt.close();
                    return new YamlConfiguration();
                }
                YamlConfiguration config = (new SQL.Converter()).decodeYaml(yaml);

                set.close();
                stmt.close();
//                conn.close();

                return config;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return new YamlConfiguration();
    }

    public static void broadcastAlarm(String contents, String type) {

        SimpleDateFormat date = new SimpleDateFormat("발신 날짜 yy년 MM월 dd일 HH시 mm분", Locale.KOREA);
        String datestr = date.format(new Date());
        datestr = "§7"+datestr;

        try {
            Connection conn = SQL.sqlData.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select * from longinus.user");

            while(set.next()) {
                YamlConfiguration yaml = (new Converter()).decodeYaml(set.getString("alarms"));

                for(int i=49; i>=0; i--) {

                    int j = i+1;

                    if(i!=49) {
                        yaml.set(j+".content", yaml.getString(i+".content"));
                        yaml.set(j+".type", yaml.getString(i+".type"));
                        yaml.set(j+".date", yaml.getString(i+".date"));
                    }
                    if(i==0) {
                        yaml.set(i+".content", contents);
                        yaml.set(i+".type", type);
                        yaml.set(i+".date", datestr);
                    }
                }

                String encoded = (new Converter()).encodeYaml(yaml);
                String uuid = set.getString("uuid");

                try {
                    stmt = conn.createStatement();
                    stmt.executeUpdate("update longinus.user set alarms = '"+encoded+"' where uuid = '"+uuid+"'");
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }

            set.close();
            stmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
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

            if(!yaml.contains(Integer.toString(i))) continue;
            yaml.set(Integer.toString(i), null);
            break;
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);
    }

    public void removeAllAlarm() {

        YamlConfiguration yaml = getAlarmFile();

        for(int i=49; i>=0; i--) {
            yaml.set(Integer.toString(i), null);
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);
    }

    public int getAlarmAmount() {

        YamlConfiguration yaml = getAlarmFile();
        int amount = 0;

        for(int i=0; i<50; i++) {
            if(yaml.contains(Integer.toString(i))) amount++;
            //if(!yaml.getString(i+".content").equals("")) amount++;
        }
        return amount;
    }

    public List<String> getAlarmList(int location) {

        YamlConfiguration yaml = getAlarmFile();
        ArrayList<String> list = new ArrayList<>();

        if(!yaml.contains(Integer.toString(location))) return list;

        String contents = yaml.getString(location+".content");
        String dates = yaml.getString(location+".date");

        SimpleDateFormat currentdate = new SimpleDateFormat("yy/MM/dd HH:mm:ss", Locale.KOREA);
        String currentdates = currentdate.format(new Date());

        String splits[] = contents.split("\\*");

        list.addAll(Arrays.asList(splits));
        list.add("");
        list.add(dates);
        return list;
    }

    public String getAlarmType(int location) {

        YamlConfiguration yaml = getAlarmFile();

        if(!yaml.contains(Integer.toString(location))) return null;
        String type = yaml.getString(location+".type");

        return type;
    }

    //TODO 알람데이터 sql 전환

    private void sendToSQLServer(String encodedYaml) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set alarms = '"+encodedYaml+"' where uuid = '"+uuid+"'");

            stmt.close();

        }
        catch(Exception e) {

        }
    }



}
