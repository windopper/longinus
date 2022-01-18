package utils;

import PlayerManager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_17_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

public class CoolDownAbilityHandler {

    private Player player;
    private PlayerManager pm;
    private String abilityName;
    private int coolDown = 100;
    private Runnable runnable = () -> {};
    private Runnable onCoolDown = () -> {};
    private Runnable onUse = () -> {};
    private Runnable onReady = () -> {};

    private CoolDownAbilityHandler(Player player, String abilityName) {
        this.player = player;
        this.abilityName = abilityName;
        this.pm = PlayerManager.getinstance(player);
        this.onCoolDown = () -> {
            player.sendMessage("§c쿨다운까지 "+String.format("%.2f", ((double)coolDown-(double)pm.dummyIncrease.get(abilityName)) / 20)
                    +"초 남았습니다!");
        };
        this.onUse = () -> {
            player.sendMessage("사용");
        };

    }

    public static CoolDownAbilityHandler getHandler(Player player, String abilityName) {
        return new CoolDownAbilityHandler(player, abilityName);
    }

    public CoolDownAbilityHandler setCoolDown(int coolDown) {
        this.coolDown = coolDown;
        return this;
    }

    public CoolDownAbilityHandler setAbility(Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    public CoolDownAbilityHandler setOnCoolDownMessage(Runnable runnable) {
        this.onCoolDown = runnable;
        return this;
    }

    public CoolDownAbilityHandler setOnReadyMessage(Runnable runnable) {
        this.onReady = runnable;
        return this;
    }

    public CoolDownAbilityHandler run() {
        if(pm.dummyIncrease.containsKey(abilityName)) {
            onCoolDown.run();
            return this;
        }
        else {
            pm.dummyIncrease.put(abilityName, 0);
            runnable.run();
            onUse.run();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if(pm.dummyIncrease.get(abilityName)>coolDown) {
                        pm.dummyIncrease.remove(abilityName);
                        onReady.run();
                        cancel();
                    }
                }
            }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
        }
        return this;

    }
}
