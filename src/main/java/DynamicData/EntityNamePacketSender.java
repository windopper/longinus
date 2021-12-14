package DynamicData;

public class EntityNamePacketSender {

//    private static EntityNamePacketSender instance;
//
//    private EntityNamePacketSender() {
//
//    }
//
//    public static EntityNamePacketSender getInstance() {
//        if(instance==null) instance = new EntityNamePacketSender();
//        return instance;
//    }
//
//    public void SendPacket(LivingEntity e) {
//
//        for(Player player : Bukkit.getOnlinePlayers()) {
//
//            PlayerConnection conn = ((CraftPlayer) player).getHandle().b;
//
//            String uuid = player.getUniqueId().toString();
//            String username = player.getName();
//
//            File file = new File(Bukkit.getPluginManager().getPlugin("spellinteract").getDataFolder(), uuid+".yml");
//            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
//
//            Arrays.stream(MobManager.MobList.values()).forEach(value -> {
//                if(value.getName().equals(EntityHealthManager.getinstance(e).getCustomName())) {
//                    int intvalue = config.getInt("Sample."+value.getPlanet()+"."+value.name()+".count");
//                    if(intvalue == 0) {
//
//                        int namespace = value.getRawName().length();
//
//                        String name = "§6[Lv.???] §c";
//                        for(int i=0; i<namespace; i++) {
//                            name +="?";
//                        }
//
//                        ChatComponentText cct = new ChatComponentText(name);
//                        Optional<ChatComponentText> opt = Optional.of(cct);
//
//                        DataWatcher d = ((CraftEntity) e).getHandle().getDataWatcher();
//                        d.set(new DataWatcherObject<>(2, DataWatcherRegistry.), opt);
//                        conn.sendPacket(new PacketPlayOutEntityMetadata(e.getEntityId(), d, true));
//
//                    }
//
//                }
//                else {
//
//                }
//            });
//
//        }
//    }



}
