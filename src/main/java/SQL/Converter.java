package SQL;

import org.bukkit.configuration.file.YamlConfiguration;
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

}
