package com.vincenthuto.hutoslib.common.template;

import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;
import com.vincenthuto.hutoslib.common.registry.HLItemInit;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.world.item.ItemStack;

public enum EffectTemplateType {
	LIGHTNING, TENDRIL;

	public boolean matches(ItemStack stack) {
		return switch (this) {
		case LIGHTNING -> stack.is(HLItemInit.lightning_template.get());
		case TENDRIL -> stack.is(HLItemInit.tendril_template.get());
		};
	}

	public String defaultJson() {
		return switch (this) {
		case LIGHTNING -> EffectTemplateJson.toLightningJson(LightningTestConfig.defaults());
		case TENDRIL -> EffectTemplateJson.toTendrilJson(TendrilEffectConfig.defaults());
		};
	}

	public String jsonFromItem(ItemStack stack) {
		return switch (this) {
		case LIGHTNING -> EffectTemplateJson.toLightningJson(LightningTestConfig.fromItem(stack));
		case TENDRIL -> EffectTemplateJson.toTendrilJson(TendrilEffectConfig.fromItem(stack));
		};
	}

	public void writeJsonToItem(ItemStack stack, String json) {
		switch (this) {
		case LIGHTNING -> EffectTemplateJson.parseLightning(json).writeToItem(stack);
		case TENDRIL -> EffectTemplateJson.parseTendril(json).writeToItem(stack);
		}
	}

	public void validateJson(String json) {
		switch (this) {
		case LIGHTNING -> EffectTemplateJson.parseLightning(json);
		case TENDRIL -> EffectTemplateJson.parseTendril(json);
		}
	}
}
