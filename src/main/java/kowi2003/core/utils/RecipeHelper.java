package kowi2003.core.utils;

import java.util.HashSet;
import java.util.Set;

import kowi2003.core.recipes.ProcessingResult;
import kowi2003.core.recipes.ingredient.ProcessingFluidIngredient;
import kowi2003.core.recipes.ingredient.ProcessingIngredient;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

/**
 * a big helper class containing a lot of helper methods related to checking, inserting and extracting recipe ingredients and results
 * 
 * @author KOWI2003
 */
public class RecipeHelper {
    
    /**
     * checks to see if there is any space in the in container for the item stack.
     * Note that this checks all of the slots in the container.
     * 
     * @param container the container to check
     * @param stack the stack to check for space for
     * @return whether there was space to fit the entire stack
     */
    public static boolean checkForOutputSpace(IItemHandler container, ItemStack stack) {
        stack = stack.copy();
        for (int i = 0; i < container.getSlots(); i++) {
            stack = container.insertItem(i, stack, true);
            if(stack.isEmpty()) return true;
        }
        return stack.isEmpty();
    }

    /**
     * checks to see if there is any space in the in container for the item stack.
     * Note that this checks only the slots between the offset and offset + count
     * 
     * @param container the container to check
     * @param outputSlotOffset the offset from which slot to check for space from
     * @param outputSlotCount the amount of slots from the offset to check for space for
     * @param stack the stack to check for space for
     * @return whether there was space to fit the entire stack
     */
    public static boolean checkForOutputSpace(IItemHandler container, int outputSlotOffset, int outputSlotCount, ItemStack stack) {
        stack = stack.copy();
        for (int i = 0; i < Math.min(outputSlotCount, container.getSlots() - outputSlotOffset); i++) {
            stack = container.insertItem(i + outputSlotOffset, stack, true);
            if(stack.isEmpty()) return true;
        }
        return stack.isEmpty();
    }

    /**
     * checks to see if there is any space in the in container for the all of the item stack in the results nonnull list.
     * Note that this checks only the slots between the offset and offset + count
     * 
     * @param container the container to check
     * @param outputSlotOffset the offset from which slot to check for space from
     * @param outputSlotCount the amount of slots from the offset to check for space for
     * @param items a list of results/itemstack for which needs to be checked if there is any space
     * @return whether there was space to fit all the results
     */
    public static boolean checkForOutputSpace(IItemHandler container, int outputSlotOffset, int outputSlotCount, NonNullList<ProcessingResult> items) {
        if(items.size() > 1) {
            outputSlotCount = Math.min(outputSlotCount, container.getSlots() - outputSlotOffset);

            // Creating temporary copy of output slots
            IItemHandler temp = new ItemStackHandler(outputSlotCount);
            for (int i = 0; i < outputSlotCount; i++)
                temp.insertItem(i, container.getStackInSlot(i + outputSlotOffset), false);

            // Actually checking for space by actually inserting into the temporary copy
            boolean condition = true;
            for (var result : items) {
                condition = insertOutput(temp, result.getItem()).isEmpty() && condition;
                if(!condition) break;
            }
                
            return condition;
        }else if(items.size() == 1)
            return checkForOutputSpace(container, outputSlotOffset, outputSlotCount, items.get(0).getItem());

        return true;
    }

    /**
     * checks to see if there is any space in the in tank for the fluid stack.
     * 
     * @param tank the tank to check
     * @param fluid the fluid stack to check for space for
     * @return whether there was space to fit the entire fluid stack
     */
    public static boolean checkForOutputSpace(IFluidHandler tank, FluidStack fluid) {
        fluid = fluid.copy();
        int filled = tank.fill(fluid, FluidAction.SIMULATE);
        fluid.setAmount(fluid.getAmount() - filled);
		return fluid.isEmpty();
    }

