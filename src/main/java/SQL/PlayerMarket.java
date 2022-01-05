package SQL;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static SQL.Connector.getConnection;

public class PlayerMarket {

    private Player player;
    private String uuid;

    public PlayerMarket(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public PlayerMarket(String uuid) {
        this.uuid = uuid;
    }

    public YamlConfiguration getMarketItemsFile() {
        try {
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select marketitems from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {
                String yaml = set.getString("marketitems");
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

    public void sendToSQLServer(YamlConfiguration rawYaml) {

        try {
            String encodedYaml = (new Converter()).encodeYaml(rawYaml);
            Connection conn = getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set marketitems = '"+encodedYaml+"' where uuid = '"+uuid+"'");

            stmt.close();
            //conn.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void registerItem(long date, ItemStack itemStack, int count, String uuid, long altera, long millis) {

        YamlConfiguration yaml = getMarketItemsFile();

        yaml.set("mainMarket."+date+".item", itemStack);
        yaml.set("mainMarket."+date+".count", count);
        yaml.set("mainMarket."+date+".uuid", uuid);
        yaml.set("mainMarket."+date+".altera", altera);
        yaml.set("mainMarket."+date+".millis", millis);

        sendToSQLServer(yaml);

    }

    public void deleteItem(String uuid) {

        YamlConfiguration yaml = getMarketItemsFile();
        for(String s : yaml.getConfigurationSection("mainMarket").getKeys(false)) {
            if(yaml.getString("mainMarket."+s+".uuid").equals(uuid)) {
                yaml.set("mainMarket."+s, null);
                break;
            }
        }
        sendToSQLServer(yaml);
    }





}
