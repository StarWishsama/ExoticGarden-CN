package io.github.thebusybiscuit.exoticgarden;

import io.github.thebusybiscuit.exoticgarden.items.*;
import io.github.thebusybiscuit.exoticgarden.listeners.PlantsListener;
import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.core.researching.Research;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.implementation.items.food.Juice;
import io.github.thebusybiscuit.slimefun4.libraries.paperlib.PaperLib;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomPotion;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Category;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.SlimefunItemStack;
import me.mrCookieSlime.Slimefun.cscorelib2.config.Config;
import me.mrCookieSlime.Slimefun.cscorelib2.item.CustomItem;
import me.mrCookieSlime.Slimefun.cscorelib2.reflection.ReflectionUtils;
import me.mrCookieSlime.Slimefun.cscorelib2.skull.SkullItem;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.GitHubBuildsUpdater;
import me.mrCookieSlime.Slimefun.cscorelib2.updater.Updater;
import org.bstats.bukkit.Metrics;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.Level;

public class ExoticGarden extends JavaPlugin implements SlimefunAddon {

	public static ExoticGarden instance;

	private final File schematicsFolder = new File(getDataFolder(), "schematics");

	private final List<Berry> berries = new ArrayList<>();
	private final List<Tree> trees = new ArrayList<>();
	private final Map<String, ItemStack> items = new HashMap<>();

	protected Config cfg;

	private Category mainCategory;
    private Category miscCategory;
	private Category foodCategory;
	private Category drinksCategory;
	private Category magicalCategory;
	private Kitchen kitchen;

