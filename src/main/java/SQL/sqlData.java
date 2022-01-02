package SQL;

import org.bukkit.inventory.ItemStack;

import java.sql.*;

public class sqlData {
    private Connection con;
    private final String server = "localhost"; // 서버 주소
    private final String user_name = "root"; //  접속자 id
    private final String password = "mysqlternis02@@@"; // 접속자 pw
    public final String servername = "longinus";
    private final String table = "mainmarket";

//    public static void main(String[] args) {
//        sqlData s = new sqlData();
//        s.QueryDeleteItemFromMarket("d");
//    }

    public sqlData() {

    }

    public Connection getConnection() {
        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=UTC"
                    , user_name, password);

            return con;

        } catch(SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void QueryRegisterMarket(String uuid, long altera, String item, int count, String selleruuid) {

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=UTC"
                    , user_name, password);

        } catch(SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement statement = con.createStatement();
            int eu = statement.executeUpdate("insert into "+servername+"."+table+" values ('"+System.currentTimeMillis()+"', '" +
                    uuid+"', '"+selleruuid+"', '"+altera+"', '"+count+"', '"+item+"', '"+(new java.sql.Timestamp(new java.util.Date().getTime()))+"')");

            statement.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}
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
            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ResultSet SelectQuery(String query) {

        try {
            con = DriverManager.getConnection("jdbc:mysql://" + server + "/" + "?useSSL=false&allowPublicKeyRetrieval=true&useUnicode=true&serverTimezone=UTC"
                    , user_name, password);

        } catch(SQLException e) {
            e.printStackTrace();
        }

        try {
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            statement.close();
            return resultSet;
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}

        return null;
    }

    public void QueryEnd() {
        try {
            if(con != null)
                con.close();
        } catch (SQLException e) {}
    }
}
