package Mob;

import Mob.Gliese581cMobs.Parasite;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MobListManager {

    public enum MobList {


        마우스풋("Gliese581c", 5, 20, 10, 10, 20, true, Parasite.description),
        폭스랫("Gliese581c", 5, 3000, 170,10, 20, true),
        폭스랫_알파("Gliese581c", 5, 30000, 10000,1500, 2000, true),
        패러싯("Gliese581c", 5, 50000, 10,10, 20, true),
        블러드루트("Gliese581c", 5, 200, 10, 50, 100, true),
        히든_오아시스("Gliese581c", 5, 200, 10, 10, 20, true),
        레이지_이글("Gliese581c", 5, 200,10, 10, 20, true),
        마우스본("Gliese581c", 5, 200, 10,10, 20, true),
        머쉬본("Gliese581c", 5, 200, 10,10, 20, true),
        데저트맘모스("Gliese581c",200, 5,10, 10, 20, true),
        발광_나비("Gliese581c", 5, 200,10, 10, 20, true),
        울프팽("Gliese581c", 5, 200, 10,10, 20, true),
        레드_블러드_루트("Gliese581c", 5, 200, 10,10, 20, true),
        클로_울프팽("Gliese581c", 5, 200,10, 10, 20, true),
        패러싯_알파("Gliese581c", 5, 200,10, 10, 20, true),
        edison1304("Gliese581c", 5, 200,10, 10, 20, false),
        감염된_개척자("Gliese581c", 5, 200, 10, 10, 20, true),
        illusion("Gliese581c", 5, 200, 10, 10, 20, false),
        샌드백("Gliese581c, ", 5, 999999, 10, 10, 10, false);


        private String planet;
        private ItemStack itemStack;
        private int level;
        private int health;
        private int mindamage = 0;
        private int maxdamage = 10;
        private int exp = 10;
        private float rawhealth;
        private String name;
        private boolean scannable = true;
        private List<String> description = Arrays.asList("");

        MobList(String planet, int level, int health, int exp, int mindmg, int maxdmg, boolean scannable) {
            this.planet = planet;
            this.level = level;
            this.exp = exp;
            this.rawhealth = (float)health / 500;
            this.health = health;
            this.mindamage = mindmg;
            this.maxdamage = maxdmg;
            this.itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            this.name = "§6[Lv."+level+"] §c "+this.name().replaceAll("_", " ");
            this.scannable = scannable;
        }

        MobList(String planet, int level, int health, int exp, int mindmg, int maxdmg, boolean scannable, List<String> description) {
            this.planet = planet;
            this.level = level;
            this.exp = exp;
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

        public int getEXP() { return exp; }

        public boolean isScannable() { return scannable; }

        public static boolean isinMobList(LivingEntity entity) {

            for(MobList mobList : MobList.values()) {
                if(EntityManager.getinstance(entity).getMobList().equals(mobList))
                    return true;
            }
            return false;

        }


    }


}
