package SQL;

import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PlayerAltera {

    private Player player;
    private String uuid;

    public PlayerAltera(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId().toString();
    }

    public long getAltera() {
        String uuid = player.getUniqueId().toString();

        try {
            Connection conn = (new SQL.sqlData()).getConnection();
            Statement stmt = conn.createStatement();
            ResultSet set = stmt.executeQuery("select altera from longinus.user where uuid = '"+uuid+"'");
            if(set.next()) {

                set.close();
                stmt.close();
                conn.close();

                return set.getLong("altera");
            }

            set.close();
            stmt.close();
            conn.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void setAltera(long altera) {
        String uuid = player.getUniqueId().toString();

        try {
            Connection conn = (new SQL.sqlData()).getConnection();
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("update longinus.user set altera = '"+altera+"' where uuid = '"+uuid+"'");

            stmt.close();
            conn.close();

        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
