package io.github.thebusybiscuit.exoticgarden;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class ExoticGardenRecipeTypes {

    public static final RecipeType KITCHEN = new RecipeType(new NamespacedKey(ExoticGarden.instance, "kitchen"), new SlimefunItemStack("KITCHEN", Material.CAULDRON, "&e厨房"), "", "&r这个物品必须要", "&r在厨房里制作");
    public static final RecipeType BREAKING_GRASS = new RecipeType(new NamespacedKey(ExoticGarden.instance, "breaking_grass"), new CustomItemStack(Material.GRASS, "&7破坏杂草"));
    public static final RecipeType HARVEST_TREE = new RecipeType(new NamespacedKey(ExoticGarden.instance, "harvest_tree"), new CustomItemStack(Material.OAK_LEAVES, "&a从树木中获得", "", "&r通过种植特定", "&r树木获得"));
    public static final RecipeType HARVEST_BUSH = new RecipeType(new NamespacedKey(ExoticGarden.instance, "harvest_bush"), new CustomItemStack(Material.OAK_LEAVES, "&a从灌木丛中获得", "", "&r通过种植特定", "&r灌木丛获得"));
    private ExoticGardenRecipeTypes() {
    }

}