	@Override
	public void onEnable() {

		PaperLib.suggestPaper(this);

		if (!schematicsFolder.exists()) {
			schematicsFolder.mkdirs();
		}

    	instance = this;
    	cfg = new Config(this);

		// Setting up bStats
		new Metrics(this, 4575);
		
		// Auto Updater
		if (cfg.getBoolean("options.auto-update") && getDescription().getVersion().startsWith("DEV - ")) {
		    Updater updater = new GitHubBuildsUpdater(this, getFile(), "TheBusyBiscuit/ExoticGarden/master");
		    updater.start();
		}

		if (ReflectionUtils.getVersion().contains("1_13")){
			getLogger().info("检测到使用 1.13 版本, 向下兼容已启用");
		}

		mainCategory = new Category(new NamespacedKey(this, "plants_and_fruits"), new CustomItem(SkullItem.fromHash("a5a5c4a0a16dabc9b1ec72fc83e23ac15d0197de61b138babca7c8a29c820"), "&a异域花园 - 植物和水果"));
		miscCategory = new Category(new NamespacedKey(this, "misc"), new CustomItem(SkullItem.fromHash("606be2df2122344bda479feece365ee0e9d5da276afa0e8ce8d848f373dd131"), "&a异域花园 - 配料和工具"));
        foodCategory = new Category(new NamespacedKey(this, "food"), new CustomItem(SkullItem.fromHash("a14216d10714082bbe3f412423e6b19232352f4d64f9aca3913cb46318d3ed"), "&a异域花园 - 食物"));
		drinksCategory = new Category(new NamespacedKey(this, "drinks"), new CustomItem(SkullItem.fromHash("2a8f1f70e85825607d28edce1a2ad4506e732b4a5345a5ea6e807c4b313e88"), "&a异域花园 - 饮料"));
		magicalCategory = new Category(new NamespacedKey(this, "magical_crops"), new CustomItem(Material.BLAZE_POWDER, "&5异域花园 - 魔法植物"));

        kitchen = new Kitchen(this, miscCategory);
        kitchen.register(this);

		Research kitchenResearch = new Research(new NamespacedKey(this, "kitchen"), 600, "Kitchen", 30);
		kitchenResearch.addItems(kitchen);
		kitchenResearch.register();
        
		SlimefunItemStack iceCube = new SlimefunItemStack("ICE_CUBE", "9340bef2c2c33d113bac4e6a1a84d5ffcecbbfab6b32fa7a7f76195442bd1a2", "&b冰块");
		new SlimefunItem(miscCategory, iceCube, RecipeType.GRIND_STONE,
		new ItemStack[] {new ItemStack(Material.ICE), null, null, null, null, null, null, null, null}, new CustomItem(iceCube, 4))
		.register(this);

		registerBerry("Grape", "葡萄", ChatColor.RED, Color.RED, PlantType.BUSH, "6ee97649bd999955413fcbf0b269c91be4342b10d0755bad7a17e95fcefdab0");
		registerBerry("Blueberry", "蓝莓", ChatColor.BLUE, Color.BLUE, PlantType.BUSH, "a5a5c4a0a16dabc9b1ec72fc83e23ac15d0197de61b138babca7c8a29c820");
		registerBerry("Elderberry", "接骨木果", ChatColor.RED, Color.FUCHSIA, PlantType.BUSH, "1e4883a1e22c324e753151e2ac424c74f1cc646eec8ea0db3420f1dd1d8b");
		registerBerry("Raspberry", "树莓", ChatColor.LIGHT_PURPLE, Color.FUCHSIA, PlantType.BUSH,"8262c445bc2dd1c5bbc8b93f2482f9fdbef48a7245e1bdb361d4a568190d9b5");
		registerBerry("Blackberry", "黑莓", ChatColor.DARK_GRAY, Color.GRAY, PlantType.BUSH, "2769f8b78c42e272a669d6e6d19ba8651b710ab76f6b46d909d6a3d482754");
		registerBerry("Cranberry", "蔓越莓", ChatColor.RED, Color.FUCHSIA, PlantType.BUSH, "d5fe6c718fba719ff622237ed9ea6827d093effab814be2192e9643e3e3d7");
		registerBerry("Cowberry", "越橘", ChatColor.RED, Color.FUCHSIA, PlantType.BUSH, "a04e54bf255ab0b1c498ca3a0ceae5c7c45f18623a5a02f78a7912701a3249");
		registerBerry("Strawberry", "草莓", ChatColor.DARK_RED, Color.FUCHSIA, PlantType.FRUIT, "cbc826aaafb8dbf67881e68944414f13985064a3f8f044d8edfb4443e76ba");

		registerPlant("Tomato", "番茄", ChatColor.DARK_RED, PlantType.FRUIT, "99172226d276070dc21b75ba25cc2aa5649da5cac745ba977695b59aebd");
		registerPlant("Lettuce", "生菜", ChatColor.DARK_GREEN, PlantType.FRUIT, "477dd842c975d8fb03b1add66db8377a18ba987052161f22591e6a4ede7f5");
		registerPlant("Tea Leaf", "茶叶", ChatColor.GREEN, PlantType.DOUBLE_PLANT, "1514c8b461247ab17fe3606e6e2f4d363dccae9ed5bedd012b498d7ae8eb3");
		registerPlant("Cabbage", "卷心菜",  ChatColor.DARK_GREEN, PlantType.FRUIT, "fcd6d67320c9131be85a164cd7c5fcf288f28c2816547db30a3187416bdc45b");
		registerPlant("Sweet Potato","地瓜",  ChatColor.GOLD, PlantType.FRUIT, "3ff48578b6684e179944ab1bc75fec75f8fd592dfb456f6def76577101a66");
		registerPlant("Mustard Seed", "芥菜籽", ChatColor.YELLOW, PlantType.FRUIT, "ed53a42495fa27fb925699bc3e5f2953cc2dc31d027d14fcf7b8c24b467121f");
		registerPlant("Curry Leaf", "咖喱叶", ChatColor.DARK_GREEN, PlantType.DOUBLE_PLANT, "32af7fa8bdf3252f69863b204559d23bfc2b93d41437103437ab1935f323a31f");
		registerPlant("Onion", "洋葱", ChatColor.RED, PlantType.FRUIT, "6ce036e327cb9d4d8fef36897a89624b5d9b18f705384ce0d7ed1e1fc7f56");
		registerPlant("Garlic", "大蒜", ChatColor.RESET, PlantType.FRUIT, "3052d9c11848ebcc9f8340332577bf1d22b643c34c6aa91fe4c16d5a73f6d8");
		registerPlant("Cilantro", "香菜", ChatColor.GREEN, PlantType.DOUBLE_PLANT, "16149196f3a8d6d6f24e51b27e4cb71c6bab663449daffb7aa211bbe577242");
		registerPlant("Black Pepper", "黑胡椒",  ChatColor.DARK_GRAY, PlantType.DOUBLE_PLANT, "2342b9bf9f1f6295842b0efb591697b14451f803a165ae58d0dcebd98eacc");

		registerPlant("Corn", "玉米", ChatColor.GOLD,  PlantType.DOUBLE_PLANT, "9bd3802e5fac03afab742b0f3cca41bcd4723bee911d23be29cffd5b965f1");
		registerPlant("Pineapple", "菠萝",  ChatColor.GOLD,  PlantType.DOUBLE_PLANT, "d7eddd82e575dfd5b7579d89dcd2350c991f0483a7647cffd3d2c587f21");

		registerTree("Oak Apple", "橡树苹果", "cbb311f3ba1c07c3d1147cd210d81fe11fd8ae9e3db212a0fa748946c3633", "&c", Color.FUCHSIA, "橡树苹果汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Coconut", "椰子", "6d27ded57b94cf715b048ef517ab3f85bef5a7be69f14b1573e14e7e42e2e8", "&6", Color.MAROON, "椰奶", false, Material.SAND);
		registerTree("Cherry", "樱桃", "c520766b87d2463c34173ffcd578b0e67d163d37a2d7c2e77915cd91144d40d1", "&c", Color.FUCHSIA, "樱桃汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pomegranate", "石榴", "cbb311f3ba1c07c3d1147cd210d81fe11fd8ae9e3db212a0fa748946c3633", "&4", Color.RED, "石榴汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lemon", "柠檬", "957fd56ca15978779324df519354b6639a8d9bc1192c7c3de925a329baef6c", "&e", Color.YELLOW, "柠檬汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Plum", "梅子", "69d664319ff381b4ee69a697715b7642b32d54d726c87f6440bf017a4bcd7", "&5", Color.RED, "酸梅汤", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Lime", "酸橙", "5a5153479d9f146a5ee3c9e218f5e7e84c4fa375e4f86d31772ba71f6468", "&a", Color.LIME, "酸橙汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Orange", "橙子", "65b1db547d1b7956d4511accb1533e21756d7cbc38eb64355a2626412212", "&6", Color.ORANGE, "橙汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Peach", "桃子", "d3ba41fe82757871e8cbec9ded9acbfd19930d93341cf8139d1dfbfaa3ec2a5", "&5", Color.RED, "桃汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Pear", "梨子", "2de28df844961a8eca8efb79ebb4ae10b834c64a66815e8b645aeff75889664b", "&a", Color.LIME, "梨汁", true, Material.DIRT, Material.GRASS_BLOCK);
		registerTree("Dragon Fruit", "火龙果", "847d73a91b52393f2c27e453fb89ab3d784054d414e390d58abd22512edd2b", "&d", Color.FUCHSIA, "火龙果汁", true, Material.DIRT, Material.GRASS_BLOCK);

		FoodRegistry.register(this, miscCategory, drinksCategory, foodCategory);

		registerMagicalPlant("Dirt", "泥土", new ItemStack(Material.DIRT, 2), "1ab43b8c3d34f125e5a3f8b92cd43dfd14c62402c33298461d4d4d7ce2d3aea",
				new ItemStack[] {null, new ItemStack(Material.DIRT), null, new ItemStack(Material.DIRT), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.DIRT), null, new ItemStack(Material.DIRT), null});


		registerMagicalPlant("Coal", "煤炭", new ItemStack(Material.COAL, 2), "7788f5ddaf52c5842287b9427a74dac8f0919eb2fdb1b51365ab25eb392c47",
		new ItemStack[] {null, new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), new ItemStack(Material.WHEAT_SEEDS), new ItemStack(Material.COAL_ORE), null, new ItemStack(Material.COAL_ORE), null});

