package com.vincenthuto.hutoslib.client.screen.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.lightning.LightningTestColors;
import com.vincenthuto.hutoslib.common.particle.GenericParticleTestConfig;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

abstract class GenericParticleTesterScreen extends Screen {
	private static final int ROW_HEIGHT = 18;
	private static final int CONTROL_HEIGHT = 16;

	private record ColorControl(EditBox input, IntSetter setter) {
	}

	private record NumericControl(EditBox input, Runnable commit) {
	}

	private record Row(String label, Supplier<String> value, int x, int y) {
	}

	protected GenericParticleTestConfig config;
	private final boolean blockScreen;
	private final List<ColorControl> colorControls = new ArrayList<>();
	private final List<NumericControl> numericControls = new ArrayList<>();
	private final List<Row> rows = new ArrayList<>();

	protected GenericParticleTesterScreen(Component title, GenericParticleTestConfig config, boolean blockScreen) {
		super(title);
		this.config = config.clamped();
		this.blockScreen = blockScreen;
	}

	@Override
	protected void init() {
		colorControls.clear();
		numericControls.clear();
		rows.clear();
		int left = Math.max(8, width / 2 - 190);
		int right = left + 212;
		int top = height < 260 ? 24 : 34;
		int y = top;
		addTypeControl(left, y);
		y += ROW_HEIGHT;
		addShapeControl(left, y);
		y += ROW_HEIGHT;
		addColor(left, y, "Color", () -> config.color(), value -> set(config.withColor(value)),
				"Particle color.");
		y += ROW_HEIGHT;
		addToggle(left, y, "Random", () -> onOff(config.randomColor()), "Uses a fresh random color each spawn.",
				() -> set(config.withRandomColor(!config.randomColor())));
		y += ROW_HEIGHT;
		addInt(left, y, "Count", () -> config.count(), 1,
				value -> set(config.withShape(value, config.spread(), config.speed())),
				"Particles spawned per test burst.");
		y += ROW_HEIGHT;
		addFloat(left, y, "Size", () -> config.spread(), 0.05F,
				value -> set(config.withShape(config.count(), value, config.speed())),
				"Random spread for bursts and radius influence for shaped presets.");
		y += ROW_HEIGHT;
		addFloat(left, y, "Speed", () -> config.speed(), 0.01F,
				value -> set(config.withShape(config.count(), config.spread(), value)),
				"Particle velocity randomization.");
		y += ROW_HEIGHT;
		addFloat(left, y, "Range", () -> config.range(), 1.0F, value -> set(config.withRange(value)),
				"Distance ahead of the player for item test shots.");

		y = top;
		addFloat(right, y, "Alpha", () -> config.alpha(), 0.05F,
				value -> set(config.withEmber(value, config.scale(), config.life())),
				"Ember alpha. Glow and Dark Glow use their built-in alpha.");
		y += ROW_HEIGHT;
		addFloat(right, y, "Scale", () -> config.scale(), 0.05F,
				value -> set(config.withEmber(config.alpha(), value, config.life())),
				"Ember particle scale.");
		y += ROW_HEIGHT;
		addInt(right, y, "Life", () -> config.life(), 1,
				value -> set(config.withEmber(config.alpha(), config.scale(), value)),
				"Ember particle lifetime.");
		y += ROW_HEIGHT;
		if (blockScreen) {
			addToggle(right, y, "Repeat", () -> onOff(config.repeat()), "Makes the placed block spawn repeatedly.",
					() -> set(config.withRepeat(!config.repeat(), config.repeatInterval())));
			y += ROW_HEIGHT;
			addInt(right, y, "Interval", () -> config.repeatInterval(), 1,
					value -> set(config.withRepeat(config.repeat(), value)),
					"Ticks between repeated block particle bursts.");
		}

		int buttonY = Math.min(height - 22, Math.max(y + 24, top + 120));
		addRenderableWidget(Button.builder(Component.literal("Test"), button -> {
			commitInputs();
			onTest();
		}).bounds(width / 2 - 126, buttonY, 76, 18).build());
		addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
			commitInputs();
			onSave();
		}).bounds(width / 2 - 38, buttonY, 76, 18).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose()).bounds(width / 2 + 50,
				buttonY, 76, 18).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
		for (Row row : rows) {
			graphics.drawString(font, row.label(), row.x(), row.y() + 4, 0xD8D8D8, false);
			String value = row.value().get();
			if (!value.isEmpty()) {
				graphics.drawString(font, value, row.x() + 78, row.y() + 4, 0xFFFFFF, false);
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

	private void addTypeControl(int x, int y) {
		rows.add(new Row("Type", () -> config.kind().label(), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("<"), button -> {
			set(config.withKind(config.kind().previous()));
			rebuildWidgets();
		}).bounds(x + 122, y, 18, CONTROL_HEIGHT).build(), "Previous particle type."));
		addRenderableWidget(withTooltip(Button.builder(Component.literal(">"), button -> {
			set(config.withKind(config.kind().next()));
			rebuildWidgets();
		}).bounds(x + 184, y, 18, CONTROL_HEIGHT).build(), "Next particle type."));
	}

	private void addShapeControl(int x, int y) {
		rows.add(new Row("Shape", () -> config.shape().label(), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("<"), button -> {
			set(config.withParticleShape(config.shape().previous()));
			rebuildWidgets();
		}).bounds(x + 122, y, 18, CONTROL_HEIGHT).build(), "Previous particle shape."));
		addRenderableWidget(withTooltip(Button.builder(Component.literal(">"), button -> {
			set(config.withParticleShape(config.shape().next()));
			rebuildWidgets();
		}).bounds(x + 184, y, 18, CONTROL_HEIGHT).build(), "Next particle shape."));
	}

	private void addColor(int x, int y, String label, Supplier<Integer> getter, IntSetter setter, String description) {
		rows.add(new Row(label, () -> "", x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("<"), button -> {
			int value = LightningTestColors.previousPreset(getter.get());
			setter.set(value);
			rebuildWidgets();
		}).bounds(x + 122, y, 18, CONTROL_HEIGHT).build(), description));
		EditBox input = new EditBox(font, x + 143, y + 1, 38, CONTROL_HEIGHT - 2, Component.literal(label));
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
			int value = LightningTestColors.nextPreset(getter.get());
			setter.set(value);
			rebuildWidgets();
		}).bounds(x + 184, y, 18, CONTROL_HEIGHT).build(), description));
	}

	private void addFloat(int x, int y, String label, Supplier<Float> getter, float step, FloatSetter setter,
			String description) {
		rows.add(new Row(label, () -> "", x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step);
			rebuildWidgets();
		}).bounds(x + 122, y, 18, CONTROL_HEIGHT).build(), description));
		EditBox input = numericInput(x + 143, y, label, String.format("%.2f", getter.get()), description);
		input.setResponder(value -> parseFloat(value, setter, input));
		numericControls.add(new NumericControl(input, () -> parseFloat(input.getValue(), setter, input)));
		addRenderableWidget(input);
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step);
			rebuildWidgets();
		}).bounds(x + 184, y, 18, CONTROL_HEIGHT).build(), description));
	}

	private void addInt(int x, int y, String label, Supplier<Integer> getter, int step, IntSetter setter,
			String description) {
		rows.add(new Row(label, () -> "", x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step);
			rebuildWidgets();
		}).bounds(x + 122, y, 18, CONTROL_HEIGHT).build(), description));
		EditBox input = numericInput(x + 143, y, label, Integer.toString(getter.get()), description);
		input.setResponder(value -> parseInt(value, setter, input));
		numericControls.add(new NumericControl(input, () -> parseInt(input.getValue(), setter, input)));
		addRenderableWidget(input);
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step);
			rebuildWidgets();
		}).bounds(x + 184, y, 18, CONTROL_HEIGHT).build(), description));
	}

	private void addToggle(int x, int y, String label, Supplier<String> getter, String description, Runnable toggle) {
		rows.add(new Row(label, getter, x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("ON/OFF"), button -> {
			toggle.run();
			rebuildWidgets();
		}).bounds(x + 122, y, 80, CONTROL_HEIGHT).build(), description));
	}

	private EditBox numericInput(int x, int y, String label, String value, String description) {
		EditBox input = new EditBox(font, x, y + 1, 38, CONTROL_HEIGHT - 2, Component.literal(label));
		input.setMaxLength(8);
		input.setTooltip(Tooltip.create(Component.literal(description)));
		input.setValue(value);
		return input;
	}

	private void commitInputs() {
		for (ColorControl control : colorControls) {
			OptionalInt parsed = LightningTestColors.parseColor(control.input().getValue());
			parsed.ifPresent(value -> control.setter().set(value));
		}
		for (NumericControl control : numericControls) {
			control.commit().run();
		}
	}

	private void parseFloat(String value, FloatSetter setter, EditBox input) {
		try {
			setter.set(Float.parseFloat(value));
			input.setTextColor(0xE0E0E0);
		} catch (NumberFormatException ex) {
			input.setTextColor(0xFF6666);
		}
	}

	private void parseInt(String value, IntSetter setter, EditBox input) {
		try {
			setter.set(Integer.parseInt(value));
			input.setTextColor(0xE0E0E0);
		} catch (NumberFormatException ex) {
			input.setTextColor(0xFF6666);
		}
	}

	private String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

	private Button withTooltip(Button button, String description) {
		button.setTooltip(Tooltip.create(Component.literal(description)));
		return button;
	}

	private void set(GenericParticleTestConfig config) {
		this.config = config.clamped();
	}

	protected abstract void onSave();

	protected abstract void onTest();
}
