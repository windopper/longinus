package PlayerChip;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class BioChipsGui {

    public void BioChipsGuiOpen(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, "생체칩");

        player.openInventory(inventory);
    }
}
