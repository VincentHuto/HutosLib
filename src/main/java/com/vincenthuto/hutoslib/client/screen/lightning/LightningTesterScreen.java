package com.vincenthuto.hutoslib.client.screen.lightning;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.lightning.LightningTestColors;
import com.vincenthuto.hutoslib.common.lightning.LightningTestConfig;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

abstract class LightningTesterScreen extends Screen {
	private record ColorControl(EditBox input, IntSetter setter) {
	}

	private record Row(String label, Supplier<String> value, int x, int y) {
	}

	protected LightningTestConfig config;
	private final boolean blockScreen;
	private final List<ColorControl> colorControls = new ArrayList<>();
	private final List<Row> rows = new ArrayList<>();

	protected LightningTesterScreen(Component title, LightningTestConfig config, boolean blockScreen) {
		super(title);
		this.config = config.clamped();
		this.blockScreen = blockScreen;
	}

	@Override
	protected void init() {
		colorControls.clear();
		rows.clear();
		int left = Math.max(8, width / 2 - 220);
		int right = left + 220;
		int top = 36;
		int step = 22;

		addToggle(left, top, "Backend", () -> config.backend().name(), () -> config.backend().name(),
				"Chooses between the custom bolt renderer and the legacy particle lightning.",
				() -> set(new LightningTestConfig(
				config.backend() == LightningTestConfig.Backend.BOLT ? LightningTestConfig.Backend.PARTICLE
						: LightningTestConfig.Backend.BOLT,
				config.colorPreset(), config.outerColor(), config.innerColor(), config.range(), config.targetOffsetX(),
				config.targetOffsetY(), config.targetOffsetZ(), config.ticksPerMeter(), config.speed(), config.maxAge(),
				config.fract(), config.maxOffset(), config.size(), config.fixedSeed(), config.seed(), config.repeat(),
				config.repeatInterval())));
		addColor(left, top + step, "Preset", () -> config.colorPreset(), value -> set(new LightningTestConfig(
				config.backend(), value, value, config.innerColor(), config.range(), config.targetOffsetX(),
				config.targetOffsetY(), config.targetOffsetZ(), config.ticksPerMeter(), config.speed(), config.maxAge(),
				config.fract(), config.maxOffset(), config.size(), config.fixedSeed(), config.seed(), config.repeat(),
				config.repeatInterval())), "Preset color applied to the particle backend and copied to Outer.");
		addColor(left, top + step * 2, "Outer", () -> config.outerColor(), value -> set(new LightningTestConfig(
				config.backend(), config.colorPreset(), value, config.innerColor(), config.range(), config.targetOffsetX(),
				config.targetOffsetY(), config.targetOffsetZ(), config.ticksPerMeter(), config.speed(), config.maxAge(),
				config.fract(), config.maxOffset(), config.size(), config.fixedSeed(), config.seed(), config.repeat(),
				config.repeatInterval())), "Main bolt color for the BOLT backend.");
		addColor(left, top + step * 3, "Inner", () -> config.innerColor(), value -> set(new LightningTestConfig(
				config.backend(), config.colorPreset(), config.outerColor(), value, config.range(), config.targetOffsetX(),
				config.targetOffsetY(), config.targetOffsetZ(), config.ticksPerMeter(), config.speed(), config.maxAge(),
				config.fract(), config.maxOffset(), config.size(), config.fixedSeed(), config.seed(), config.repeat(),
				config.repeatInterval())), "Secondary inner bolt color for the BOLT backend.");
		addFloat(left, top + step * 4, "Range", () -> config.range(), 1.0F, value -> setRange(value),
				"Distance in blocks for player-look test shots.");
		addFloat(left, top + step * 5, "Offset X", () -> config.targetOffsetX(), 1.0F, value -> setOffset(value,
				config.targetOffsetY(), config.targetOffsetZ()), "Block tester target offset on the X axis.");
		addFloat(left, top + step * 6, "Offset Y", () -> config.targetOffsetY(), 1.0F, value -> setOffset(
				config.targetOffsetX(), value, config.targetOffsetZ()), "Block tester target offset on the Y axis.");
		addFloat(left, top + step * 7, "Offset Z", () -> config.targetOffsetZ(), 1.0F, value -> setOffset(
				config.targetOffsetX(), config.targetOffsetY(), value), "Block tester target offset on the Z axis.");

		addFloat(right, top, "Ticks/M", () -> config.ticksPerMeter(), 4.0F, value -> setTicksPerMeter(value),
				"Bolt lifetime scale per block of distance.");
		addFloat(right, top + step, "Speed", () -> config.speed(), 0.25F, value -> setSpeed(value),
				"Animation speed; higher values make both backends finish faster.");
		addInt(right, top + step * 2, "Max Age", () -> config.maxAge(), 1, value -> setMaxAge(value),
				"Maximum lifetime in ticks for either backend.");
		addInt(right, top + step * 3, "Fract", () -> config.fract(), 1, value -> setFract(value),
				"Lightning subdivision count for jagged detail.");
		addFloat(right, top + step * 4, "Max Off", () -> config.maxOffset(), 0.05F, value -> setMaxOffset(value),
				"Maximum branch/random offset for lightning shape.");
		addFloat(right, top + step * 5, "Size", () -> config.size(), 0.01F, value -> setSize(value),
				"Rendered bolt width for the BOLT backend.");
		addToggle(right, top + step * 6, "Fixed Seed", () -> onOff(config.fixedSeed()), () -> onOff(config.fixedSeed()),
				"Keeps the same random bolt shape while testing.",
				() -> set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(),
						config.innerColor(), config.range(), config.targetOffsetX(), config.targetOffsetY(),
						config.targetOffsetZ(), config.ticksPerMeter(), config.speed(), config.maxAge(), config.fract(),
						config.maxOffset(), config.size(), !config.fixedSeed(), config.seed(), config.repeat(),
						config.repeatInterval())));
		addLong(right, top + step * 7, "Seed", () -> config.seed(), 1L, value -> setSeed(value),
				"Random seed used when Fixed Seed is ON.");

		if (blockScreen) {
			addToggle(left, top + step * 8, "Repeat", () -> onOff(config.repeat()), () -> onOff(config.repeat()),
					"Makes the placed block spawn lightning repeatedly.",
					() -> set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(),
							config.innerColor(), config.range(), config.targetOffsetX(), config.targetOffsetY(),
							config.targetOffsetZ(), config.ticksPerMeter(), config.speed(), config.maxAge(),
							config.fract(), config.maxOffset(), config.size(), config.fixedSeed(), config.seed(),
							!config.repeat(), config.repeatInterval())));
			addInt(right, top + step * 8, "Interval", () -> config.repeatInterval(), 5, value -> setRepeatInterval(value),
					"Ticks between repeated block lightning spawns.");
		}

		int buttonY = Math.min(height - 28, top + step * 10);
		addRenderableWidget(Button.builder(Component.literal("Test"), button -> {
			commitColorInputs();
			onTest();
		}).bounds(width / 2 - 126, buttonY, 76, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
			commitColorInputs();
			onSave();
		}).bounds(width / 2 - 38, buttonY, 76, 20).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose()).bounds(width / 2 + 50,
				buttonY, 76, 20).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		renderBackground(graphics, mouseX, mouseY, partialTick);
		graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
		for (Row row : rows) {
			graphics.drawString(font, row.label(), row.x(), row.y() + 5, 0xD8D8D8, false);
			String value = row.value().get();
			if (!value.isEmpty()) {
				graphics.drawString(font, value, row.x() + 142, row.y() + 5, 0xFFFFFF, false);
			}
		}
		for (Renderable renderable : renderables) {
			renderable.render(graphics, mouseX, mouseY, partialTick);
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private interface FloatSetter {
		void set(float value);
	}

	private interface IntSetter {
		void set(int value);
	}

	private interface LongSetter {
		void set(long value);
	}

	private void addColor(int x, int y, String label, Supplier<Integer> getter, IntSetter setter, String description) {
		rows.add(new Row(label, () -> "", x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("<"), button -> {
			int value = getter.get();
			for (int i = 0; i < currentStepMultiplier(); i++) {
				value = LightningTestColors.previousPreset(value);
			}
			setter.set(value);
			rebuildWidgets();
		}).bounds(x + 105, y, 18, 20).build(), description));
		EditBox input = new EditBox(font, x + 126, y + 1, 58, 18, Component.literal(label));
		input.setMaxLength(10);
		input.setTooltip(Tooltip.create(Component.literal(description)));
		input.setValue(LightningTestColors.displayName(getter.get()));
		input.setResponder(value -> {
			OptionalInt parsed = LightningTestColors.parseColor(value);
			if (parsed.isPresent()) {
				setter.set(parsed.getAsInt());
				input.setTextColor(0xE0E0E0);
			} else {
				input.setTextColor(0xFF6666);
			}
		});
		colorControls.add(new ColorControl(input, setter));
		addRenderableWidget(input);
		addRenderableWidget(withTooltip(Button.builder(Component.literal(">"), button -> {
			int value = getter.get();
			for (int i = 0; i < currentStepMultiplier(); i++) {
				value = LightningTestColors.nextPreset(value);
			}
			setter.set(value);
			rebuildWidgets();
		}).bounds(x + 188, y, 18, 20).build(), description));
	}

	private void addFloat(int x, int y, String label, Supplier<Float> getter, float step, FloatSetter setter,
			String description) {
		rows.add(new Row(label, () -> String.format("%.2f", getter.get()), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 105, y, 18, 20).build(), description));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 188, y, 18, 20).build(), description));
	}

	private void addInt(int x, int y, String label, Supplier<Integer> getter, int step, IntSetter setter,
			String description) {
		rows.add(new Row(label, () -> Integer.toString(getter.get()), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 105, y, 18, 20).build(), description));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 188, y, 18, 20).build(), description));
	}

	private void addLong(int x, int y, String label, Supplier<Long> getter, long step, LongSetter setter,
			String description) {
		rows.add(new Row(label, () -> Long.toString(getter.get()), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 105, y, 18, 20).build(), description));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 188, y, 18, 20).build(), description));
	}

	private void addToggle(int x, int y, String label, Supplier<String> getter, Supplier<String> buttonLabel,
			String description, Runnable action) {
		rows.add(new Row(label, getter, x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal(buttonLabel.get()), button -> {
			action.run();
			rebuildWidgets();
		}).bounds(x + 105, y, 76, 20).build(), description));
	}

	private int currentStepMultiplier() {
		return stepMultiplier(hasShiftDown());
	}

	static int stepMultiplier(boolean shiftDown) {
		return shiftDown ? 5 : 1;
	}

	private Button withTooltip(Button button, String description) {
		button.setTooltip(Tooltip.create(Component.literal(description + " Hold Shift for 5x steps.")));
		return button;
	}

	private void commitColorInputs() {
		for (ColorControl control : colorControls) {
			OptionalInt parsed = LightningTestColors.parseColor(control.input().getValue());
			if (parsed.isPresent()) {
				control.setter().set(parsed.getAsInt());
				control.input().setTextColor(0xE0E0E0);
			} else {
				control.input().setTextColor(0xFF6666);
			}
		}
	}

	private static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

	private void set(LightningTestConfig config) {
		this.config = config.clamped();
	}

	private void setFract(int value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), config.speed(), config.maxAge(), value, config.maxOffset(), config.size(),
				config.fixedSeed(), config.seed(), config.repeat(), config.repeatInterval()));
	}

	private void setMaxAge(int value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), config.speed(), value, config.fract(), config.maxOffset(), config.size(),
				config.fixedSeed(), config.seed(), config.repeat(), config.repeatInterval()));
	}

	private void setMaxOffset(float value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), config.speed(), config.maxAge(), config.fract(), value, config.size(),
				config.fixedSeed(), config.seed(), config.repeat(), config.repeatInterval()));
	}

	private void setOffset(float x, float y, float z) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), x, y, z, config.ticksPerMeter(), config.speed(), config.maxAge(), config.fract(),
				config.maxOffset(), config.size(), config.fixedSeed(), config.seed(), config.repeat(),
				config.repeatInterval()));
	}

	private void setRange(float value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				value, config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(), config.ticksPerMeter(),
				config.speed(), config.maxAge(), config.fract(), config.maxOffset(), config.size(), config.fixedSeed(),
				config.seed(), config.repeat(), config.repeatInterval()));
	}

	private void setRepeatInterval(int value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), config.speed(), config.maxAge(), config.fract(), config.maxOffset(),
				config.size(), config.fixedSeed(), config.seed(), config.repeat(), value));
	}

	private void setSeed(long value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), config.speed(), config.maxAge(), config.fract(), config.maxOffset(),
				config.size(), config.fixedSeed(), value, config.repeat(), config.repeatInterval()));
	}

	private void setSize(float value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), config.speed(), config.maxAge(), config.fract(), config.maxOffset(), value,
				config.fixedSeed(), config.seed(), config.repeat(), config.repeatInterval()));
	}

	private void setSpeed(float value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(),
				config.ticksPerMeter(), value, config.maxAge(), config.fract(), config.maxOffset(), config.size(),
				config.fixedSeed(), config.seed(), config.repeat(), config.repeatInterval()));
	}

	private void setTicksPerMeter(float value) {
		set(new LightningTestConfig(config.backend(), config.colorPreset(), config.outerColor(), config.innerColor(),
				config.range(), config.targetOffsetX(), config.targetOffsetY(), config.targetOffsetZ(), value,
				config.speed(), config.maxAge(), config.fract(), config.maxOffset(), config.size(), config.fixedSeed(),
				config.seed(), config.repeat(), config.repeatInterval()));
	}

	protected abstract void onSave();

	protected abstract void onTest();
}
