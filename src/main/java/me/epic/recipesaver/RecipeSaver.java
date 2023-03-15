package me.epic.recipesaver;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

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
     * @return Shaped recipe from ConfigurationSecton
     */
    public static ShapedRecipe loadRecipe(ConfigurationSection section, NamespacedKey namespacedKey) {
        return loadRecipe(section, namespacedKey, null);
    }

    /**
     * Loads a recipe from a specified {@link ConfigurationSection} path in a FileConfiguration
     *
     * @param fileConfiguration to get the config section
     * @param path to config section
     * @param key NamespacedKey to register the recipe on
     * @param result Result for the recipe
     * @return ShapedRecipe from FileConfiguration
     */
    public static ShapedRecipe loadRecipe(FileConfiguration fileConfiguration, String path, NamespacedKey key, Material result) {
        return loadRecipe(fileConfiguration.getConfigurationSection(path), key, result);
    }

    /**
     * Loads a recipe from a {@link ConfigurationSection}
     *
     * @param section to load recipe
     * @param namespacedKey NamespacedKey to register the recipe on
     * @param result Result for the recipe
     * @return Shaped recipe from ConfigurationSecton
     */
    public static ShapedRecipe loadRecipe(ConfigurationSection section, NamespacedKey namespacedKey, Material result) {
        ShapedRecipe recipe = new ShapedRecipe(namespacedKey, new ItemStack(result == null ? Material.matchMaterial(section.getString("result")) : result));
        ConfigurationSection items = section.getConfigurationSection("items");
        List<String> shape = section.getStringList("shape");
        recipe.shape(shape.toArray(String[]::new));
        for (String key : items.getKeys(false)) {
            if (items.isList(key)) {
                RecipeChoice choice = new RecipeChoice.MaterialChoice(items.getStringList(key).stream().map(Material::matchMaterial).toArray(Material[]::new));
                recipe.setIngredient(key.charAt(0), choice);
                continue;
            }
            recipe.setIngredient(key.charAt(0), Material.matchMaterial(items.getString(key)));
        }

        return recipe;
    }

}
