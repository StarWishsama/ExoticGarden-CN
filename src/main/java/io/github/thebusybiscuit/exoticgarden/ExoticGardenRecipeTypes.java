package io.github.thebusybiscuit.exoticgarden;

import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;

public final class ExoticGardenRecipeTypes {

    private ExoticGardenRecipeTypes() {}

    public static final RecipeType KITCHEN = new RecipeType(new NamespacedKey(ExoticGarden.instance, "kitchen"), new SlimefunItemStack("KITCHEN", Material.CAULDRON, "&e厨房"), "", "&r这个物品必须要在厨房里制作");
    public static final RecipeType BREAKING_GRASS = new RecipeType(new NamespacedKey(ExoticGarden.instance, "breaking_grass"), new CustomItem(Material.GRASS, "&7破坏杂草"));
    public static final RecipeType HARVEST_TREE = new RecipeType(new NamespacedKey(ExoticGarden.instance, "harvest_tree"), new CustomItem(Material.OAK_LEAVES, "&a从树木中获得", "", "&r通过种植特定树木获得"));
    public static final RecipeType HARVEST_BUSH = new RecipeType(new NamespacedKey(ExoticGarden.instance, "harvest_bush"), new CustomItem(Material.OAK_LEAVES, "&a从灌木丛中获得", "", "&r通过种植特定灌木丛获得"));

}