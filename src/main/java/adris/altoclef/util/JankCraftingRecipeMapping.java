package adris.altoclef.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.client.recipebook.RecipeBookType;
import net.minecraft.item.Item;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeDisplayEntry;
import net.minecraft.recipe.display.RecipeDisplay;
import net.minecraft.recipe.display.SlotDisplayContexts;
import net.minecraft.util.context.ContextParameterMap;

import java.util.ArrayList;
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

        ContextParameterMap ctx = clientContext();
        if (ctx == null) {
            return Optional.empty();
        }

        List<ItemTarget> toSatisfy = new ArrayList<>();
        for (ItemTarget target : recipe.getSlots()) {
            if (target != null && !target.isEmpty()) {
                toSatisfy.add(target);
            }
        }

        List<RecipeDisplayEntry> discovered = new ArrayList<>();
        for (var collection : book.getResultsForCategory(RecipeBookType.CRAFTING)) {
            discovered.addAll(collection.getAllRecipes());
        }

        for (RecipeDisplayEntry entry : discovered) {
            if (entry == null || !matchesOutput(entry, ctx, output)) continue;
            if (!matchesIngredients(entry, toSatisfy)) continue;
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    private static ClientRecipeBook clientRecipeBook() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return null;
        return client.player.getRecipeBook();
    }

    private static ContextParameterMap clientContext() {
        if (MinecraftClient.getInstance().world == null) return null;
        // 1.21.4: build a context param map with REGISTRIES + FUEL_REGISTRY so display slots
        // can resolve their result stacks.
        var builder = new net.minecraft.util.context.ContextParameterMap.Builder()
                .add(SlotDisplayContexts.REGISTRIES, MinecraftClient.getInstance().world.getRegistryManager())
                .add(SlotDisplayContexts.FUEL_REGISTRY, MinecraftClient.getInstance().world.getFuelRegistry());
        var type = new net.minecraft.util.context.ContextType.Builder()
                .require(SlotDisplayContexts.REGISTRIES)
                .allow(SlotDisplayContexts.FUEL_REGISTRY)
                .build();
        return builder.build(type);
    }

    /**
     * Check whether the display's result item matches the target output item.
     */
    private static boolean matchesOutput(RecipeDisplayEntry entry, ContextParameterMap ctx, Item output) {
        RecipeDisplay display = entry.display();
        if (display == null || display.result() == null) return false;
        return display.result().getStacks(ctx).stream().anyMatch(s -> s.getItem().equals(output));
    }

    /**
     * Check whether the display's crafting-requirement ingredients satisfy the requested
     * crafting slots. We use the entry's {@link RecipeDisplayEntry#craftingRequirements()} so
     * that we don't need to resolve display-slot stacks.
     */
    private static boolean matchesIngredients(RecipeDisplayEntry entry, List<ItemTarget> toSatisfy) {
        if (toSatisfy.isEmpty()) return true;
        List<Ingredient> requirements = entry.craftingRequirements().orElse(List.of());
        if (requirements.isEmpty()) return true;
        // 1.21.4: reject entries whose non-empty requirement count differs from the
        // requested slot count — otherwise a surplus requirement could be silently
        // dropped and the wrong display entry's id would reach clickRecipe.
        long nonEmpty = requirements.stream().filter(i -> !i.isEmpty()).count();
        if (nonEmpty != toSatisfy.size()) return false;

        List<ItemTarget> remaining = new ArrayList<>(toSatisfy);
        for (Ingredient ingredient : requirements) {
            if (ingredient.isEmpty()) continue;
            for (int i = 0; i < remaining.size(); i++) {
                ItemTarget target = remaining.get(i);
                // A target is satisfied if ANY of its matching items is accepted by this ingredient slot.
                for (Item match : target.getMatches()) {
                    if (ingredient.test(new net.minecraft.item.ItemStack(match))) {
                        remaining.remove(i);
                        break;
                    }
                }
            }
        }
        return remaining.isEmpty();
    }
}
