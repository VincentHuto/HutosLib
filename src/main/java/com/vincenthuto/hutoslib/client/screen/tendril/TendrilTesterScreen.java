package com.vincenthuto.hutoslib.client.screen.tendril;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.lightning.LightningTestColors;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

abstract class TendrilTesterScreen extends Screen {
	private static final int ACTION_BUTTON_BOTTOM_MARGIN = 4;
	private static final int ACTION_BUTTON_GAP = 10;
	private static final int ACTION_BUTTON_HEIGHT = 18;

	enum Section {
		TARGET("Target"), LIFECYCLE("Age"), COLORS("Color"), SHAPE("Shape"), BRANCHING("Branch"), WRITHE(
				"Writhe"), SURFACE("Surface"), SEED("Seed");

		private final String label;

		Section(String label) {
			this.label = label;
		}
	}

	private record ColorControl(EditBox input, IntSetter setter) {
	}

	private record Header(String label, int x, int y) {
	}

	private record Row(String label, Supplier<String> value, int x, int y) {
	}

	private static class CompactLayout {
		private final int startX;
		private final int top;
		private final int columnWidth;
		private final int columnGap;
		private final int rowHeight;
		private final int controlHeight;
		private final int[] nextY;

		CompactLayout(int width, int height) {
			this.columnWidth = 208;
			this.columnGap = 10;
			this.rowHeight = compactRowHeight(height);
			this.controlHeight = Math.max(13, rowHeight - 2);
			this.top = compactTop(height);
			this.nextY = new int[compactColumnCount(width)];
			int totalWidth = nextY.length * columnWidth + (nextY.length - 1) * columnGap;
			this.startX = Math.max(6, (width - totalWidth) / 2);
			for (int i = 0; i < nextY.length; i++) {
				nextY[i] = top;
			}
		}

		int columns() {
			return nextY.length;
		}

		int controlHeight() {
			return controlHeight;
		}

		int maxY() {
			int max = top;
			for (int y : nextY) {
				max = Math.max(max, y);
			}
			return max;
		}

		int nextControlY(int column) {
			int y = nextY[column];
			nextY[column] += rowHeight;
			return y;
		}

		int nextSectionY(int column) {
			int y = nextY[column];
			nextY[column] += rowHeight;
			return y;
		}

		int shortestColumn() {
			int shortest = 0;
			for (int i = 1; i < nextY.length; i++) {
				if (nextY[i] < nextY[shortest]) {
					shortest = i;
				}
			}
			return shortest;
		}

		int x(int column) {
			return startX + column * (columnWidth + columnGap);
		}
	}

	protected TendrilEffectConfig config;
	private final boolean blockScreen;
	private final List<ColorControl> colorControls = new ArrayList<>();
	private final EnumSet<Section> collapsedSections = defaultCollapsedSections();
	private final List<Header> headers = new ArrayList<>();
	private final List<Row> rows = new ArrayList<>();

	protected TendrilTesterScreen(Component title, TendrilEffectConfig config, boolean blockScreen) {
		super(title);
		this.config = config.clamped();
		this.blockScreen = blockScreen;
	}

	static int compactColumnCount(int width) {
		if (width >= 660) {
			return 3;
		}
		return width >= 430 ? 2 : 1;
	}

	static int compactRowHeight(int height) {
		return height < 280 ? 14 : 16;
	}

	static int compactTop(int height) {
		return height < 280 ? 18 : 22;
	}

	static int actionButtonY(int height, int contentMaxY) {
		return Math.min(height - ACTION_BUTTON_HEIGHT - ACTION_BUTTON_BOTTOM_MARGIN,
				contentMaxY + ACTION_BUTTON_GAP);
	}

	static int visibleSectionRowCount(Set<Section> collapsedSections, boolean blockScreen) {
		int rows = 0;
		for (Section section : sections()) {
			rows++;
			if (!collapsedSections.contains(section)) {
				rows += sectionControlRowCount(section, blockScreen);
			}
		}
		return rows;
	}

	private static int sectionControlRowCount(Section section, boolean blockScreen) {
		return switch (section) {
		case TARGET -> blockScreen ? 6 : 4;
		case LIFECYCLE, SHAPE, BRANCHING, WRITHE -> 4;
		case SEED -> 2;
		case COLORS, SURFACE -> 3;
		};
	}

	static List<Section> sections() {
		return List.of(Section.TARGET, Section.LIFECYCLE, Section.COLORS, Section.SHAPE, Section.BRANCHING,
				Section.WRITHE, Section.SURFACE, Section.SEED);
	}

	static EnumSet<Section> defaultCollapsedSections() {
		return EnumSet.allOf(Section.class);
	}

