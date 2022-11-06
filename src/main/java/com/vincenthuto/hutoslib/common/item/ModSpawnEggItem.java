package com.vincenthuto.hutoslib.common.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.RegistryObject;

//TODO remove because of ForgeSpawnEggItem
public class ModSpawnEggItem extends SpawnEggItem {

	protected static final List<ModSpawnEggItem> UNADDED_EGGS = new ArrayList<>();
	@SuppressWarnings("unchecked")
	public static void initSpawnEggs() {
		final Map<EntityType<? extends Mob>, SpawnEggItem> EGGS = ObfuscationReflectionHelper
				.getPrivateValue(SpawnEggItem.class, null, "field_195987_b");
		for (final SpawnEggItem spawnEgg : UNADDED_EGGS) {
			EGGS.put((EntityType<? extends Mob>) spawnEgg.getType(null), spawnEgg);
		}
		UNADDED_EGGS.clear();
	}

	private final Lazy<? extends EntityType<?>> entityTypeSupplier;

	@SuppressWarnings("deprecation")
	public ModSpawnEggItem(final RegistryObject<? extends EntityType<?>> entityTypeSupplier, final int primaryColour,
			final int secondaryColour, final Item.Properties properties) {
		super(null, primaryColour, secondaryColour, properties);
		this.entityTypeSupplier = Lazy.of(entityTypeSupplier::get);
		UNADDED_EGGS.add(this);
	}

	@Override
	public EntityType<?> getType(CompoundTag p_43229_) {
		return this.entityTypeSupplier.get();
	}

}