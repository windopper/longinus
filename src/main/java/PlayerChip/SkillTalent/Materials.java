package PlayerChip.SkillTalent;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Materials {

    private static Materials materials;

    private Materials() {

    }

    public static Materials getInstance() {
        if(materials == null) materials = new Materials();
        return materials;
    }

    public ItemStack unselected() {
        ItemStack itemStack = new ItemStack(Material.BOOK, 1);
        return itemStack;
    }

    public ItemStack selected() {
        ItemStack itemStack = new ItemStack(Material.ENCHANTED_BOOK, 1);
        return itemStack;
    }

    public ItemStack skillSelect() {
        ItemStack itemStack = new ItemStack(Material.KNOWLEDGE_BOOK, 1);
        return itemStack;
    }

    public ItemStack notOpened() {
        ItemStack itemStack = new ItemStack(Material.BARRIER, 1);
        return itemStack;
    }
}
