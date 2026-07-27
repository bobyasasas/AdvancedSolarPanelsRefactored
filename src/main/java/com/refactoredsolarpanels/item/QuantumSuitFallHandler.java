package com.refactoredsolarpanels.item;

import com.refactoredsolarpanels.registry.ModItems;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public final class QuantumSuitFallHandler {
    private QuantumSuitFallHandler() {
    }

    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }

        ItemStack boots = event.getEntity().getItemBySlot(EquipmentSlot.FEET);
        if (boots.getItem() != ModItems.LAPIS_QUANTUM_BOOTS.get()
                || !(boots.getItem() instanceof ItemArmorQuantumSuit quantumBoots)) {
            return;
        }

        // IC2's fall handler checks its own quantum boots by item identity, so
        // subclasses such as the enchantable lapis variant never reach this method.
        if (quantumBoots.absorbFall(boots, event.getDistance())) {
            event.setCanceled(true);
        }
    }
}
