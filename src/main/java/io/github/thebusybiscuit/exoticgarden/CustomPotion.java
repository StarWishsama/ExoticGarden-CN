package io.github.thebusybiscuit.exoticgarden;

import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;

public final class CustomPotion extends ItemStack {

    @ParametersAreNonnullByDefault
    public CustomPotion(String name, Color color, PotionEffect effect, String... lore) {
        super(Material.POTION);

        PotionMeta meta = (PotionMeta) getItemMeta();
        List<String> list = new ArrayList<>();

        for (String line : lore) {
            list.add(ChatColors.color(line));
        }

        meta.setDisplayName(ChatColors.color(name));
        meta.setLore(list);
        meta.setColor(color);
        meta.addCustomEffect(effect, true);

        setItemMeta(meta);
    }

}