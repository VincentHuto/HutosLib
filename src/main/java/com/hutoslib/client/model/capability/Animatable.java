package com.hutoslib.client.model.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class Animatable implements IAnimatable {
	@CapabilityInject(IAnimatable.class)
	public static final Capability<IAnimatable> CAPABILITY = null;

	private int animationTick = 0;
	private Animation animation;

	@Override
	public int getAnimationTick() {
		return animationTick;
	}

	@Override
	public void setAnimationTick(int tick) {
		this.animationTick = tick;
	}

	@Override
	public Animation getAnimation() {
		return animation;
	}

	@Override
	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	@Override
	public Animation[] getAnimations() {
		return new Animation[0];
	}
}
