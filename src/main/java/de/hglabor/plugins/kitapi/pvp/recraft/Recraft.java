package de.hglabor.plugins.kitapi.pvp.recraft;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Recraft {
    private final List<RecraftMaterial> recraftMaterials;

    public Recraft() {
        this.recraftMaterials = List.of(
                new RecraftMaterial(1, Material.RED_MUSHROOM, Material.BROWN_MUSHROOM),
                new RecraftMaterial(1, Material.COCOA_BEANS),
                new RecraftMaterial(1, Material.CACTUS));
    }

    public void calcRecraft(ItemStack[] items) {
        recraftMaterials.forEach(RecraftMaterial::reset);
        for (ItemStack item : items) {
            if (item == null)
                continue;
            for (RecraftMaterial recraftMaterial : recraftMaterials) {
                Material type = item.getType();
                if (recraftMaterial.containsKey(type)) {
                    recraftMaterial.put(type, recraftMaterial.getOrDefault(type, 0) + item.getAmount());
                }
            }
        }
    }

    public void decrease(Player player, int amount) {
        List<Material> lowestMaterials = new ArrayList<>();
        for (RecraftMaterial recraftMaterial : recraftMaterials) {
            if (recraftMaterial.getLowestMaterial() != null) {
                lowestMaterials.add(recraftMaterial.getLowestMaterial());
            }
        }
        Material highestMaterial = null;
        float i = 0;
        for (Material lowestMaterial : lowestMaterials) {
            RecraftMaterial recraftMaterial = byMaterial(lowestMaterial);
            if (recraftMaterial.get(lowestMaterial) * recraftMaterial.getMaterialValue() > i) {
                i = recraftMaterial.get(lowestMaterial) * recraftMaterial.getMaterialValue();
                highestMaterial = lowestMaterial;
            }
        }
        RecraftMaterial recraftMaterial = byMaterial(highestMaterial);
        recraftMaterial.decrease(highestMaterial, amount);
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) {
                continue;
            }
            if (item.getType().equals(highestMaterial)) {
                item.setAmount(item.getAmount() - amount);
                break;
            }
        }
    }

    public RecraftMaterial byMaterial(Material material) {
        return recraftMaterials.stream().filter(recraftMaterial -> recraftMaterial.containsKey(material)).findFirst().orElse(null);
    }

    public float getRecraftPoints() {
        float points = 0;
        for (RecraftMaterial recraftMaterial : recraftMaterials) {
            points += recraftMaterial.getPoints();
        }
        return points;
    }
}
