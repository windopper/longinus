package PlanetSelect;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.UUID;

public enum PlanetList {

    Gliese581c("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTRjYmZmYjk3MTIyMzhjOTU2OTc3ZDFiZmRhNGIzMDhlZDQ3N2JhMzEwNmMzMTMwMmMyNDA4NjJlZjc3OWEifX19",
    "Gliese581c");

    private String skinURL;
    private String rawName;

    PlanetList(String URL, String rawName) {
        this.skinURL = URL;
        this.rawName = rawName;
    }

    public String getRawName() {
        return rawName;
    }

    public ItemStack getPlanet() {
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
