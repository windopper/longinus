package Items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MaterialItemBuilder {

    private MaterialItemBuilder() {}

    private GRADE grade = GRADE.일반;
    private Material material = Material.STONE;
    private String name = "default";
    private List<String> lore = new ArrayList<>();

    public static MaterialItemBuilder getBuilder() {
        return new MaterialItemBuilder();
    }

    public enum GRADE {
        일반("§3일반", "§3", 0.3),
        희귀("§a희귀", "§a", 0.1),
        미확인("§5미확인", "§5", 0.01);

        private String lore;
        private String color;
        private double percent;

        GRADE(String lore, String color, double percent) {
            this.lore = lore;
            this.color = color;
            this.percent = percent;
        }

        String getLore() {
            return lore;
        }
        String getColor() {
            return color;
        }
        public double getPercent() { return percent; }
    }

    public MaterialItemBuilder setGrade(GRADE grade) {
        this.grade = grade;
        return this;
    }

    public MaterialItemBuilder setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public MaterialItemBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MaterialItemBuilder setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStack build() {
        ItemStack itemStack = new ItemStack(material, 1);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(grade.getColor()+name);
        lore.add(0, "");
        lore.add(0, grade.getLore());
        itemMeta.setLore(lore);

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }



}
