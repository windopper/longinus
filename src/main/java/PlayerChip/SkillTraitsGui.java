package PlayerChip;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class SkillTraitsGui {

    public void skillTraitsGuiOpen(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 54, "스킬 특성");

        player.openInventory(inventory);
    }


}
