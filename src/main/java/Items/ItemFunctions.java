package Items;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ItemFunctions {

    public ItemStack setUntradable(ItemStack itemStack) {
        return itemStack;
    }

    public ItemStack setQuestItem(ItemStack itemStack) {

        List<String> lores = itemStack.getItemMeta().getLore();
        lores.add("");
        lores.add("§c퀘스트 아이템");
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setLore(lores);
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

}
