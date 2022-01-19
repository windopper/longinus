package Items.Equipments;

import SQL.Connector;
import SQL.SQLManager;
import com.mysql.cj.protocol.Resultset;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.Arrays;

public class WeaponBuilder {

    private static WeaponBuilder weaponBuilder;
    private WeaponBuilder() {

    }
    public static WeaponBuilder getInstance() {
        if(weaponBuilder == null) weaponBuilder = new WeaponBuilder();
        return weaponBuilder;
    }

    private Integer getRandomPercent() {
        return (int)(Math.random() * 100) + 1;
    }

    public ItemStack getItemFromSQL(String Name) {
        ItemStack itemStack = new ItemStack(Material.STONE, 1);
        Connection conn = Connector.getConnection();
        try {
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery(
                    "select * from longinus.itemlist where name = '"+Name+"'"
            );

            itemStack = new ItemStack(Arrays.stream(Material.values())
                    .filter((v)->{
                        try {
                            return v.name().equals(set.getString("type"));
                        }
                        catch(Exception e) {
                        }
                        return false;
                    }).toList().get(0), 1);

            int[] stats = Arrays.stream(WeaponStats.values()).mapToInt((s)->getRandomPercent()).toArray();
            String[] infos = Arrays.stream(WeaponInfo.values()).map(s->{
                try {
                    return set.getString(s.name());
                }
                catch(Exception e) {}
                return "none";
            }).toArray(String[]::new);
        }
        catch(SQLException e) {
            e.printStackTrace();
        }

        return itemStack;
    }
}