		registerMagicalPlant("Iron", "铁锭", new ItemStack(Material.IRON_INGOT), "db97bdf92b61926e39f5cddf12f8f7132929dee541771e0b592c8b82c9ad52d",
		new ItemStack[] {null, new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), getItem("COAL_PLANT"), new ItemStack(Material.IRON_BLOCK), null, new ItemStack(Material.IRON_BLOCK), null});

		registerMagicalPlant("Gold", "金", SlimefunItems.GOLD_4K, "e4df892293a9236f73f48f9efe979fe07dbd91f7b5d239e4acfd394f6eca",
		new ItemStack[] {null, SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, getItem("IRON_PLANT"), SlimefunItems.GOLD_16K, null, SlimefunItems.GOLD_16K, null});

		registerMagicalPlant("Copper", "铜", new CustomItem(SlimefunItems.COPPER_DUST, 8), "d4fc72f3d5ee66279a45ac9c63ac98969306227c3f4862e9c7c2a4583c097b8a",
		new ItemStack[] {null, SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, getItem("GOLD_PLANT"), SlimefunItems.COPPER_DUST, null, SlimefunItems.COPPER_DUST, null});

		registerMagicalPlant("Aluminum", "铝", new CustomItem(SlimefunItems.ALUMINUM_DUST, 8), "f4455341eaff3cf8fe6e46bdfed8f501b461fb6f6d2fe536be7d2bd90d2088aa",
		new ItemStack[] {null, SlimefunItems.ALUMINUM_DUST, null, SlimefunItems.ALUMINUM_DUST, getItem("IRON_PLANT"), SlimefunItems.ALUMINUM_DUST, null, SlimefunItems.ALUMINUM_DUST, null});

		registerMagicalPlant("Tin", "锡", new CustomItem(SlimefunItems.TIN_DUST, 8), "6efb43ba2fe6959180ee7307f3f054715a34c0a07079ab73712547ffd753dedd",
		new ItemStack[] {null, SlimefunItems.TIN_DUST, null, SlimefunItems.TIN_DUST, getItem("IRON_PLANT"), SlimefunItems.TIN_DUST, null, SlimefunItems.TIN_DUST, null});
		
		registerMagicalPlant("Silver", "银", new CustomItem(SlimefunItems.SILVER_DUST, 8), "1dd968b1851aa7160d1cd9db7516a8e1bf7b7405e5245c5338aa895fe585f26c",
		new ItemStack[] {null, SlimefunItems.SILVER_DUST, null, SlimefunItems.SILVER_DUST, getItem("IRON_PLANT"), SlimefunItems.SILVER_DUST, null, SlimefunItems.SILVER_DUST, null});
		
		registerMagicalPlant("Lead", "铅", new CustomItem(SlimefunItems.LEAD_DUST, 8), "93c3c418039c4b28b0da75a6d9b22712c7015432d4f4226d6cc0a77d54b64178",
		new ItemStack[] {null, SlimefunItems.LEAD_DUST, null, SlimefunItems.LEAD_DUST, getItem("IRON_PLANT"), SlimefunItems.LEAD_DUST, null, SlimefunItems.LEAD_DUST, null});
		
		registerMagicalPlant("Redstone", "红石", new ItemStack(Material.REDSTONE, 8), "e8deee5866ab199eda1bdd7707bdb9edd693444f1e3bd336bd2c767151cf2",
		new ItemStack[] {null, new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), getItem("GOLD_PLANT"), new ItemStack(Material.REDSTONE_BLOCK), null, new ItemStack(Material.REDSTONE_BLOCK), null});

		registerMagicalPlant("Lapis", "青金石", new ItemStack(Material.LAPIS_LAZULI, 16), "2aa0d0fea1afaee334cab4d29d869652f5563c635253c0cbed797ed3cf57de0",
		new ItemStack[] {null, new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), getItem("REDSTONE_PLANT"), new ItemStack(Material.LAPIS_ORE), null, new ItemStack(Material.LAPIS_ORE), null});

		registerMagicalPlant("Ender", "末影珍珠", new ItemStack(Material.ENDER_PEARL, 4), "4e35aade81292e6ff4cd33dc0ea6a1326d04597c0e529def4182b1d1548cfe1",
		new ItemStack[] {null, new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), getItem("LAPIS_PLANT"), new ItemStack(Material.ENDER_PEARL), null, new ItemStack(Material.ENDER_PEARL), null});

		registerMagicalPlant("Quartz", "石英", new ItemStack(Material.QUARTZ, 8), "26de58d583c103c1cd34824380c8a477e898fde2eb9a74e71f1a985053b96",
		new ItemStack[] {null, new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), getItem("ENDER_PLANT"), new ItemStack(Material.NETHER_QUARTZ_ORE), null, new ItemStack(Material.NETHER_QUARTZ_ORE), null});

		registerMagicalPlant("Diamond", "钻石", new ItemStack(Material.DIAMOND), "f88cd6dd50359c7d5898c7c7e3e260bfcd3dcb1493a89b9e88e9cbecbfe45949",
		new ItemStack[] {null, new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), getItem("QUARTZ_PLANT"), new ItemStack(Material.DIAMOND), null, new ItemStack(Material.DIAMOND), null});

		registerMagicalPlant("Emerald", "绿宝石", new ItemStack(Material.EMERALD), "4fc495d1e6eb54a386068c6cb121c5875e031b7f61d7236d5f24b77db7da7f",
		new ItemStack[] {null, new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), getItem("DIAMOND_PLANT"), new ItemStack(Material.EMERALD), null, new ItemStack(Material.EMERALD), null});

		registerMagicalPlant("Glowstone", "萤石", new ItemStack(Material.GLOWSTONE_DUST, 8), "65d7bed8df714cea063e457ba5e87931141de293dd1d9b9146b0f5ab383866",
		new ItemStack[] {null, new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), getItem("REDSTONE_PLANT"), new ItemStack(Material.GLOWSTONE), null, new ItemStack(Material.GLOWSTONE), null});

		registerMagicalPlant("Obsidian", "黑曜石", new ItemStack(Material.OBSIDIAN, 2), "7840b87d52271d2a755dedc82877e0ed3df67dcc42ea479ec146176b02779a5",
		new ItemStack[] {null, new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), getItem("LAPIS_PLANT"), new ItemStack(Material.OBSIDIAN), null, new ItemStack(Material.OBSIDIAN), null});

		registerMagicalPlant("Slime", "粘液球", new ItemStack(Material.SLIME_BALL, 8), "90e65e6e5113a5187dad46dfad3d3bf85e8ef807f82aac228a59c4a95d6f6a",
		new ItemStack[] {null, new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), getItem("ENDER_PLANT"), new ItemStack(Material.SLIME_BALL), null, new ItemStack(Material.SLIME_BALL), null});

		new Crook(miscCategory, new SlimefunItemStack("CROOK", new CustomItem(Material.WOODEN_HOE, "&r拐棍", "", "&7+ 树苗掉率提升 &b25%")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {new ItemStack(Material.STICK), new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null, null, new ItemStack(Material.STICK), null})
		.register(this);

		SlimefunItemStack grassSeeds = new SlimefunItemStack("GRASS_SEEDS", Material.PUMPKIN_SEEDS, "&r草籽", "", "&7&o可以种在泥土上");
		new GrassSeeds(mainCategory, grassSeeds, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new PlantsListener(this);

		items.put("WHEAT_SEEDS", new ItemStack(Material.WHEAT_SEEDS));
		items.put("PUMPKIN_SEEDS", new ItemStack(Material.PUMPKIN_SEEDS));
		items.put("MELON_SEEDS", new ItemStack(Material.MELON_SEEDS));
		
		for (Material sapling : Tag.SAPLINGS.getValues()) {
		    items.put(sapling.name(), new ItemStack(sapling));
		}
		
		items.put("GRASS_SEEDS", grassSeeds);

		Iterator<String> iterator = items.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			cfg.setDefaultValue("grass-drops." + key, true);
			if (!cfg.getBoolean("grass-drops." + key)) iterator.remove();
		}
		
		cfg.save();
	}

	@Override
	public void onDisable() {
		instance = null;
	}

	private void registerTree(String rawName, String name, String texture, String color, Color pcolor, String juice, boolean pie, Material... soil) {
		String id = rawName.toUpperCase(Locale.ROOT).replace(' ', '_');
		Tree tree = new Tree(id, texture, soil);
		trees.add(tree);

		SlimefunItemStack sapling = new SlimefunItemStack(id + "_SAPLING", Material.OAK_SAPLING, color + name + " 树苗");

		items.put(id + "_SAPLING", sapling);

		new SlimefunItem(mainCategory, sapling, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new ExoticGardenFruit(mainCategory, new SlimefunItemStack(id, texture, color + name), ExoticGardenRecipeTypes.HARVEST_TREE, true,
		new ItemStack[] {null, null, null, null, getItem(id + "_SAPLING"), null, null, null, null})
		.register(this);

		if (pcolor != null) {
			new Juice(drinksCategory, new SlimefunItemStack(juice.toUpperCase().replace(" ", "_"), new CustomPotion(color + juice, pcolor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o恢复 &b&o" + "3.0" + " &7&o点饥饿值")), RecipeType.JUICER,
			new ItemStack[] {getItem(id), null, null, null, null, null, null, null, null})
			.register(this);
		}

		if (pie) {
			new CustomFood(foodCategory, new SlimefunItemStack(id + "_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", color + name + " 派", "", "&7&o恢复 &b&o" + "6.5" + " &7&o点饥饿值"),
			new ItemStack[] {getItem(id), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
			13)
			.register(this);
		}

		if (!new File(schematicsFolder, id + "_TREE.schematic").exists()) {
			saveSchematic(id + "_TREE");
		}
	}

	private void saveSchematic(@Nonnull String id) {
		try (InputStream input = getClass().getResourceAsStream("/schematics/" + id + ".schematic")) {
			try (FileOutputStream output = new FileOutputStream(new File(schematicsFolder, id + ".schematic"))) {
				byte[] buffer = new byte[1024];
                int len;
                
                while ((len = input.read(buffer)) > 0) {
                	output.write(buffer, 0, len);
                }
			}
		} catch (IOException e) {
			getLogger().log(Level.SEVERE, e, () -> "Failed to load file: \"" + id + ".schematic\"");
		}
	}

	public void registerBerry(String rawName, String name, ChatColor color, Color potionColor, PlantType type, String texture) {
		String upperCase = rawName.toUpperCase(Locale.ROOT);
		Berry berry = new Berry(upperCase, type, texture);
		berries.add(berry);

		SlimefunItemStack sfi = new SlimefunItemStack(upperCase + "_BUSH", Material.OAK_SAPLING, color + name + "灌木丛");

		items.put(upperCase + "_BUSH", sfi);

		new SlimefunItem(mainCategory, sfi, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new ExoticGardenFruit(mainCategory, new SlimefunItemStack(upperCase, texture, color + name), ExoticGardenRecipeTypes.HARVEST_BUSH, true,
		new ItemStack[] {null, null, null, null, getItem(upperCase + "_BUSH"), null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack(upperCase + "_JUICE", new CustomPotion(color + name + "果汁", potionColor, new PotionEffect(PotionEffectType.SATURATION, 6, 0), "", "&7&o恢复 &b&o" + "3.0" + " &7&o点饥饿值")), RecipeType.JUICER,
		new ItemStack[] {getItem(upperCase), null, null, null, null, null, null, null, null})
		.register(this);

		new Juice(drinksCategory, new SlimefunItemStack(upperCase + "_SMOOTHIE", new CustomPotion(color + name + "冰沙", potionColor, new PotionEffect(PotionEffectType.SATURATION, 10, 0), "", "&7&o恢复 &b&o" + "5.0" + " &7&o点饥饿值")), RecipeType.ENHANCED_CRAFTING_TABLE,
		new ItemStack[] {getItem(upperCase + "_JUICE"), getItem("ICE_CUBE"), null, null, null, null, null, null, null})
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack(upperCase + "_JELLY_SANDWICH", "8c8a939093ab1cde6677faf7481f311e5f17f63d58825f0e0c174631fb0439", color + name + "果酱三明治", "", "&7&o恢复 &b&o" + "8.0" + " &7&o点饥饿值"),
		new ItemStack[] {null, new ItemStack(Material.BREAD), null, null, getItem(upperCase + "_JUICE"), null, null, new ItemStack(Material.BREAD), null},
		16)
		.register(this);

		new CustomFood(foodCategory, new SlimefunItemStack(upperCase + "_PIE", "3418c6b0a29fc1fe791c89774d828ff63d2a9fa6c83373ef3aa47bf3eb79", color + name + "派", "", "&7&o恢复 &b&o" + "6.5" + " &7&o点饥饿值"),
		new ItemStack[] {getItem(upperCase), new ItemStack(Material.EGG), new ItemStack(Material.SUGAR), new ItemStack(Material.MILK_BUCKET), SlimefunItems.WHEAT_FLOUR, null, null, null, null},
		13)
		.register(this);
	}

	@Nonnull
	private static ItemStack getItem(@Nonnull String id) {
		SlimefunItem item = SlimefunItem.getByID(id);
		return item != null ? item.getItem() : null;
	}

	public void registerPlant(String rawName, String name, ChatColor color, PlantType type, String texture) {
		String upperCase = rawName.toUpperCase(Locale.ROOT);
		String enumStyle = upperCase.replace(' ', '_');

		Berry berry = new Berry(enumStyle, type, texture);
		berries.add(berry);

		SlimefunItemStack bush = new SlimefunItemStack(enumStyle + "_BUSH", Material.OAK_SAPLING, color + name + "植物");
		items.put(upperCase + "_BUSH", bush);

		new SlimefunItem(mainCategory, bush, ExoticGardenRecipeTypes.BREAKING_GRASS,
		new ItemStack[] {null, null, null, null, new ItemStack(Material.GRASS), null, null, null, null})
		.register(this);

		new ExoticGardenFruit(mainCategory, new SlimefunItemStack(enumStyle, texture, color + name), ExoticGardenRecipeTypes.HARVEST_BUSH, true,
		new ItemStack[] {null, null, null, null, getItem(enumStyle + "_BUSH"), null, null, null, null})
		.register(this);
	}

	private void registerMagicalPlant(String rawName, String name, ItemStack item, String texture, ItemStack[] recipe) {
		String upperCase = rawName.toUpperCase(Locale.ROOT);
		String enumStyle = upperCase.replace(' ', '_');

		SlimefunItemStack essence = new SlimefunItemStack(enumStyle + "_ESSENCE", Material.BLAZE_POWDER, "&r魔法精华", "", "&7" + name);

		Berry berry = new Berry(essence, upperCase + "_ESSENCE", PlantType.ORE_PLANT, texture);
		berries.add(berry);

		new SlimefunItem(magicalCategory, new SlimefunItemStack(enumStyle + "_PLANT", Material.OAK_SAPLING, "&r" + name + "植物"), RecipeType.ENHANCED_CRAFTING_TABLE,
		recipe)
		.register(this);

		MagicalEssence magicalEssence = new MagicalEssence(magicalCategory, essence);

		magicalEssence.setRecipeOutput(item.clone());
		magicalEssence.register(this);
	}

	@Nullable
	public static ItemStack harvestPlant(@Nonnull Block block) {
		SlimefunItem item = BlockStorage.check(block);
		if (item == null) {
			return null;
		}

		for (Berry berry : getBerries()) {
			if (item.getId().equalsIgnoreCase(berry.getID())) {
				switch (berry.getType()) {
					case ORE_PLANT:
					case DOUBLE_PLANT:
						Block plant = block;

						if (Tag.LEAVES.isTagged(block.getType())) {
							block = block.getRelative(BlockFace.UP);
						} else {
							plant = block.getRelative(BlockFace.DOWN);
						}

						BlockStorage.deleteLocationInfoUnsafely(block.getLocation(), false);
						block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.OAK_LEAVES);
						block.setType(Material.AIR);

						plant.setType(Material.OAK_SAPLING);
						BlockStorage.deleteLocationInfoUnsafely(plant.getLocation(), false);
						BlockStorage.store(plant, getItem(berry.toBush()));
						return berry.getItem().clone();
					default:
						block.setType(Material.OAK_SAPLING);
						BlockStorage.deleteLocationInfoUnsafely(block.getLocation(), false);
						BlockStorage.store(block, getItem(berry.toBush()));
						return berry.getItem().clone();
				}
			}
		}

		return null;
	}

	public static ExoticGarden getInstance() {
		return instance;
	}

	public File getSchematicsFolder() {
		return schematicsFolder;
	}

	public static Kitchen getKitchen() {
		return instance.kitchen;
	}

	public static List<Tree> getTrees() {
		return instance.trees;
	}

	public static List<Berry> getBerries() {
		return instance.berries;
	}

	public static Map<String, ItemStack> getGrassDrops() {
		return instance.items;
	}

	public Config getCfg() {
		return cfg;
	}

	@Override
	public JavaPlugin getJavaPlugin() {
		return this;
	}

	@Override
	public String getBugTrackerURL() {
		return "https://github.com/TheBusyBiscuit/ExoticGarden/issues";
	}

}