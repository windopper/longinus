package utils;

import Exceptions.UndefinedFunctionError;
import PlayerManager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import spellinteracttest.Main;

import java.util.function.Function;
import java.util.function.Supplier;

public class DuraAbilityHandler {

    private Player player;
    private PlayerManager pm;
    private String abilityName = "";
    private boolean isPersistent = true;
    private int tick = 20;
    private int stack = 5;
    private Runnable runnableInitialize;
    private Runnable runnableFinalize;
    private Supplier<Boolean> stopCondition = () -> false;

    public DuraAbilityHandler(Player player, String abilityName) {
        this.player = player;
        this.pm = PlayerManager.getinstance(player);
        this.abilityName = abilityName;
    }
    public static DuraAbilityHandler getHandler(Player player, String abilityName) {
        return new DuraAbilityHandler(player, abilityName);
    }
    public DuraAbilityHandler isPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
        return this;
    }
    public DuraAbilityHandler setTick(int tick) {
        this.tick = tick;
        return this;
    }
    public DuraAbilityHandler setMaximumStack(int stack) {
        this.stack = stack;
        return this;
    }
    public DuraAbilityHandler setRunnable(Runnable initialize, Runnable finalize) {
        this.runnableInitialize = initialize;
        this.runnableFinalize = finalize;
        return this;
    }
    public DuraAbilityHandler setStopCondition(Supplier<Boolean> stopCondition) {
        this.stopCondition = stopCondition;
        return this;
    }

    public DuraAbilityHandler run() {
        if(pm.dummyCount.stream().filter((a)->a.contains(abilityName)).toList().size()<stack) {
            runnableInitialize.run();
            pm.dummyCount.add(abilityName);
            if(!pm.dummyIncrease.containsKey(abilityName)) {
                pm.dummyIncrease.put(abilityName, 0);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        pm.dummyIncrease.replace(abilityName, pm.dummyIncrease.get(abilityName)+1);
                        if(pm.dummyIncrease.get(abilityName)>=tick || stopCondition.get()) {
                            pm.dummyIncrease.remove(abilityName);
                            while(pm.dummyCount.contains(abilityName)) {
                                runnableFinalize.run();
                                pm.dummyCount.remove(abilityName);
                            }
                            cancel();
                        }
                    }
                }.runTaskTimer(Main.getPlugin(Main.class), 0, 1);
            }
            else {
                if(isPersistent) pm.dummyIncrease.replace(abilityName, 0);
            }
        }
        return this;
    }
}
