package ClassAbility;

import DynamicData.Damage;
import Mob.EntityStatusManager;
import PlayerManager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class SpellManager {

    final private Player player;
    private Location loc;
    private Vector dir;

    final private double multiply;


    private boolean EntityPassable = false;
    private boolean WallPassable = true;
    private int MaximumRange = 5;
    private double HitBoxRange = 1.5;

    private double DamageRate = 1;
    private double multiplyDamage = 1;
    private double addDamage = 0;

    private double KnockBackRate = 0;
    private Entity KnockBackStandard;

    private int StunTick = 0;

    private int BurnTick = 0;
    private int BurnDamage = 0;
    private double BurnDamageRate = 1;

    private Sound HitSound = Sound.ENTITY_ARROW_HIT_PLAYER;

    private List<Entity> HitEntityList = new ArrayList<>();

    private final List<Particle> ParticleTrail = new ArrayList<>();
    private final List<Integer> ParticleTrailAmount = new ArrayList<>();
    private final List<Double> ParticleTrailX = new ArrayList<>();
    private final List<Double> ParticleTrailY = new ArrayList<>();
    private final List<Double> ParticleTrailZ = new ArrayList<>();
    private final List<Double> ParticleTrailSpeed = new ArrayList<>();
    private final List<Object> ParticleTrailDustOptions = new ArrayList<>();

    private Method trailMethod;
    private Object instance;


    private final List<Particle> ParticleDest = new ArrayList<>();
    private final List<Integer> ParticleDestAmount = new ArrayList<>();
    private final List<Double> ParticleDestX = new ArrayList<>();
    private final List<Double> ParticleDestY = new ArrayList<>();
    private final List<Double> ParticleDestZ = new ArrayList<>();
    private final List<Double> ParticleDestSpeed = new ArrayList<>();
    private final List<Object> ParticleDestDustOptions = new ArrayList<>();

    private final List<Particle> ParticleDepart = new ArrayList<>();
    private final List<Integer> ParticleDepartAmount = new ArrayList<>();
    private final List<Double> ParticleDepartX = new ArrayList<>();
    private final List<Double> ParticleDepartY = new ArrayList<>();
    private final List<Double> ParticleDepartZ = new ArrayList<>();
    private final List<Double> ParticleDepartSpeed = new ArrayList<>();
    private final List<Object> ParticleDepartDustOptions = new ArrayList<>();


    private final List<Sound> SoundDepart = new ArrayList<>();
    private final List<Float> VolumneDepart = new ArrayList<>();
    private final List<Float> PitchDepart = new ArrayList<>();

    private final List<Sound> SoundDest = new ArrayList<>();
    private final List<Float> VolumneDest = new ArrayList<>();
    private final List<Float> PitchDest= new ArrayList<>();

    public enum SpellType {
        SelfRange,
        HitScan
    }

    public enum MeleeOrSpell {
        Melee,
        Spell
    }

    private enum ParticleType {
        DepartureParticle,
        TrailParticle,
        DestinationParticle,
        WallParticle
    }

    private enum SoundType {
        DepartureSound,
        DestinationSound,
        WallSound
    }

    public SpellManager(Player player, double multiply) {
        this.player = player;
        loc = player.getEyeLocation();
        dir = loc.getDirection().normalize().multiply(multiply);
        this.multiply = multiply;
    }
    public SpellManager(Player player) {
        this.player = player;
        loc = player.getEyeLocation();
        dir = loc.getDirection().normalize().multiply(0.1);
        this.multiply = 0.1;
    }
    public SpellManager(Player player, Location EyeLoc, double multiply) {
        this.player = player;
        loc = EyeLoc;
        dir = loc.getDirection().normalize().multiply(multiply);
        this.multiply = multiply;
    }

    public void setEntityPassable(boolean value) {
        this.EntityPassable = value;
    }

    public void setWallPassable(boolean value) { this.WallPassable = value; }

    public void setDamageRate(double DamageRate) {
        this.DamageRate = DamageRate;
    }

    public void setmultiplyDamage(double Damage) { this.multiplyDamage = Damage; }

    public void setaddDamage(double Damage) { this.addDamage = Damage; }

    public void setMaximumRange(int Range) {
        this.MaximumRange = Range;
    }

    public void setHitSound(Sound HitSound) {
        this.HitSound = HitSound;
    }

    public void setKnockBack(Entity KnockBackStandard, double KnockBackRate) {
        this.KnockBackStandard = KnockBackStandard;
        this.KnockBackRate = KnockBackRate;
    }

    public void setStun(int StunTick) {
        this.StunTick = StunTick;
    }

    public void setBurn(int BurnTick, int BurnDamage, double BurnDamageRate) {
        this.BurnTick = BurnTick;
        this.BurnDamage = BurnDamage;
        this.BurnDamageRate = BurnDamageRate;
    }

    public void setBurn(int BurnTick, double BurnDamageRate) {
        this.BurnTick = BurnTick;
        this.BurnDamageRate = BurnDamageRate;
    }

    public void addTrailParticle(Particle ParticleTrail,
                                 int Amount,
                                 double X,
                                 double Y,
                                 double Z,
                                 double Speed,
                                 Object Options)
    {

        this.ParticleTrail.add(ParticleTrail);
        this.ParticleTrailAmount.add(Amount);
        this.ParticleTrailX.add(X);
        this.ParticleTrailY.add(Y);
        this.ParticleTrailZ.add(Z);
        this.ParticleTrailSpeed.add(Speed);
        this.ParticleTrailDustOptions.add(Options);
    }

    public void addTrailParticle(Particle ParticleTrail)
    {

        this.ParticleTrail.add(ParticleTrail);
        this.ParticleTrailAmount.add(1);
        this.ParticleTrailX.add(0d);
        this.ParticleTrailY.add(0d);
        this.ParticleTrailZ.add(0d);
        this.ParticleTrailSpeed.add(0d);
        this.ParticleTrailDustOptions.add(null);
    }
    public void setTrailMethod(Method method) {
        this.trailMethod = method;
    }
    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public void addDestinationParticle(Particle ParticleDest,
                                       int Amount,
                                       double X,
                                       double Y,
                                       double Z,
                                       double Speed,
                                       Object Options)
    {

        this.ParticleDest.add(ParticleDest);
        this.ParticleDestAmount.add(Amount);
        this.ParticleDestX.add(X);
        this.ParticleDestY.add(Y);
        this.ParticleDestZ.add(Z);
        this.ParticleDestSpeed.add(Speed);
        this.ParticleDestDustOptions.add(Options);
    }

    public void addDepartParticle(Particle ParticleDepart,
                                  int Amount,
                                  double X,
                                  double Y,
                                  double Z,
                                  double Speed,
                                  Object Options)
    {

        this.ParticleDepart.add(ParticleDepart);
        this.ParticleDepartAmount.add(Amount);
        this.ParticleDepartX.add(X);
        this.ParticleDepartY.add(Y);
        this.ParticleDepartZ.add(Z);
        this.ParticleDepartSpeed.add(Speed);
        this.ParticleDepartDustOptions.add(Options);
    }

    public void addDepartSound(Sound Sound, float Volume, float Pitch) {
        this.SoundDepart.add(Sound);
        this.VolumneDepart.add(Volume);
        this.PitchDepart.add(Pitch);
    }
    public void addDestinationSound(Sound Sound, float Volume, float Pitch) {
        this.SoundDest.add(Sound);
        this.VolumneDest.add(Volume);
        this.PitchDest.add(Pitch);
    }

    public void setHitBoxRange(double HitBoxRange) {
        this.HitBoxRange = HitBoxRange;
    }

    public List<Entity> getHitEntityList() {
        return HitEntityList;
    }

    public Location getHitLocation() {

        for(int i=0; i<MaximumRange / multiply; i++) {
            for (LivingEntity e : player.getWorld().getLivingEntities()) {
                Location eloc = e.getBoundingBox().getCenter().toLocation(e.getWorld());
                double dist = eloc.distance(loc);
                if (dist < HitBoxRange || e.getBoundingBox().contains(loc.getX(), loc.getY(), loc.getZ())) {
                    if (entitycheck.entitycheck(e) && entitycheck.duelcheck(e, player) && e != player) {
                        return loc;
                    }
                }
            }
            loc.add(dir);
        }

        return loc;
    }

    public boolean RunRayCast(MeleeOrSpell meleeorspell) {

        RunParticles(ParticleType.DepartureParticle);
        RunSounds(SoundType.DepartureSound);

        for(int i=0; i<MaximumRange / multiply; i++) {

            RunParticles(ParticleType.TrailParticle);
            RunTrailMethod(loc, i);
            
            // 벽 체크
            if(WallChecker(loc)) {

                RunParticles(ParticleType.DestinationParticle);
                RunSounds(SoundType.DestinationSound);
                return true;
            }
            // 엔티티 체크
            for (LivingEntity e : player.getWorld().getLivingEntities()) {

                Location eloc = e.getBoundingBox().getCenter().toLocation(e.getWorld());
                double dist = eloc.distance(loc);
                if (dist < HitBoxRange || e.getBoundingBox().contains(loc.getX(), loc.getY(), loc.getZ())) {
                    if (entitycheck.entitycheck(e) && entitycheck.duelcheck(e, player) && e != player
                            && !HitEntityList.contains(e)) {

                        player.playSound(player.getLocation(), HitSound, 0.5f, 2f);

                        int dmg = getDamage(meleeorspell);

                        if(HitEntityList.isEmpty()) {
                            RunParticles(ParticleType.DestinationParticle);
                            RunSounds(SoundType.DestinationSound);
                        }
                        if(StunTick != 0)
                            EntityStatusManager.getinstance(e).Stun(player, StunTick);

                        if(BurnTick != 0)
                            EntityStatusManager.getinstance(e).burns(player, BurnTick, BurnDamage + (int)(dmg * BurnDamageRate));

                        if(KnockBackStandard != null)
                            EntityStatusManager.getinstance(e).KnockBack(KnockBackStandard, KnockBackRate);

                        HitEntityList.add(e);
                        Damage.getinstance().taken(dmg, e, player);
                    }
                }


                if(EntityPassable == false && HitEntityList.size() >= 1) return true;
            }
            loc.add(dir);

        }

        return false;
    }

    public boolean RunRadiusRange(MeleeOrSpell meleeorspell, Location CurrentLoc) {

        RunParticles(ParticleType.TrailParticle);
        // 벽 체크
        if(WallChecker(CurrentLoc) == true) {
            RunParticles(ParticleType.DestinationParticle, CurrentLoc);
            RunSounds(SoundType.DestinationSound, CurrentLoc);
            return true;
        }

        for (LivingEntity e : player.getWorld().getLivingEntities()) {

            Location eloc = e.getBoundingBox().getCenter().toLocation(e.getWorld());
            double dist = eloc.distance(CurrentLoc);
            if (dist < HitBoxRange || e.getBoundingBox().contains(loc.getX(), loc.getY(), loc.getZ())) {
                if (entitycheck.entitycheck(e) && entitycheck.duelcheck(e, player) && e != player
                        && !HitEntityList.contains(e)) {

                    player.playSound(player.getLocation(), HitSound, 0.5f, 2f);

                    int dmg = getDamage(meleeorspell);

                    RunParticles(ParticleType.DestinationParticle, CurrentLoc);
                    RunSounds(SoundType.DestinationSound, CurrentLoc);

                    if(StunTick != 0)
                        EntityStatusManager.getinstance(e).Stun(player, StunTick);

                    if(KnockBackStandard != null)
                        EntityStatusManager.getinstance(e).KnockBack(KnockBackStandard, KnockBackRate);

                    if(BurnTick != 0)
                        EntityStatusManager.getinstance(e).burns(player, BurnTick, BurnDamage + (int)(dmg * BurnDamageRate));

                    HitEntityList.add(e);
                    Damage.getinstance().taken(dmg, e, player);
                }
            }



        }

        if(HitEntityList.size() >= 1) return true;


        loc.add(dir);

        return false;
    }

    public boolean RunVectorCast() {

        return false;
    }


    private void RunParticles(ParticleType Type) {

        if(Type.equals(ParticleType.DepartureParticle)) {

            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int k=0; k<ParticleDepart.size(); k++) {
                    p.spawnParticle(ParticleDepart.get(k), loc,
                            (int)ParticleDepartAmount.get(k),
                            (double)ParticleDepartX.get(k),
                            (double)ParticleDepartY.get(k),
                            (double)ParticleDepartZ.get(k),
                            (double)ParticleDepartSpeed.get(k),
                            ParticleDepartDustOptions.get(k));
                }
            }

        }
        else if(Type.equals(ParticleType.TrailParticle)) {

            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int k=0; k<ParticleTrail.size(); k++) {
                    p.spawnParticle(ParticleTrail.get(k), loc,
                            (int)ParticleTrailAmount.get(k),
                            (double)ParticleTrailX.get(k),
                            (double)ParticleTrailY.get(k),
                            (double)ParticleTrailZ.get(k),
                            (double)ParticleTrailSpeed.get(k),
                            ParticleTrailDustOptions.get(k));
                }
            }

        }
        else if(Type.equals(ParticleType.DestinationParticle)) {

            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int k=0; k<ParticleDest.size(); k++) {
                    p.spawnParticle(ParticleDest.get(k), loc,
                            (int)ParticleDestAmount.get(k),
                            (double)ParticleDestX.get(k),
                            (double)ParticleDestY.get(k),
                            (double)ParticleDestZ.get(k),
                            (double)ParticleDestSpeed.get(k),
                            ParticleDestDustOptions.get(k));
                }
            }

        }
    }

    private void RunParticles(ParticleType Type, Location CurrentLoc) {

        if(Type.equals(ParticleType.DepartureParticle)) {

            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int k=0; k<ParticleDepart.size(); k++) {
                    p.spawnParticle(ParticleDepart.get(k), CurrentLoc,
                            (int)ParticleDepartAmount.get(k),
                            (double)ParticleDepartX.get(k),
                            (double)ParticleDepartY.get(k),
                            (double)ParticleDepartZ.get(k),
                            (double)ParticleDepartSpeed.get(k),
                            ParticleDepartDustOptions.get(k));
                }
            }

        }
        else if(Type.equals(ParticleType.TrailParticle)) {

            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int k=0; k<ParticleTrail.size(); k++) {
                    p.spawnParticle(ParticleTrail.get(k), CurrentLoc,
                            (int)ParticleTrailAmount.get(k),
                            (double)ParticleTrailX.get(k),
                            (double)ParticleTrailY.get(k),
                            (double)ParticleTrailZ.get(k),
                            (double)ParticleTrailSpeed.get(k),
                            ParticleTrailDustOptions.get(k));
                }
            }

        }
        else if(Type.equals(ParticleType.DestinationParticle)) {

            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int k=0; k<ParticleDest.size(); k++) {
                    p.spawnParticle(ParticleDest.get(k), CurrentLoc,
                            (int)ParticleDestAmount.get(k),
                            (double)ParticleDestX.get(k),
                            (double)ParticleDestY.get(k),
                            (double)ParticleDestZ.get(k),
                            (double)ParticleDestSpeed.get(k),
                            ParticleDestDustOptions.get(k));
                }
            }

        }
    }

    public void RunTrailMethod(Location loc, int i) {
        if(trailMethod == null) return;
        try {
            trailMethod.invoke(instance, loc, i);
        }
        catch(Exception e) {

        }
    }

    private void RunSounds(SoundType Type) {
        if(Type.equals(SoundType.DepartureSound)) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int i = 0; i< SoundDepart.size(); i++){
                    p.playSound(player.getLocation(), SoundDepart.get(i), VolumneDepart.get(i), PitchDepart.get(i));
                }
            }
        }
        else if(Type.equals(SoundType.DestinationSound)) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int i = 0; i< SoundDest.size(); i++){
                    p.playSound(loc, SoundDest.get(i), VolumneDest.get(i), PitchDest.get(i));
                }
            }
        }
    }

    private void RunSounds(SoundType Type, Location CurrentLoc) {
        if(Type.equals(SoundType.DepartureSound)) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int i = 0; i< SoundDepart.size(); i++){
                    p.playSound(player.getLocation(), SoundDepart.get(i), VolumneDepart.get(i), PitchDepart.get(i));
                }
            }
        }
        else if(Type.equals(SoundType.DestinationSound)) {
            for(Player p : Bukkit.getOnlinePlayers()) {
                for(int i = 0; i< SoundDest.size(); i++){
                    p.playSound(CurrentLoc, SoundDest.get(i), VolumneDest.get(i), PitchDest.get(i));
                }
            }
        }
    }

    private Integer getDamage(MeleeOrSpell meleeorspell) {

        int dmg = meleeorspell.equals(MeleeOrSpell.Melee) ? PlayerManager.getinstance(player).meleedmgcalculate(player, DamageRate)
                : PlayerManager.getinstance(player).spelldmgcalculate(player, DamageRate);
        dmg = (int)(dmg * multiplyDamage);
        dmg += (int)addDamage;
        return dmg;
    }

    public boolean WallChecker(Location loc) {
        // 벽 체크
        if(WallPassable == false && !loc.getBlock().isPassable()) {
            return true;
        }
        return false;
    }





}
