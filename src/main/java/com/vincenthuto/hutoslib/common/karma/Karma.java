package com.vincenthuto.hutoslib.common.karma;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.util.ValueIOSerializable;

public class Karma implements IKarma, ValueIOSerializable {
private boolean active = false;
private float karma = 0.0F;

@Override
public void serialize(ValueOutput output) {
output.putBoolean("Active", active);
output.putFloat("Amount", karma);
}

@Override
public void deserialize(ValueInput input) {
this.active = input.getBooleanOr("Active", false);
this.karma = input.getFloatOr("Amount", 0.0F);
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
