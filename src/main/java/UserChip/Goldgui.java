package UserChip;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

public class Goldgui {
	
	public static final HashMap<Player, String> insertvalue = new HashMap<>();
	
	public void GoldGuiOpen(Player sender, Player receiver) {
		
		Inventory gui = Bukkit.createInventory(null, 45, "값을 입력해주세요");
		
		
		gui.setItem(0, getSkull("3f09018f46f349e553446946a38649fcfcf9fdfd62916aec33ebca96bb21b5")); // 0번
		
		
		sender.openInventory(gui);
	}
	
	
    public ItemStack getSkull(String skinURL) {
        ItemStack head = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short)3);
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
