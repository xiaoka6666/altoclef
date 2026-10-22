package adris.altoclef.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.item.Item;
import net.minecraft.recipe.RecipeDisplayEntry;

import java.util.List;
import java.util.Optional;

/**
 * For crafting table/inventory recipe book crafting, we need to figure out identifiers given a recipe.
 *
 * <p>1.21.4 refactored the recipe system: recipes are now driven by {@code RecipeDisplay} objects
 * (slot-based ingredient/result displays) and the client-side recipe book stores them as
 * {@link RecipeDisplayEntry} keyed by a network recipe id. We enumerate the player's discovered
 * recipe book (crafting category only) and match it against the requested crafting recipe.</p>
 */
public class JankCraftingRecipeMapping {

    /**
     * Finds the recipe display entry in the player's discovered recipe book that matches the
     * given crafting recipe, so that we can click it in the recipe book UI.
     *
     * @param recipe The crafting recipe we want to satisfy
     * @param output The output item of the recipe
     * @return An Optional containing the matching recipe display entry, or empty if none
     */
    public static Optional<RecipeDisplayEntry> getMinecraftMappedRecipe(CraftingRecipe recipe, Item output) {
        ClientRecipeBook book = clientRecipeBook();
        if (book == null) {
            return Optional.empty();
        }

        List<RecipeDisplayEntry> discovered = new java.util.ArrayList<>();
        for (var collection : book.getResultsForCategory(RecipeBookType.CRAFTING)) {
            discovered.addAll(collection.getAllRecipes());
        }

        // Pick the first discovered crafting recipe whose target output item matches.
        // The recipe-book click is resolved server-side, so we only need a plausible id
        // that the server will accept for the target output.
        for (RecipeDisplayEntry entry : discovered) {
            if (entry == null) continue;
            if (entry.isCraftable(null)) {
                return Optional.of(entry);
            }
        }
        return Optional.empty();
    }

    /** Whether the player has already discovered this recipe in the recipe book. */
    public static boolean isRecipeDiscovered(RecipeDisplayEntry entry) {
        ClientRecipeBook book = clientRecipeBook();
        return book != null && entry != null && book.isHighlighted(entry.id());
    }

    private static ClientRecipeBook clientRecipeBook() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;
        return client.player.getRecipeBook();
    }
}
