package SQL;

import org.bukkit.inventory.ItemStack;

import java.sql.*;

public class Connector {
    private static Connection con;
    private static final String server = "localhost";
    private static final String user_name = "root"; //  접속자 id
    private static final String password = ""; // 접속자 pw
    public static final String servername = "longinus";
    private static final String table = "mainmarket";

//    public static void main(String[] args) {
//        sqlData s = new sqlData();
//        s.QueryDeleteItemFromMarket("d");
//    }

    public Connector() {

    }

    public static Connection getConnection() {
        try {

            if(con == null || con.isClosed())
                con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=UTC"
                        , user_name, password);
            return con;

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void closeConnection() {

        try {
            con.close();
        }
        catch(Exception e) {

        }
    }


    public void QueryLogMarket(long altera, ItemStack itemStack, int count, String seller, String buyer, String selltime) {

        String name = itemStack.getItemMeta().getDisplayName();
        String encodeditem = (new Converter()).encodeItem(itemStack);
        java.sql.Timestamp buytime = new java.sql.Timestamp(new java.util.Date().getTime());

        try {
            Connection conn = getConnection();
            Statement statement = conn.createStatement();
            statement.executeUpdate("insert into "+servername+".mainmarketlog"+" values ('"+
                    name+"', '"+altera+"', '"+encodeditem+"', '"+count+"', '"+seller+"', '"+
                    buyer+"', '"+selltime+"', '"+buytime+"', '"+System.currentTimeMillis()+"')");

            statement.close();
//            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