    /**
     * tries to insert the item stack into the container.
     * Note that this checks all of the slots in the container.
     * 
     * @param container the container to insert the item into
     * @param stack the stack to insert into the container 
     * @return the remaining item stack or an empty item stack if the entire stack was inserted
     */
    public static ItemStack insertOutput(IItemHandler container, ItemStack stack) {
        stack = stack.copy();
        for (int i = 0; i < container.getSlots(); i++) {
            stack = container.insertItem(i, stack, false);
            if(stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    /**
     * tries to insert the item stack into the container.
     * Note that this checks only the slots between the offset and offset + count
     * 
     * @param container the container to insert the item into
     * @param outputSlotOffset the offset from which slot to check for space from
     * @param outputSlotCount the amount of slots from the offset to check for space for
     * @param stack the stack to insert into the container 
     * @return the remaining item stack or an empty item stack if the entire stack was inserted
     */
    public static ItemStack insertOutput(IItemHandler handler, int outputSlotOffset, int outputSlotCount, ItemStack stack) {
        stack = stack.copy();
        for (int i = 0; i < Math.min(outputSlotCount, handler.getSlots() - outputSlotOffset); i++) {
			stack = handler.insertItem(i + outputSlotOffset, stack, false);
            if(stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    /**
     * tries to insert the fluid stack into the tank.
     * 
     * @param tank the tank to insert the fluid into
     * @param result the fluid stack to insert into the tank 
     * @return the remaining fluid stack or an empty fluid stack if the entire stack was inserted
     */
    public static FluidStack insertOutput(IFluidHandler tank, FluidStack result) {
        result = result.copy();
        int filled = tank.fill(result, FluidAction.EXECUTE);
        result.setAmount(result.getAmount() - filled);
		return result;
    }

    /**
     * Extracts the ingredients from the input slots, note that the input slots are defined as the range of slot indexes from
     * [inputSlotOffset -> inputSlotOffset + inputSlotCount]
     * 
     * Note: this is a shapeless implementation of the extract inputs method, for an shaped implementation see {@link RecipeHelper#extractShapedInput extractShapedInput}
     * 
     * @param container the container to extract the ingredients from
     * @param inputSlotOffset the starting index of the input slots
     * @param inputSlotCount the amount of input slots
     * @param ingredients the ingredients to be extracted
     * @return whether the extraction was successfull
     */
    public static boolean extractInput(IItemHandler container, int inputSlotOffset, int inputSlotCount, NonNullList<ProcessingIngredient> ingredients) {
        for(var ingredient : ingredients) {
            int count = ingredient.getCount();
            for (int i = 0; i < inputSlotCount; i++) {
                if(ingredient.getIngredient().test(container.getStackInSlot(i))) {
                    var stack = container.extractItem(i + inputSlotOffset, count, false);
                    count -= stack.getCount();
                }
            }
            if(count > 0)
                return false;
        }
        return true;
    }

    /**
     * Extracts the ingredients from the input slots, note that the input slots are defined as the range of slot indexes from
     * [inputSlotOffset -> inputSlotOffset + inputSlotCount]
     * 
     * Note: this is a shaped implementation of the extract inputs method, for an shapeless implementation see {@link RecipeHelper#extractInput extractInput}
     * 
     * @param container the container to extract the ingredients from
     * @param inputSlotOffset the starting index of the input slots
     * @param inputSlotCount the amount of input slots
     * @param ingredients the ingredients to be extracted
     * @return whether the extraction was successfull
     */
    public static boolean extractShapedInput(IItemHandler container, int inputSlotOffset, int inputSlotCount, NonNullList<ProcessingIngredient> ingredients) {
        inputSlotCount = Math.min(inputSlotCount, container.getSlots() - inputSlotOffset);
        for(int i = 0; i < inputSlotCount; i++) {
            var ingredient = i >= ingredients.size() ? ProcessingIngredient.EMPTY : ingredients.get(i);

            int count = ingredient.getCount();
            if(ingredient.test(container.getStackInSlot(i))) {
                var stack = container.extractItem(i + inputSlotOffset, count, false);
                count -= stack.getCount();
            }else 
                return false;

            if(count > 0)
                return false;
        }
        return true;
    }

    /**
     * checks to see if the ingredients exist in the input slots of the container.
     * the input slots are defined by the slotOffset and the slotCount, indexes: [inputSlotOffset -> inputSlotOffset + inputSlotCount]
     * 
     * @param container the container to check
     * @param slotOffset the starting index of the input slots
     * @param slotCount the amount of input slots
     * @param ingredients a list of ingredients to check
     * @return whether all the ingredients are present
     */
    public static boolean check(IItemHandlerModifiable container, int slotOffset, int slotCount, NonNullList<ProcessingIngredient> ingredients) {
        return check(new RecipeWrapper(container), slotOffset, slotCount, ingredients);
    }

    /**
     * checks to see if the ingredients exist in the input slots of the container.
     * the input slots are defined by the slotOffset and the slotCount, indexes: [inputSlotOffset -> inputSlotOffset + inputSlotCount]
     * 
     * @param container the container to check
     * @param slotOffset the starting index of the input slots
     * @param slotCount the amount of input slots
     * @param ingredients a list of ingredients to check
     * @return whether all the ingredients are present
     */
    public static boolean check(Container container, int slotOffset, int slotCount, NonNullList<ProcessingIngredient> ingredients) {
        Set<Integer> checkedSlots = new HashSet<>();
        int i = 0;
        slotCount = Math.min(slotCount, container.getContainerSize() - slotOffset);
        for (var ingredient : ingredients) {
            for (int j = 0; j < slotCount; j++) {
                int slot = j + slotOffset;
                if(checkedSlots.contains(slot)) 
                    continue;
                var stack = container.getItem(slot);
                if(ingredient.test(stack)) {
                    checkedSlots.add(slot);
                    i++;
                }
            }

            if(i == ingredients.size()) {
                if(checkedSlots.size() != slotCount) {
                    for (int j = 0; j < slotCount; j++) {
                        int slot = j + slotOffset;
                        if(checkedSlots.contains(slot))
                            continue;
                        if(container.getItem(slot).isEmpty())
                            checkedSlots.add(slot);
                    }
                }
                
                return checkedSlots.size() == slotCount;
            }
        }

        return i == ingredients.size();
    }

     /**
     * checks to see if the fluid ingredients exist in the input tanks of the container.
     * 
     * @param handler the tanks to check
     * @param ingredients a list of ingredients to check
     * @return whether all the fluid ingredients are present
     */
    public static boolean check(IFluidHandler[] handler, NonNullList<ProcessingFluidIngredient> ingredients) {
        return check(handler, 0, Math.min(handler.length, ingredients.size()), ingredients);
    }

    /**
     * checks to see if the fluid ingredients exist in the input tanks of the container.
     * the input tanks are defined by the slotOffset and the slotCount, indexes: [inputSlotOffset -> inputSlotOffset + inputSlotCount]
     * 
     * @param handler the tanks to check
     * @param slotOffset the starting index of the input slots
     * @param slotCount the amount of input slots
     * @param ingredients a list of ingredients to check
     * @return whether all the fluid ingredients are present
     */
    public static boolean check(IFluidHandler[] handler, int slotOffset, int slotCount, NonNullList<ProcessingFluidIngredient> ingredients) {
        slotCount = Math.min(handler.length - slotOffset, slotCount);
        for (int i = 0; i < Math.min(slotCount, ingredients.size()); i++) {
            var ingredient = ingredients.get(i);
            var fluid = handler[i + slotOffset];
            if(!ingredient.test(fluid.getFluidInTank(0)))
                return false;
        }
        return true;
    }
}
