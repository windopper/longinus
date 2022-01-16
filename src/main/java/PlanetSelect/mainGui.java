package PlanetSelect;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class mainGui {

    public final static List<Player> GuiViewer = new ArrayList<>();

    public void openGui(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 36, "이동할 행성을 선택하세요");
        inventory.setItem(10, gliese581c());


        player.openInventory(inventory);
    }

    private final ItemStack gliese581c() {

        ItemStack itemStack = PlanetList.Gliese581c.getPlanet();
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

    private final ItemStack getSkull(String skinURL) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        if(skinURL.isEmpty())return head;
        ItemMeta headMeta = head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", skinURL).getBytes());
        profile.getProperties().put("textures", new Property("textures", skinURL));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        profileField.setAccessible(true);
        try {
            profileField.set(headMeta, profile);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
    }
}
