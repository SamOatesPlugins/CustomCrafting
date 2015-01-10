package com.samoatesgames.customcrafting;

import com.samoatesgames.samoatesplugincore.configuration.PluginConfiguration;
import com.samoatesgames.samoatesplugincore.plugin.SamOatesPlugin;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

/**
 * The main plugin class
 *
 * @author Sam Oates <sam@samoatesgames.com>
 */
public final class CustomCrafting extends SamOatesPlugin {
        
    /**
     * Class constructor
     */
    public CustomCrafting() {
        super("CustomCrafting", "CustomCrafting", ChatColor.GOLD);
    }

    /**
     * Called when the plugin is enabled
     */
    @Override
    public void onEnable() {
        super.onEnable();     

        loadRecipes();
        
        this.logInfo("Succesfully enabled.");
    }

    /**
     * Called when the plugin is disabled
     */
    @Override
    public void onDisable() {
        
    }
    
    /**
     * Register all configuration settings
     */
    public void setupConfigurationSettings() {

    }
    
    private void loadRecipes() {
        File recipeFolder = new File(this.getDataFolder(), "recipes");
        if (!recipeFolder.exists()) {
            // Recipes folder doesn't exist, create it and copy the example recipe to the folder.
            recipeFolder.mkdirs();
        
            InputStream exampleRecipeSource = this.getClass().getResourceAsStream("example_recipe.yml");
            FileOutputStream outputFile = null;
            try {
                outputFile = new FileOutputStream(new File(recipeFolder, "example_recipe.yml"));
                byte[] buf = new byte[1024];
                int i;
                while ((i = exampleRecipeSource.read(buf)) != -1) {
                    outputFile.write(buf, 0, i);
                }
            }
            catch (Exception ex) {
                this.logException("Failed to copy example recipe to recipes folder.", ex);
            } finally {
                try {
                    if (outputFile != null) {
                        outputFile.close();
                    }
                    if (exampleRecipeSource != null) {
                        exampleRecipeSource.close();
                    }   
                }
                catch (Exception ex) {
                    this.logException("Failed to copy example recipe to recipes folder.", ex);
                }
            }
        }
        
        // Load in all recipe files
        for (File recipeFile : recipeFolder.listFiles()) {
            loadRecipe(recipeFile);
        }        
    }
    
    /**
     * 
     * @param recipeFile 
     */
    private void loadRecipe(File recipeFile) {
        
        PluginConfiguration recipeConfiguration = new PluginConfiguration(this);
        
        // Result item
        recipeConfiguration.registerSetting(Setting.RecipeResultItem, Material.STONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeResultQuantity, 1);
        
        // Ingredients
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot01, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot02, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot03, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot04, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot05, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot06, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot07, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot08, Material.COBBLESTONE.name());
        recipeConfiguration.registerSetting(Setting.RecipeIngredientSlot09, Material.COBBLESTONE.name());
                
        // Load the file
        recipeConfiguration.loadPluginConfiguration(recipeFile);
        
        Material resultMaterial = Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeResultItem, Material.STONE.name()));
        int resultQuantity = recipeConfiguration.getSetting(Setting.RecipeResultQuantity, 1);
        
        Material[] materials = new Material[] {
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot01, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot02, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot03, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot04, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot05, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot06, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot07, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot08, Material.COBBLESTONE.name())),         
            Material.valueOf(recipeConfiguration.getSetting(Setting.RecipeIngredientSlot09, Material.COBBLESTONE.name())),         
        };
        
        char[] materialMapId = new char[] {
            '*', '#', '@',
            '%', '^', '&',
            '!', '£', '$'
        };
        
        String[] recipeMap = new String[3];
        
        for (int rowIndex = 0; rowIndex < 3; ++rowIndex) {
            String rowMap = "";
            for (int columnIndex = 0; columnIndex < 3; ++columnIndex) {
                int entryIndex = (rowIndex * 3) + columnIndex;
                if (materials[entryIndex] == Material.AIR) {
                    rowMap += " ";
                } else {
                    rowMap += materialMapId[entryIndex];
                }
            }
            recipeMap[2 - rowIndex] = rowMap;
        }
        
        Server server = this.getServer();
        ShapedRecipe recipe = new ShapedRecipe(new ItemStack(resultMaterial, resultQuantity));
        recipe.shape(recipeMap);
        
        for (int slot = 0; slot < 9; ++slot) {
            if (materials[slot] != Material.AIR) {
                recipe.setIngredient(materialMapId[slot], materials[slot]);
            }
        }
        
        server.addRecipe(recipe);
        this.logInfo("Loaded Custom Recipe '" + recipeFile.getName() + "'.");
    }
}
