package me.epic.recipesaver;

import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author The-Epic
 */
public final class RecipeSaver  {

    /**
     * Loads a recipe from a {@link ConfigurationSection}
     *
     * @param section to load recipe
     * @param namespacedKey NamespacedKey to register the recipe on
     * @return Recipe from ConfigurationSecton
     */
	@Nullable
    public static Recipe loadRecipe(@NotNull ConfigurationSection section, @NotNull NamespacedKey namespacedKey) {
        return loadRecipe(section, namespacedKey, null);
    }

    /**
     * Loads a recipe from a specified {@link ConfigurationSection} path in a FileConfiguration
     *
     * @param fileConfiguration to get the config section
     * @param path to config section
     * @param key NamespacedKey to register the recipe on
     * @param result Result for the recipe
     * @return Recipe from FileConfiguration
     */
	@Nullable
    public static Recipe loadRecipe(@NotNull FileConfiguration fileConfiguration, @NotNull String path, @NotNull NamespacedKey key, @Nullable ItemStack result) {
        return loadRecipe(fileConfiguration.getConfigurationSection(path), key, result);
    }

    /**
     * Loads a recipe from a {@link ConfigurationSection}
     *
     * @param section to load recipe
     * @param namespacedKey NamespacedKey to register the recipe on
     * @param result Result for the recipe
     * @return Recipe from ConfigurationSecton
     */
	@Nullable
    public static Recipe loadRecipe(@NotNull ConfigurationSection section, @NotNull NamespacedKey namespacedKey, @Nullable ItemStack result) {
    	result = (result == null) ? new ItemStack(Material.matchMaterial(section.getString("result"))) : result;
        switch (section.getString("type")) {
            case "shaped" -> {
                ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result);
                ConfigurationSection items = section.getConfigurationSection("items");
                List<String> shape = section.getStringList("shape");
                recipe.shape(shape.toArray(String[]::new));
                loadIngredients(items, recipe);

                return recipe;
            }
            case "shapeless" -> {
                ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, result);
                ConfigurationSection items = section.getConfigurationSection("items");
                loadIngredients(items, recipe);
                return recipe;
            }
        }
        return null;
    }

    /**
     * Adds items for both recipes
     *
     * @param items Configuration section to get the items from
     * @param recipe to add the ingredients to
     */
	private static void loadIngredients(@NotNull ConfigurationSection items, @NotNull Recipe recipe) {
		for (String key : items.getKeys(false)) {
			char recipeKey = key.charAt(0);
			
			if (items.isList(key)) {
				RecipeChoice choice = new RecipeChoice.MaterialChoice(items.getStringList(key).stream().map(Material::matchMaterial).filter(Objects::nonNull).toArray(Material[]::new));
				addIngredient(recipe, recipeKey, choice);
				continue;
			}
			
			Material material = Material.matchMaterial(items.getString(key));
			if (material == null) continue;
			
			addIngredient(recipe, recipeKey, new RecipeChoice.MaterialChoice(material));
		}
	}
    
	/**
	 * Adds an ingredient to the given recipe
	 * 
	 * @param recipe the recipe to add the ingredient to
	 * @param key the key map the ingredient to, only used for shaped recipes
	 * @param ingredient the ingredient to add
	 */
	private static void addIngredient(@NotNull Recipe recipe, @NotNull char key, @NotNull RecipeChoice ingredient) {
		if (recipe instanceof ShapelessRecipe shapelessRecipe) {
			shapelessRecipe.addIngredient(ingredient);
			return;
		}

		if (recipe instanceof ShapedRecipe shapedRecipe) {
			shapedRecipe.setIngredient(key, ingredient);
		}
	}
}
