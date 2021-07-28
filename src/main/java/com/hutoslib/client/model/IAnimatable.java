package com.hutoslib.client.model;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public interface IAnimatable {

	Animation NO_ANIMATION = new Animation(0) {
		@Override
		public String toString() {
			return "NO_ANIMATION";
		}
	};

	int getAnimationTick();

	void setAnimationTick(int tick);

	Animation getAnimation();

	void setAnimation(Animation animation);

	Animation[] getAnimations();

	default boolean noActiveAnimation() {
		return getAnimation() == NO_ANIMATION;
	}

	default void updateAnimations() {
		Animation current = getAnimation();
		if (current != NO_ANIMATION) {
			int tick = getAnimationTick() + 1;
			if (tick >= current.getDuration()) {
				setAnimation(NO_ANIMATION);
				tick = 0;
			}
			setAnimationTick(tick);
		}
	}

	static void registerCapability() {
		/*
		 * , new Capability.IStorage<IAnimatable>() { // There is no data needed to be
		 * stored.
		 * 
		 * @Nullable
		 * 
		 * @Override public Tag writeNBT(Capability<IAnimatable> capability, IAnimatable
		 * instance, Direction side) { return null; }
		 * 
		 * @Override public void readNBT(Capability<IAnimatable> capability, IAnimatable
		 * instance, Direction side, Tag nbt) { } }, CapImpl::new);
		 */
		CapabilityManager.INSTANCE.register(IAnimatable.class);
	}

	class CapImpl implements IAnimatable {
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
}