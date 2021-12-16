package Mob;

import Gliese581cMobs.Parasite;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MobListManager {

    public enum MobList {

        마우스풋("Gliese581c", 5, 20, 10, 20, true, Parasite.description),
        폭스랫("Gliese581c", 5, 200, 10, 20, true),
        패러싯("Gliese581c", 5, 50000, 2000, 3000, true),
        블러드루트("Gliese581c", 5, 200, 50, 100, true),
        히든_오아시스("Gliese581c", 5, 200, 10, 20, true),
        레이지_이글("Gliese581c", 5, 200, 10, 20, true),
        마우스본("Gliese581c", 5, 200, 10, 20, true),
        머쉬본("Gliese581c", 5, 200, 10, 20, true),
        데저트맘모스("Gliese581c",200, 5, 10, 20, true),
        발광_나비("Gliese581c", 5, 200, 10, 20, true),
        울프팽("Gliese581c", 5, 200, 10, 20, true),
        레드_블러드_루트("Gliese581c", 5, 200, 10, 20, true),
        클로_울프팽("Gliese581c", 5, 200, 10, 20, true),
        패러싯_알파("Gliese581c", 5, 200, 10, 20, true),
        edison1304("Gliese581c", 5, 200, 10, 20, false);





        private String planet;
        private ItemStack itemStack;
        private int level;
        private int health;
        private int mindamage = 0;
        private int maxdamage = 10;
        private float rawhealth;
        private String name;
        private boolean scannable = true;
        private List<String> description = Arrays.asList("");

        MobList(String planet, int level, int health, int mindmg, int maxdmg, boolean scannable) {
            this.planet = planet;
            this.level = level;
            this.rawhealth = (float)health / 500;
            this.health = health;
            this.mindamage = mindmg;
            this.maxdamage = maxdmg;
            this.itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            this.name = "§6[Lv."+level+"] §c "+this.name().replaceAll("_", " ");
            this.scannable = scannable;
        }

        MobList(String planet, int level, int health, int mindmg, int maxdmg, boolean scannable, List<String> description) {
            this.planet = planet;
            this.level = level;
            this.rawhealth = (float)health / 500;
            this.health = health;
            this.mindamage = mindmg;
            this.maxdamage = maxdmg;
            this.itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            this.name = "§6[Lv."+level+"] §c "+this.name().replaceAll("_", " ");
            this.description = description;
            this.scannable = scannable;
        }

        MobList(String planet, int level, int health, ItemStack itemStack) {
            this.planet = planet;
            this.level = level;
            this.itemStack = itemStack;
            this.name = "§6[Lv."+level+"] §c "+this.name().replaceAll("_", " ");
        }
        public List<String> getDescription() {
            return description;
        }

        public int getLevel() {
            return level;
        }

        public float getRawHealth() {
            return rawhealth;
        }

        public int getHealth() {
            return health;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public String getPlanet() {
            return planet;
        }

        public String getName() {
            return name;
        }

        public String getRawName() {
            return this.name().replaceAll("_", " ");
        }

        public int getMindamage() { return mindamage; }

        public int getMaxdamage() { return maxdamage; }

        public boolean isScannable() { return scannable; }


    }


}
