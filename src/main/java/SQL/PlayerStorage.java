package SQL;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PlayerStorage {

//    private final static String[] Storage = {
//            "0","1","2","3","4","5","6",
//            "9","10","11","12","13","14","15",
//            "18","19","20","21","22","23","24",
//            "27","28","29","30","31","32","33",
//            "36","37","38","39","40","41","42",
//            "45","46","47","48","49","50","51"};

    private Player player;
    private String uuid;

    public PlayerStorage(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

//    //public static String[] getStorageSlot() {
//        return Storage;
//    }

    public YamlConfiguration getStorageFile() {
        try {
            Connection conn = (new SQL.sqlData()).getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select storages from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("classes");
                YamlConfiguration config = (new SQL.Converter()).decodeYaml(yaml);
                return config;
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storageSave(Inventory inv, String page) {

        YamlConfiguration yaml = getStorageFile();

        for(int i=0; i<54; i++) {
            if(i%9<=6) {
                yaml.set(page+"."+i, inv.getItem(i));
            }
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded);

    }

    public Inventory storageCall(Inventory inv, String page) {

        YamlConfiguration yaml = getStorageFile();

        for(int i=0; i<54; i++) {
            if(i%9<=6) {
                inv.setItem(i, yaml.getItemStack(page+"."+i));
            }
        }

        return inv;
    }

    public boolean checkStorageExist(String page) {
        YamlConfiguration yaml = getStorageFile();
        return yaml.contains("storage"+page);
    }

    public int maxStoragePage() {
        int i=0;
        while(checkStorageExist(Integer.toString(i))) {
            i++;
        }
        i--;

        return i;
    }

    private void sendToSQLServer(String encodedYaml) {
        try {
            Connection conn = (new sqlData()).getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set storages = '"+encodedYaml+"' where = uuid+'"+uuid+"'");

            stmt.close();
            conn.close();

        }
        catch(Exception e) {

        }
    }
}
