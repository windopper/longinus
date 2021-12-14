package Mob;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MobManager {

    public enum MobList {


        마우스풋("Gliese581c", 5, 20, Arrays.asList(
                "대부분 지역에서 출연. 글리제 581c에서 최초로 발견된 생명체",
                "성격이 포악하여 자신의 영역에 들어오면 상대가 누구든 공격함")),
        폭스랫("Gliese581c", 5, 200),
        패러싯("Gliese581c", 5, 200),
        블러드루트("Gliese581c", 5, 200),
        히든_오아시스("Gliese581c", 5, 200),
        레이지_이글("Gliese581c", 5, 200),
        마우스본("Gliese581c", 5, 200),
        머쉬본("Gliese581c", 5, 200),
        데저트맘모스("Gliese581c",200, 5),
        발광_나비("Gliese581c", 5, 200),
        울프팽("Gliese581c", 5, 200);





        private String planet;
        private ItemStack itemStack;
        private int level;
        private int health;
        private float rawhealth;
        private String name;
        private List<String> description = Arrays.asList("");

        MobList(String planet, int level, int health) {
            this.planet = planet;
            this.level = level;
            this.rawhealth = (float)health / 500;
            this.health = health;
            this.itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            this.name = "§6[Lv."+level+"] §c "+this.name().replaceAll("_", " ");
        }

        MobList(String planet, int level, int health, List<String> description) {
            this.planet = planet;
            this.level = level;
            this.rawhealth = (float)health / 500;
            this.health = health;
            this.itemStack = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            this.name = "§6[Lv."+level+"] §c "+this.name().replaceAll("_", " ");
            this.description = description;
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


    }


}
