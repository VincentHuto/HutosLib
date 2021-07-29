package com.hutoslib.common.enity;

import javax.annotation.Nullable;

import com.hutoslib.client.model.capability.Animation;
import com.hutoslib.client.model.capability.IAnimatable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class TestMobEntity extends AbstractSkeleton implements IAnimatable {
	public TestMobEntity(EntityType<? extends TestMobEntity> p_34166_, Level p_34167_) {
		super(p_34166_, p_34167_);
		this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
	}

	protected void registerGoals() {
		this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractPiglin.class, true));
		super.registerGoals();
	}

	protected SoundEvent getAmbientSound() {
		return SoundEvents.WITHER_SKELETON_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource p_34195_) {
		return SoundEvents.WITHER_SKELETON_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.WITHER_SKELETON_DEATH;
	}

	protected SoundEvent getStepSound() {
		return SoundEvents.WITHER_SKELETON_STEP;
	}

	protected void dropCustomDeathLoot(DamageSource p_34174_, int p_34175_, boolean p_34176_) {
		super.dropCustomDeathLoot(p_34174_, p_34175_, p_34176_);
		Entity entity = p_34174_.getEntity();
		if (entity instanceof Creeper) {
			Creeper creeper = (Creeper) entity;
			if (creeper.canDropMobsSkull()) {
				creeper.increaseDroppedSkulls();
				this.spawnAtLocation(Items.WITHER_SKELETON_SKULL);
			}
		}

	}

	protected void populateDefaultEquipmentSlots(DifficultyInstance p_34172_) {
		this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SWORD));
	}

	protected void populateDefaultEquipmentEnchantments(DifficultyInstance p_34184_) {
	}

	@Nullable
	public SpawnGroupData finalizeSpawn(ServerLevelAccessor p_34178_, DifficultyInstance p_34179_,
			MobSpawnType p_34180_, @Nullable SpawnGroupData p_34181_, @Nullable CompoundTag p_34182_) {
		SpawnGroupData spawngroupdata = super.finalizeSpawn(p_34178_, p_34179_, p_34180_, p_34181_, p_34182_);
		this.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(4.0D);
		this.reassessWeaponGoal();
		return spawngroupdata;
	}

	protected float getStandingEyeHeight(Pose p_34186_, EntityDimensions p_34187_) {
		return 2.1F;
	}

	public boolean doHurtTarget(Entity p_34169_) {
		if (!super.doHurtTarget(p_34169_)) {
			return false;
		} else {
			if (p_34169_ instanceof LivingEntity) {
				((LivingEntity) p_34169_).addEffect(new MobEffectInstance(MobEffects.WITHER, 200), this);
			}

			return true;
		}
	}

	protected AbstractArrow getArrow(ItemStack p_34189_, float p_34190_) {
		AbstractArrow abstractarrow = super.getArrow(p_34189_, p_34190_);
		abstractarrow.setSecondsOnFire(100);
		return abstractarrow;
	}

	public boolean canBeAffected(MobEffectInstance p_34192_) {
		return p_34192_.getEffect() == MobEffects.WITHER ? false : super.canBeAffected(p_34192_);
	}

	// Animation
	private int animationTick;
	private Animation animation = NO_ANIMATION;
	public static final Animation LIGHTNING_ANIMATION = new Animation(64);
	public static final Animation CHARGE_ANIMATION = new Animation(104);
	public static final Animation BITE_ANIMATION = new Animation(17);

	@Override
	public int getAnimationTick() {
		return animationTick;
	}

	@Override
	public void setAnimationTick(int tick) {
		animationTick = tick;
	}

	@Override
	public Animation getAnimation() {
		return animation;
	}

	@Override
	public void setAnimation(Animation animation) {
		if (animation == null)
			animation = NO_ANIMATION;
		setAnimationTick(0);
		this.animation = animation;
	}

	@Override
	public Animation[] getAnimations() {
		return new Animation[] { BITE_ANIMATION, LIGHTNING_ANIMATION, CHARGE_ANIMATION };
	}
}