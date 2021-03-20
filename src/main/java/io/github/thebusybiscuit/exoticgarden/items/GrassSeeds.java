package io.github.thebusybiscuit.exoticgarden.items;

import io.github.thebusybiscuit.slimefun4.core.handlers.ItemUseHandler;
import io.github.thebusybiscuit.slimefun4.implementation.items.SimpleSlimefunItem;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.inventory.ItemUtils;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

import javax.annotation.ParametersAreNonnullByDefault;

public class GrassSeeds extends SimpleSlimefunItem<ItemUseHandler> {

	@ParametersAreNonnullByDefault
	public GrassSeeds(Category category, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
		super(category, item, recipeType, recipe);
	}

	@Override
	public ItemUseHandler getItemHandler() {
		return e -> {
			if (e.getClickedBlock().isPresent()) {
				Block b = e.getClickedBlock().get();

				if (b.getType() == Material.DIRT) {
					if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
						ItemUtils.consumeItem(e.getItem(), false);
					}

					b.setType(Material.GRASS_BLOCK);

					if (b.getRelative(BlockFace.UP).getType() == Material.AIR) {
						b.getRelative(BlockFace.UP).setType(Material.GRASS);
					}

					b.getWorld().playEffect(b.getLocation(), Effect.STEP_SOUND, Material.GRASS);
				}
			}
		};
	}

}