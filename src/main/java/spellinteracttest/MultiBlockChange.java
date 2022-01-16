package spellinteracttest;

import net.minecraft.server.network.PlayerConnection;
import net.minecraft.world.entity.EnumMoveType;
import net.minecraft.world.entity.item.EntityFallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftFallingBlock;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;

public class MultiBlockChange {

    public void test(Player player) {

        Location loc = player.getLocation();

        HashMap<Location, Material> blocks = new HashMap<>();
        HashMap<Location, BlockData> bdata = new HashMap<>();

        double x1 = -244;
        double y1 = 125;
        double z1 = 269;

        double x2 = -227;
        double y2 = 136;
        double z2 = 233;

        for(double x = x1; x<=x2; x++) {
            for(double y = y1; y<=y2; y++) {
                for(double z = z2; z<=z1; z++) {
                    Location location = new Location(Bukkit.getWorld("gliese581c"), x, y, z);
                    blocks.put(location, location.getBlock().getType());
                    bdata.put(location, location.getBlock().getBlockData());
                }
            }
        }

        new BukkitRunnable() {

            int time = 0;

            @Override
            public void run() {

                for(Location loc : blocks.keySet()) {
                    player.sendBlockChange(loc, Material.AIR.createBlockData());
                }
                for(Location loc : blocks.keySet()) {
                    player.sendBlockChange(loc.clone().add(0, time, 0), bdata.get(loc) == null ? blocks.get(loc).createBlockData() : bdata.get(loc));
                }

                if(time>10) cancel();
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"),0, 10);


//        for(int i=0; i<5; i++) {
//            player.sendBlockChange(loc.clone().add(0, i, 0), Material.STONE.createBlockData());
//        }

    }

    public void SmoothMultiBlockChange(Player player) {

        Location locatio = player.getLocation();

        PlayerConnection connection = ((CraftPlayer) player).getHandle().b;

        HashMap<Location, Material> blocks = new HashMap<>();
        HashMap<Location, BlockData> bdata = new HashMap<>();
        HashMap<Location, FallingBlock> fblock = new HashMap<>();



        double x1 = -244;
        double y1 = 125;
        double z1 = 233;

        double x2 = -227;
        double y2 = 136;
        double z2 = 269;


//        double x1 = -247;
//        double y1 = 128;
//        double z1 = 268;
//
//        double x2 = -244;
//        double y2 = 130;
//        double z2 = 271;

        for(double x = x1; x<=x2; x++) {
            for(double y = y1; y<=y2; y++) {
                for(double z = z1; z<=z2; z++) {
                    Location location = new Location(Bukkit.getWorld("gliese581c"), x, y, z);
                    blocks.put(location, location.getBlock().getType());
                    bdata.put(location, location.getBlock().getBlockData());
                }
            }
        }
        for(Location loc : blocks.keySet()) {
            FallingBlock fallingBlock = player.getWorld().spawnFallingBlock(loc.clone().add(0, 10, 0), bdata.get(loc) == null ? blocks.get(loc).createBlockData() : bdata.get(loc));
            fallingBlock.setHurtEntities(false);
            fallingBlock.setInvulnerable(false);
            fallingBlock.setDropItem(false);
            fallingBlock.setGravity(false);

            fblock.put(loc, fallingBlock);
            EntityFallingBlock entityfallingBlock = ((CraftFallingBlock) fallingBlock).getHandle();
            entityfallingBlock.setNoGravity(true);
            entityfallingBlock.setMot(0d, 0.1d, 0d);
            entityfallingBlock.move(EnumMoveType.a, entityfallingBlock.getMot());

//            EntityFallingBlock f = new EntityFallingBlock(((CraftWorld) player.getWorld()).getHandle()
//                    , loc.getX(), loc.getY(), loc.getZ(), CraftMagicNumbers.getBlock(bdata.get(loc) == null ? blocks.get(loc).createBlockData() : bdata.get(loc).getMaterial()));

//            FallingBlock fb = (FallingBlock) f.getBukkitEntity();
//            connection.sendPacket(new PacketPlayOutSpawnEntity(f));


            //((CraftWorld) player.getWorld()).getHandle().addEntity(f);
        }


        for(Location loc : blocks.keySet()) {
            player.sendBlockChange(loc, Material.AIR.createBlockData());
        }

         new BukkitRunnable() {

            double time = 0;

            @Override
             public void run() {

                for(Location loc : fblock.keySet()) {
                    if(time<200)
                        fblock.get(loc).setVelocity(new Vector(0, 0.2, 0));
                    else
                        fblock.get(loc).setVelocity(new Vector(0.3, 0, 0));
                    fblock.get(loc).setTicksLived(1);
                }

                if(time>400) {
                    for(FallingBlock f : fblock.values()) {
                        f.remove();
                    }
                    cancel();
                }
                time++;

            }
         }.runTaskTimer(Bukkit.getPluginManager().getPlugin("spellinteract"), 0, 1);

    }

}
