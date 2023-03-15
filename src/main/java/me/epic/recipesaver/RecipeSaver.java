package me.epic.recipesaver;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.*;

import java.util.List;

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
    public static Recipe loadRecipe(ConfigurationSection section, NamespacedKey namespacedKey) {
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
    public static Recipe loadRecipe(FileConfiguration fileConfiguration, String path, NamespacedKey key, ItemStack result) {
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
    public static Recipe loadRecipe(ConfigurationSection section, NamespacedKey namespacedKey, ItemStack result) {
        switch (section.getString("type")) {
            case "shaped" -> {
                ShapedRecipe recipe = new ShapedRecipe(namespacedKey, result == null ? new ItemStack(Material.matchMaterial(section.getString("result"))) : result);
                ConfigurationSection items = section.getConfigurationSection("items");
                List<String> shape = section.getStringList("shape");
                recipe.shape(shape.toArray(String[]::new));
                addItems(items, recipe);

                return recipe;
            }
            case "shapeless" -> {
                ShapelessRecipe recipe = new ShapelessRecipe(namespacedKey, result == null ? new ItemStack(Material.matchMaterial(section.getString("result"))) : result);
                ConfigurationSection items = section.getConfigurationSection("items");
                addItems(items, recipe);
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
    private static void addItems(ConfigurationSection items, Recipe recipe) {
        for (String key : items.getKeys(false)) {
            if (items.isList(key)) {
                RecipeChoice choice = new RecipeChoice.MaterialChoice(items.getStringList(key).stream().map(Material::matchMaterial).toArray(Material[]::new));
                if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                    shapelessRecipe.addIngredient(choice);
                } else {
                    ((ShapedRecipe) recipe).setIngredient(key.charAt(0), choice);
                }
                continue;
            }
            if (recipe instanceof ShapelessRecipe shapelessRecipe) {
                shapelessRecipe.addIngredient(Material.matchMaterial(items.getString(key)));
            } else {
                ((ShapedRecipe) recipe).setIngredient(key.charAt(0), Material.matchMaterial(items.getString(key)));
            }
        }
    }

}
