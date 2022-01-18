package utils;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import spellinteracttest.Main;

public class ItemNameTempChanger {


    /*

    작동 안됨
     */
    public static void Change(ItemStack itemStack, String changeName, int tempTick) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        net.minecraft.world.item.ItemStack nmsStack = CraftItemStack.asNMSCopy(itemStack);
        String originName = nmsStack.getTag().getString("이름");
        itemMeta.setDisplayName(changeName);
        itemStack.setItemMeta(itemMeta);
        Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class), () -> {
            itemMeta.setDisplayName(originName);
        }, tempTick);
    }
}
