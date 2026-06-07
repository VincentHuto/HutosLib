package com.vincenthuto.hutoslib.common.block.entity;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.registry.HLBlockInit;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class HLBlockEntityInit {

public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister
.create(Registries.BLOCK_ENTITY_TYPE, HutosLib.MOD_ID);

public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DisplayPedestalBlockEntity>> display_pedestal = BLOCK_ENTITIES
.register("display_pedestal", () -> BlockEntityType.Builder
.of(DisplayPedestalBlockEntity::new, HLBlockInit.display_pedestal.get()).build(null));

public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<LightningTesterBlockEntity>> lightning_tester = BLOCK_ENTITIES
.register("lightning_tester_block", () -> BlockEntityType.Builder
.of(LightningTesterBlockEntity::new, HLBlockInit.lightning_tester_block.get()).build(null));
}
