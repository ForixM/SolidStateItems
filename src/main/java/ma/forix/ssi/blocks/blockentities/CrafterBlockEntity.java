package ma.forix.ssi.blocks.blockentities;

import ma.forix.ssi.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Collection;
import java.util.List;

public class CrafterBlockEntity extends NetworkableBE {
    public CrafterBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.CRAFTER_BLOCK_ENTITY.get(), pPos, pBlockState);
        System.out.println("new block entity from crafterblock");
    }

    @Override
    public void tickServer(Level level){
        super.tickServer(level);
    }

    public ItemStack craft(List<ItemStack> ingredients, Item ordered){
        System.out.println("crafter called");
        RecipeManager recipeManager = level.getRecipeManager();
        Collection<Recipe<?>> recipes = recipeManager.getRecipes();
        ItemStack test = new ItemStack(ordered);
        int i = 0;
        for (Recipe<?> recipe : recipes) {
            if (recipe.getResultItem().getItem() == ordered){
                NonNullList<Ingredient> requiredIngredients = recipe.getIngredients();
                for (Ingredient ingredient : requiredIngredients) {
                    for (ItemStack item : ingredient.getItems()) {
                        if (i < ingredients.size() && item.sameItem(ingredients.get(i))){
                            i++;
                            break;
                        }
                        return ItemStack.EMPTY;
                    }
                }
                return recipe.getResultItem().copy();
            }
        }
        return ItemStack.EMPTY;

//        for (Recipe<?> recipe : recipes) {
//            System.out.println("New recipe for: "+recipe.getResultItem());
//            NonNullList<Ingredient> ingredients = recipe.getIngredients();
//            for (Ingredient ingredient : ingredients) {
//                ItemStack[] stacks = ingredient.getItems();
//                if (stacks.length > 0)
//                    System.out.println("    "+stacks[0]);
//            }
//            System.out.println("-----------------------");
//        }
    }
}
