package PlanetSelect;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class mainGui {

    public final static List<Player> GuiViewer = new ArrayList<>();

    public void openGui(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 36, "이동할 행성을 선택하세요");

        inventory.setItem(10, gliese581c());


        player.openInventory(inventory);
    }

    public ItemStack gliese581c() {

        ItemStack itemStack = new ItemStack(Material.SAND, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§6>>§e> §l§oGliese581c§r §e<§6<<");
        itemMeta.setLore(Arrays.asList("§7-----------------------------",
                "§d-[ 클릭하여 행성으로 이동하세요 ]-",
                "",
                "",
                ""));
        itemStack.setItemMeta(itemMeta);

        return itemStack;

    }
}
