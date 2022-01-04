package SQL;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.yaml.snakeyaml.util.UriEncoder;

import java.util.Base64;

public class Converter {

    public String encodeItem(ItemStack itemstack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemstack);

        String configstring = config.saveToString();
        try {
            configstring = UriEncoder.encode(configstring);
            configstring = Base64.getEncoder().encodeToString(configstring.getBytes());
            return configstring;
        }
        catch(Exception e) {

        }

        return null;
    }

    public ItemStack decodeItem(String string) {
        YamlConfiguration config = new YamlConfiguration();

        string = new String(Base64.getDecoder().decode(string.getBytes()));
        string = UriEncoder.decode(string);

        try {
            config.loadFromString(string);
        }
        catch(Exception e) {
            return null;
        }

        return config.getItemStack("i");
    }

    public String encodeYaml(YamlConfiguration file) {
        String yaml = file.saveToString();
        yaml = UriEncoder.encode(yaml);
        yaml = Base64.getEncoder().encodeToString(yaml.getBytes());
        return yaml;
    }

    public YamlConfiguration decodeYaml(String yaml) {

        if(yaml == null || yaml.equals("null")) return new YamlConfiguration();
        YamlConfiguration config = new YamlConfiguration();
        yaml = new String(Base64.getDecoder().decode(yaml.getBytes()));
        yaml = UriEncoder.decode(yaml);
        try {
            config.loadFromString(yaml);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return config;
    }

    public String coordToString(Location loc) {

        String world = loc.getWorld().getName();
        String x = Double.toString(loc.getX());
        String y = Double.toString(loc.getY());
        String z = Double.toString(loc.getZ());

        String result = world+","+x+","+y+","+z;
        return result;

    }

    public Location stringToCoord(Player player, String className) {

        String uuid = player.getUniqueId().toString();
        try {
            YamlConfiguration yaml = (new PlayerClass(player)).getClassFile();
            String coord = yaml.getString(className+".coord");

            if(coord.equals("0") || coord == null) { // 저장된 로케이션이 없을때
                Location zero = new Location(Bukkit.getWorld("world"), 7.5, 145, 4.5);
                return zero;
            }

            String split[] = coord.split(",");
            String world = split[0];
            double x = Double.parseDouble(split[1]);
            double y = Double.parseDouble(split[2]);
            double z = Double.parseDouble(split[3]);
            Location location = new Location(Bukkit.getWorld(world), x, y, z);

            return location;
        }
        catch(Exception e) {

        }

        Location zero = new Location(Bukkit.getWorld("world"), 7.5, 145, 4.5);
        return zero;
    }
}
