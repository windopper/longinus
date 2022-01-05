package SQL;

import net.minecraft.nbt.NBTTagCompound;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.Statement;

public class MainMarket {

    String uuid;

    public MainMarket(String uuid) {
        this.uuid = uuid;
    }

    public MainMarket(ItemStack itemStack) {
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        NBTTagCompound tag = nmsStack.getTag();
        this.uuid = tag.getString("UUID");
    }

    public void deleteItem() {
        updateQuery("delete from longinus.mainmarket where uuid = '"+uuid+"'");
    }


    public void updateQuery(String query) {

        try {
            Connection connection = Connector.getConnection();
            Statement stmt = connection.createStatement();
            stmt.executeUpdate(query);

            stmt.close();
            //connection.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void registerItem(String uuid, long altera, String item, int count, String selleruuid, String itemName) {

        try {
            Connection con = Connector.getConnection();
            Statement statement = con.createStatement();
            int eu = statement.executeUpdate("insert into longinus.mainmarket values ('"+System.currentTimeMillis()+"', '"+itemName+"', '"+
                    uuid+"', '"+selleruuid+"', '"+altera+"', '"+count+"', '"+item+"', '"+(new java.sql.Timestamp(new java.util.Date().getTime()))+"')");

            statement.close();
            con.close();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