	@Override
	protected void init() {
		colorControls.clear();
		headers.clear();
		rows.clear();
		CompactLayout layout = new CompactLayout(width, height);
		addTargetControls(layout, layout.shortestColumn());
		addLifecycleControls(layout, layout.shortestColumn());
		addColorsControls(layout, layout.shortestColumn());
		addShapeControls(layout, layout.shortestColumn());
		addBranchingControls(layout, layout.shortestColumn());
		addWritheControls(layout, layout.shortestColumn());
		addSurfaceControls(layout, layout.shortestColumn());
		addSeedControls(layout, layout.shortestColumn());

		int buttonY = actionButtonY(height, layout.maxY());
		addRenderableWidget(Button.builder(Component.literal("Test"), button -> {
			commitColorInputs();
			onTest();
		}).bounds(width / 2 - 126, buttonY, 76, ACTION_BUTTON_HEIGHT).build());
		addRenderableWidget(Button.builder(Component.literal("Save"), button -> {
			commitColorInputs();
			onSave();
		}).bounds(width / 2 - 38, buttonY, 76, ACTION_BUTTON_HEIGHT).build());
		addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose()).bounds(width / 2 + 50,
				buttonY, 76, ACTION_BUTTON_HEIGHT).build());
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
		graphics.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
		for (Header header : headers) {
			graphics.drawString(font, header.label(), header.x(), header.y() + 3, 0xFF7777, false);
		}
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

	private interface LongSetter {
		void set(long value);
	}

	private void addBranchingControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.BRANCHING)) {
			return;
		}
		addInt(layout, column, "Branches", () -> config.branchCount(), 1, value -> setBranchCount(value),
				"Total generated branch budget.");
		addInt(layout, column, "Depth", () -> config.branchDepth(), 1, value -> setBranchDepth(value),
				"Maximum branch recursion depth.");
		addFloat(layout, column, "Length", () -> config.branchLength(), 0.05F, value -> setBranchLength(value),
				"Branch length as a fraction of the main tendril.");
		addFloat(layout, column, "Spread", () -> config.branchSpread(), 0.1F, value -> setBranchSpread(value),
				"How far branches diverge from their parent.");
	}

	private void addColorsControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.COLORS)) {
			return;
		}
		addColor(layout, column, "Outer", () -> config.glowColor(), value -> set(config.withColors(
				config.coreColor(), value)), "Wider outer tendril sheath color.");
		addColor(layout, column, "Inner", () -> config.coreColor(), value -> set(config.withColors(value,
				config.glowColor())), "Darker inner tendril core color.");
		addToggle(layout, column, "Blend", () -> onOff(config.blendColors()), () -> onOff(config.blendColors()),
				"ON lets the outer sheath wash over the core; OFF redraws the core last for high-contrast colors.",
				() -> set(config.withBlendColors(!config.blendColors())));
	}

	private void addLifecycleControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.LIFECYCLE)) {
			return;
		}
		addInt(layout, column, "Grow", () -> config.growTicks(), 1, value -> setGrowTicks(value),
				"Ticks spent writing the tendril into the world.");
		addInt(layout, column, "Hold", () -> config.holdTicks(), 1, value -> setHoldTicks(value),
				"Ticks the full tendril stays visible.");
		addInt(layout, column, "Fade", () -> config.fadeTicks(), 1, value -> setFadeTicks(value),
				"Ticks spent thinning and fading out.");
		addReadout(layout, column, "Total", () -> Integer.toString(config.totalLifetime()));
	}

	private void addSeedControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.SEED)) {
			return;
		}
		addToggle(layout, column, "Fixed Seed", () -> onOff(config.fixedSeed()), () -> onOff(config.fixedSeed()),
				"Keeps the same random tendril structure while testing.",
				() -> set(config.withFixedSeed(!config.fixedSeed(), config.seed())));
		addLong(layout, column, "Seed", () -> config.seed(), 1L, value -> set(config.withFixedSeed(
				config.fixedSeed(), value)), "Random seed used when Fixed Seed is ON.");
	}

	private void addShapeControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.SHAPE)) {
			return;
		}
		addInt(layout, column, "Segments", () -> config.segments(), 1, value -> setSegments(value),
				"Ring count detail along each strand.");
		addInt(layout, column, "Strands", () -> config.strandCount(), 1, value -> setStrandCount(value),
				"Parallel organic strands in the same effect.");
		addFloat(layout, column, "Base Width", () -> config.baseWidth(), 0.01F, value -> setBaseWidth(value),
				"Radius of the tendril base.");
		addFloat(layout, column, "Tip Scale", () -> config.tipScale(), 0.01F, value -> setTipScale(value),
				"Tip radius as a fraction of the base.");
	}

	private void addSurfaceControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.SURFACE)) {
			return;
		}
		addToggle(layout, column, "Mode", () -> config.mode().name(), () -> config.mode().name(),
				"Freeform uses open 3D curves; Surface tries to snap samples onto nearby block faces.",
				() -> set(config.withMode(config.mode() == TendrilEffectConfig.Mode.FREEFORM
						? TendrilEffectConfig.Mode.SURFACE : TendrilEffectConfig.Mode.FREEFORM)));
		addFloat(layout, column, "Snap Dist", () -> config.surfaceSnapDistance(), 0.25F,
				value -> setSurfaceDistance(value), "Maximum surface snap search distance.");
		addFloat(layout, column, "Lift", () -> config.surfaceLift(), 0.02F, value -> setSurfaceLift(value),
				"Offset away from the snapped block face.");
	}

	private void addTargetControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.TARGET)) {
			return;
		}
		addFloat(layout, column, "Range", () -> config.range(), 1.0F, value -> set(config.withRange(value)),
				"Distance in blocks for player-look test shots.");
		addFloat(layout, column, "Offset X", () -> config.targetOffsetX(), 1.0F,
				value -> setOffset(value, config.targetOffsetY(), config.targetOffsetZ()),
				"Block tester target offset on the X axis.");
		addFloat(layout, column, "Offset Y", () -> config.targetOffsetY(), 1.0F,
				value -> setOffset(config.targetOffsetX(), value, config.targetOffsetZ()),
				"Block tester target offset on the Y axis.");
		addFloat(layout, column, "Offset Z", () -> config.targetOffsetZ(), 1.0F,
				value -> setOffset(config.targetOffsetX(), config.targetOffsetY(), value),
				"Block tester target offset on the Z axis.");
		if (blockScreen) {
			addToggle(layout, column, "Repeat", () -> onOff(config.repeat()), () -> onOff(config.repeat()),
					"Makes the placed block spawn tendrils repeatedly.",
					() -> set(config.withRepeat(!config.repeat(), config.repeatInterval())));
			addInt(layout, column, "Interval", () -> config.repeatInterval(), 5, value -> set(config.withRepeat(
					config.repeat(), value)), "Ticks between repeated block tendril spawns.");
		}
	}

	private void addWritheControls(CompactLayout layout, int column) {
		if (!addHeader(layout, column, Section.WRITHE)) {
			return;
		}
		addFloat(layout, column, "Amplitude", () -> config.writheAmplitude(), 0.02F,
				value -> setWritheAmplitude(value),
				"How far the tendril writhes away from its base path.");
		addFloat(layout, column, "Frequency", () -> config.writheFrequency(), 0.01F,
				value -> setWritheFrequency(value), "How quickly the tendril writhes.");
		addFloat(layout, column, "Curl", () -> config.curl(), 0.1F, value -> setCurl(value),
				"Static side curl of the main path.");
		addFloat(layout, column, "Sag", () -> config.sag(), 0.1F, value -> setSag(value),
				"Vertical droop along the tendril.");
	}

	private void addColor(CompactLayout layout, int column, String label, Supplier<Integer> getter, IntSetter setter,
			String description) {
		int x = layout.x(column);
		int y = layout.nextControlY(column);
		rows.add(new Row(label, () -> "", x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("<"), button -> {
			int value = getter.get();
			for (int i = 0; i < currentStepMultiplier(); i++) {
				value = LightningTestColors.previousPreset(value);
			}
			setter.set(value);
			rebuildWidgets();
		}).bounds(x + 122, y, 18, layout.controlHeight()).build(), description));
		EditBox input = new EditBox(font, x + 143, y + 1, 38, layout.controlHeight() - 2, Component.literal(label));
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
		}).bounds(x + 184, y, 18, layout.controlHeight()).build(), description));
	}

	private void addFloat(CompactLayout layout, int column, String label, Supplier<Float> getter, float step,
			FloatSetter setter, String description) {
		int x = layout.x(column);
		int y = layout.nextControlY(column);
		rows.add(new Row(label, () -> String.format("%.2f", getter.get()), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 122, y, 18, layout.controlHeight()).build(), description));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 184, y, 18, layout.controlHeight()).build(), description));
	}

	private boolean addHeader(CompactLayout layout, int column, Section section) {
		int x = layout.x(column);
		int y = layout.nextSectionY(column);
		boolean collapsed = collapsedSections.contains(section);
		headers.add(new Header(section.label, x, y));
		addRenderableWidget(Button.builder(Component.literal(collapsed ? "+" : "-"), button -> {
			commitColorInputs();
			if (collapsedSections.contains(section)) {
				collapsedSections.remove(section);
			} else {
				collapsedSections.add(section);
			}
			rebuildWidgets();
		}).bounds(x + 184, y, 18, layout.controlHeight()).tooltip(Tooltip.create(Component.literal(
				(collapsed ? "Expand " : "Collapse ") + section.label + " controls."))).build());
		return !collapsed;
	}

	private void addInt(CompactLayout layout, int column, String label, Supplier<Integer> getter, int step,
			IntSetter setter, String description) {
		int x = layout.x(column);
		int y = layout.nextControlY(column);
		rows.add(new Row(label, () -> Integer.toString(getter.get()), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 122, y, 18, layout.controlHeight()).build(), description));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 184, y, 18, layout.controlHeight()).build(), description));
	}

	private void addLong(CompactLayout layout, int column, String label, Supplier<Long> getter, long step,
			LongSetter setter, String description) {
		int x = layout.x(column);
		int y = layout.nextControlY(column);
		rows.add(new Row(label, () -> Long.toString(getter.get()), x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("-"), button -> {
			setter.set(getter.get() - step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 122, y, 18, layout.controlHeight()).build(), description));
		addRenderableWidget(withTooltip(Button.builder(Component.literal("+"), button -> {
			setter.set(getter.get() + step * currentStepMultiplier());
			rebuildWidgets();
		}).bounds(x + 184, y, 18, layout.controlHeight()).build(), description));
	}

	private void addReadout(CompactLayout layout, int column, String label, Supplier<String> value) {
		rows.add(new Row(label, value, layout.x(column), layout.nextControlY(column)));
	}

	private void addToggle(CompactLayout layout, int column, String label, Supplier<String> getter,
			Supplier<String> buttonLabel, String description, Runnable action) {
		int x = layout.x(column);
		int y = layout.nextControlY(column);
		rows.add(new Row(label, getter, x, y));
		addRenderableWidget(withTooltip(Button.builder(Component.literal(buttonLabel.get()), button -> {
			action.run();
			rebuildWidgets();
		}).bounds(x + 122, y, 60, layout.controlHeight()).build(), description));
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

	private int currentStepMultiplier() {
		return stepMultiplier(hasShiftDown());
	}

	private static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

	private void set(TendrilEffectConfig config) {
		this.config = config.clamped();
	}

	private void setBaseWidth(float value) {
		set(config.withShape(config.segments(), config.strandCount(), value, config.tipScale()));
	}

	private void setBranchCount(int value) {
		set(config.withBranching(value, config.branchDepth(), config.branchLength(), config.branchSpread()));
	}

	private void setBranchDepth(int value) {
		set(config.withBranching(config.branchCount(), value, config.branchLength(), config.branchSpread()));
	}

	private void setBranchLength(float value) {
		set(config.withBranching(config.branchCount(), config.branchDepth(), value, config.branchSpread()));
	}

	private void setBranchSpread(float value) {
		set(config.withBranching(config.branchCount(), config.branchDepth(), config.branchLength(), value));
	}

	private void setCurl(float value) {
		set(config.withWrithe(config.writheAmplitude(), config.writheFrequency(), value, config.sag()));
	}

	private void setFadeTicks(int value) {
		set(config.withLifecycle(config.growTicks(), config.holdTicks(), value));
	}

	private void setGrowTicks(int value) {
		set(config.withLifecycle(value, config.holdTicks(), config.fadeTicks()));
	}

	private void setHoldTicks(int value) {
		set(config.withLifecycle(config.growTicks(), value, config.fadeTicks()));
	}

	private void setOffset(float x, float y, float z) {
		set(config.withTargetOffset(x, y, z));
	}

	private void setSag(float value) {
		set(config.withWrithe(config.writheAmplitude(), config.writheFrequency(), config.curl(), value));
	}

	private void setSegments(int value) {
		set(config.withShape(value, config.strandCount(), config.baseWidth(), config.tipScale()));
	}

	private void setStrandCount(int value) {
		set(config.withShape(config.segments(), value, config.baseWidth(), config.tipScale()));
	}

	private void setSurfaceDistance(float value) {
		set(config.withSurface(value, config.surfaceLift()));
	}

	private void setSurfaceLift(float value) {
		set(config.withSurface(config.surfaceSnapDistance(), value));
	}

	private void setTipScale(float value) {
		set(config.withShape(config.segments(), config.strandCount(), config.baseWidth(), value));
	}

	private void setWritheAmplitude(float value) {
		set(config.withWrithe(value, config.writheFrequency(), config.curl(), config.sag()));
	}

	private void setWritheFrequency(float value) {
		set(config.withWrithe(config.writheAmplitude(), value, config.curl(), config.sag()));
	}

	static int stepMultiplier(boolean shiftDown) {
		return shiftDown ? 5 : 1;
	}

	private Button withTooltip(Button button, String description) {
		button.setTooltip(Tooltip.create(Component.literal(description + " Hold Shift for 5x steps.")));
		return button;
	}

	protected abstract void onSave();

	protected abstract void onTest();
}
