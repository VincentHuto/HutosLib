package com.vincenthuto.hutoslib.common.karma;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class Karma implements IKarma, INBTSerializable<CompoundTag> {
private boolean active = false;
private float karma = 0.0F;

@Override
public CompoundTag serializeNBT() {
CompoundTag tag = new CompoundTag();
tag.putBoolean("Active", active);
tag.putFloat("Amount", karma);
return tag;
}

@Override
public void deserializeNBT(CompoundTag nbt) {
if (nbt.contains("Active") && nbt.contains("Amount")) {
this.active = nbt.getBoolean("Active");
this.karma = nbt.getFloat("Amount");
}
}

@Override
public void addKarma(float points) { this.karma += points; }

@Override
public float getKarma() { return this.karma; }

@Override
public boolean isActive() { return active; }

@Override
public void setActive(boolean set) { this.active = set; }

@Override
public void setKarma(float points) { this.karma = points; }

@Override
public void subtractKarma(float points) { this.karma -= points; }

@Override
public void toggleActive() { this.active = !active; }
}
