package Mob.Gliese581cMobs;

import CustomEvents.CustomMobDeathEvent;
import Mob.MobListManager;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.EntityFox;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class Gliese581cEntitySummon implements Listener {

    @EventHandler
    public void CustomMobDeathEvent(CustomMobDeathEvent event) {

        Entity entity = event.getEntity();
        MobListManager.MobList mobList = event.getMobList();
        String mobvalue = mobList.name();

        // MouseFoot And FoxRat's parasite
        // Get Rand number
        double rand = Math.random();
        if(mobvalue.equals("마우스풋") || mobvalue.equals("폭스랫")) {
            org.bukkit.World world = entity.getWorld();
            Location loc = entity.getLocation();
            Parasite parasite = new Parasite(world, loc);
        }

    }

    public void summonGuard(Player player) {
        org.bukkit.World world = player.getWorld();
        Location loc = player.getLocation();

        Defender defender = new Defender(EntityTypes.aV, ((CraftWorld) world).getHandle(), loc);
    }

    public void summonBloodRoot(Player player) {
        Location loc = player.getEyeLocation();
        World nmsworld = ((CraftWorld) loc.getWorld()).getHandle();
        //MouseFoot mouseFoot = new MouseFoot(nmsworld);
        //FoxRat foxRat = new FoxRat(EntityTypes.E, nmsworld, player);

        BloodRoot bloodRoot = new BloodRoot(EntityTypes.aB, nmsworld);
        bloodRoot.setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    public void summonFoxRat(Player player) {
        Location loc = player.getEyeLocation();
        World nmsworld = ((CraftWorld) loc.getWorld()).getHandle();

        FoxMother fox = new FoxMother(EntityTypes.E, nmsworld);
        fox.setPosition(loc.getX(), loc.getY(), loc.getZ());

        for(int i=0; i<10; i++) {
            FoxRat foxRat = new FoxRat(EntityTypes.E, nmsworld, (EntityFox) fox);
            foxRat.setPosition(loc.getX(), loc.getY(), loc.getZ());
        }


    }

    public void summonHiddenOasis(Player player) {
        Location loc = player.getEyeLocation();
        World nmsworld = ((CraftWorld) loc.getWorld()).getHandle();
        //MouseFoot mouseFoot = new MouseFoot(nmsworld);
        HiddenOasis hiddenOasis = new HiddenOasis(EntityTypes.be, nmsworld);
        hiddenOasis.setPosition(loc.getX(), loc.getY(), loc.getZ());
    }

    public void summonGlowingButterFly(Player player) {
        Location loc = player.getEyeLocation();
        World nmsworld = ((CraftWorld) loc.getWorld()).getHandle();
        GlowingButterfly glowingButterfly =  new GlowingButterfly(EntityTypes.be, nmsworld, loc);
        glowingButterfly.setPosition(loc.getX(), loc.getY(), loc.getZ());

    }

    public void summonRageEagle(Location location) {
        World nmsworld = ((CraftWorld) location.getWorld()).getHandle();
        RageEagle rageEagle = new RageEagle(EntityTypes.g, nmsworld, location);
        rageEagle.setPosition(location.getX(), location.getY(), location.getZ());
    }

    public void summonMushBone(Location location) {
        World nmsworld = ((CraftWorld) location.getWorld()).getHandle();
        MushBone mushBone = new MushBone(EntityTypes.aD, nmsworld, location);
    }

    public void summonMob(Location location, String mobName) {
        World nmsworld = ((CraftWorld) location.getWorld()).getHandle();
        if(mobName.equals("dm")) {
            DessertMammoth dm = new DessertMammoth(EntityTypes.L, nmsworld, location);
        }
        else if(mobName.equals("sb")) {
            SanBag sb = new SanBag(EntityTypes.aB, nmsworld, location);
        }
        else if(mobName.equals("wf")) {
            WolfFang wf = new WolfFang(EntityTypes.bc, nmsworld, location);
        }
    }



}
