package SQL;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static SQL.Connector.getConnection;

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

    public void initUserStorages() {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("insert into longinus.userstorages (uuid) values ('"+uuid+"')");

            for(int i=1; i<=10; i++) {

                YamlConfiguration yaml = new YamlConfiguration();

                for(int j=0; j<54; j++) {
                    if(j%9<=6) {
                        yaml.set(Integer.toString(j), new ItemStack(Material.AIR, 1));
                    }
                }
                String encoded = (new Converter()).encodeYaml(yaml);
                stmt.executeUpdate("update longinus.userstorages set storage"+i+" = '"+encoded+"' where uuid = '"+uuid+"'");
            }

            stmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isDataExist() {

        Connector Connector = new Connector();

        try {
            String uuid = player.getUniqueId().toString();
            Connection conn = Connector.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select exists ( select * from longinus.userstorages where uuid = '"+uuid+
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

    public YamlConfiguration getStorageFile(String page) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select storage"+page+" from longinus.userstorages where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("storage"+page);

                if(yaml == null) {
                    set.close();
                    stmt.close();
                    return new YamlConfiguration();
                }

                YamlConfiguration config = (new SQL.Converter()).decodeYaml(yaml);

                set.close();
                stmt.close();
                //conn.close();

                return config;
            }

            set.close();
            stmt.close();
            //conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void storageSave(Inventory inv, String page) {

        YamlConfiguration yaml = getStorageFile(page);
        if(yaml == null) {
            yaml = new YamlConfiguration();
        }

        for(int i=0; i<54; i++) {
            if(i%9<=6) {
                yaml.set(Integer.toString(i), inv.getItem(i));
            }
        }

        String encoded = (new Converter()).encodeYaml(yaml);
        sendToSQLServer(encoded, page);

    }

    public Inventory storageCall(Inventory inv, String page) {

        YamlConfiguration yaml = getStorageFile(page);

        if(yaml == null) {
            return inv;
        }
        else {
            for(int i=0; i<54; i++) {
                if(i%9<=6) {
                    inv.setItem(i, yaml.getItemStack(Integer.toString(i)));
                }
            }
        }


        return inv;
    }

    public boolean checkStorageExist(String page) {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select storage"+page+" from longinus.userstorages where uuid = '"+uuid+"'");
            if(set.next()) {
                String result = set.getString("storage"+page);
                if(result.equals("null")) return false;
                else {
                    return true;
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return false;

//        YamlConfiguration yaml = getStorageFile(page);
//        return yaml.contains(page);
    }

    public int maxStoragePage() {

        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select storagelimit from longinus.userstorages where uuid = '"+uuid+"'");
            if(set.next()) {
                int max = set.getInt("storagelimit");

                set.close();
                stmt.close();

                return max;
            }

            set.close();
            stmt.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return 1;

    }

    private void sendToSQLServer(String encodedYaml, String page) {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.userstorages set storage"+page+" = '"+encodedYaml+"' where uuid = '"+uuid+"'");

            stmt.close();
            //conn.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
