package EntityPlayerManager;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;

public class DummyEntityPlayer extends EntityPlayer {
    public DummyEntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile) {
        super(minecraftserver, worldserver, gameprofile);
    }
}
