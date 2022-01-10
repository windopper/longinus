package GUIManager;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.util.HashSet;
import java.util.Set;

public class GUIManager implements Listener {

    private static final Set<String> clickCanceller = new HashSet<>();

    @EventHandler
    public void UIClick1(InventoryDragEvent event) {
        for(String s : clickCanceller) {
            if(event.getView().getTitle().contains(s)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void UIClick2(InventoryInteractEvent event) {
        for(String s : clickCanceller) {
            if(event.getView().getTitle().contains(s)) event.setCancelled(true);
        }
    }

    @EventHandler
    public void UIClick3(InventoryClickEvent event) {
        for(String s : clickCanceller) {
            if(event.getView().getTitle().contains(s)) event.setCancelled(true);
        }
    }

    public static void setClickCanceller(String s) {
        clickCanceller.add(s);
    }


}
