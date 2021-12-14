package SpyGlass;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class SpyGlassItemManager {

    public enum SpyGlassPlanet {
        Gliese581c;
    }

    public ItemStack getSpyGlassItem(SpyGlassPlanet planet, int level) {
        ItemStack itemStack = new ItemStack(Material.SPYGLASS, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName("§5고성능 스캔망원경");
        itemMeta.setLore(Arrays.asList(
                "§8유틸리티 아이템",
                "",
                "§9"+planet.name()+"전용 아이템",
                "§e유효사거리:: §630  §e스캔시간:: §6 2초"
        ));

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
