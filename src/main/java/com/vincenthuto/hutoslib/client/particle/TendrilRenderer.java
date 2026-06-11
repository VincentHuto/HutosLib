package com.vincenthuto.hutoslib.client.particle;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.vincenthuto.hutoslib.client.HLRenderTypeInit;
import com.vincenthuto.hutoslib.client.particle.data.TendrilEffectData;
import com.vincenthuto.hutoslib.client.particle.data.TendrilGeometry;
import com.vincenthuto.hutoslib.common.tendril.TendrilAnchor;
import com.vincenthuto.hutoslib.common.tendril.TendrilAnchorState;
import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class TendrilRenderer {
	enum GeometryPass {
		CORE, GLOW
	}

	@FunctionalInterface
	interface GeometryPassConsumer {
		void accept(GeometryPass pass, List<Vec3> vertices);
	}

	private record ResolvedAnchors(Vec3 start, Vec3 end) {
	}

	private record Rgba(float r, float g, float b, float a) {
	}

	private static class TendrilInstance {
		private final TendrilEffectData data;
		private final TendrilAnchorState startState;
		private final TendrilAnchorState endState;
		private final Timestamp createdTimestamp;

		TendrilInstance(TendrilEffectData data, Timestamp createdTimestamp) {
			this.data = data;
			this.startState = new TendrilAnchorState(data.start());
			this.endState = new TendrilAnchorState(data.end());
			this.createdTimestamp = createdTimestamp;
		}

		Optional<ResolvedAnchors> resolve(TendrilAnchor.EntityResolver resolver) {
			Optional<Vec3> start = startState.resolve(resolver);
			Optional<Vec3> end = endState.resolve(resolver);
			if (start.isEmpty() || end.isEmpty()) {
				return Optional.empty();
			}
			return Optional.of(new ResolvedAnchors(start.get(), end.get()));
		}

		boolean render(ClientLevel level, Matrix4f matrix, MultiBufferSource buffers, Timestamp timestamp) {
			Optional<ResolvedAnchors> anchors = resolve(TendrilAnchor.forLevel(level));
			if (anchors.isEmpty()) {
				return false;
			}
			TendrilEffectConfig config = data.config().clamped();
			float age = timestamp.subtract(createdTimestamp).value();
			float growProgress = Math.min(1.0F, age / config.growTicks());
			float alphaScale = 1.0F;
			if (age > config.growTicks() + config.holdTicks()) {
				alphaScale = 1.0F - Math.min(1.0F,
						(age - config.growTicks() - config.holdTicks()) / config.fadeTicks());
			}
			if (alphaScale <= 0.0F) {
				return true;
			}

			TendrilGeometry geometry = TendrilGeometry.generate(anchors.get().start(), anchors.get().end(), config,
					data.seed(), age, surfaceResolver(level, config));
			renderGeometry(matrix, buffers, geometry, config, growProgress, alphaScale);
			return true;
		}

		boolean shouldRemove(Timestamp timestamp) {
			return timestamp.isPassed(createdTimestamp, data.config().totalLifetime());
		}
	}

	private static class Timestamp {
		private static final Timestamp ZERO = new Timestamp(0, 0);
		private final long ticks;
		private final float partial;

		Timestamp(long ticks, float partial) {
			this.ticks = ticks;
			this.partial = partial;
		}

		boolean isPassed(Timestamp prev, double duration) {
			long ticksPassed = ticks - prev.ticks;
			if (ticksPassed > duration) {
				return true;
			}
			duration -= ticksPassed;
			if (duration >= 1) {
				return false;
			}
			return (partial - prev.partial) >= duration;
		}

		Timestamp subtract(Timestamp other) {
			long newTicks = ticks - other.ticks;
			float newPartial = partial - other.partial;
			if (newPartial < 0) {
				newPartial += 1;
				newTicks -= 1;
			}
			return new Timestamp(newTicks, newPartial);
		}

		float value() {
			return ticks + partial;
		}
	}

	public static final TendrilRenderer INSTANCE = new TendrilRenderer();

	private static final int MAX_ACTIVE_TENDRILS = 256;
	private static final double MAX_RENDER_DISTANCE_SQR = 128 * 128;

	public static void onWorldRenderLast(float partialTicks, PoseStack poseStack) {
		poseStack.pushPose();
		Vec3 camera = TendrilRenderer.INSTANCE.minecraft.gameRenderer.getMainCamera().getPosition();
		poseStack.translate(-camera.x, -camera.y, -camera.z);
		MultiBufferSource.BufferSource buffers = TendrilRenderer.INSTANCE.minecraft.renderBuffers().bufferSource();
		TendrilRenderer.INSTANCE.render(partialTicks, poseStack, buffers);
		buffers.endBatch(HLRenderTypeInit.TENDRIL_CORE);
		buffers.endBatch(HLRenderTypeInit.TENDRIL_GLOW);
		poseStack.popPose();
	}

	private final Minecraft minecraft = Minecraft.getInstance();
	private final List<TendrilInstance> tendrils = new LinkedList<>();

	public void add(TendrilEffectData data, float partialTicks) {
		if (minecraft.level == null) {
			return;
		}
		TendrilInstance instance = new TendrilInstance(data, currentTimestamp(partialTicks));
		Optional<ResolvedAnchors> anchors = instance.resolve(TendrilAnchor.forLevel(minecraft.level));
		if (anchors.isEmpty() || !isCloseEnoughToRender(anchors.get())) {
			return;
		}
		synchronized (tendrils) {
			while (tendrils.size() >= MAX_ACTIVE_TENDRILS) {
				tendrils.remove(0);
			}
			tendrils.add(instance);
		}
	}

	public void clear() {
		synchronized (tendrils) {
			tendrils.clear();
		}
	}

	public void render(float partialTicks, PoseStack poseStack, MultiBufferSource buffers) {
		if (minecraft.level == null) {
			return;
		}
		Timestamp timestamp = currentTimestamp(partialTicks);
		Matrix4f matrix = poseStack.last().pose();
		synchronized (tendrils) {
			for (Iterator<TendrilInstance> iterator = tendrils.iterator(); iterator.hasNext();) {
				TendrilInstance tendril = iterator.next();
				if (tendril.shouldRemove(timestamp) || !tendril.render(minecraft.level, matrix, buffers, timestamp)) {
					iterator.remove();
				}
			}
		}
	}

	private Timestamp currentTimestamp(float partialTicks) {
		return minecraft.level == null ? Timestamp.ZERO : new Timestamp(minecraft.level.getGameTime(), partialTicks);
	}

	private boolean isCloseEnoughToRender(ResolvedAnchors anchors) {
		Vec3 camera = minecraft.gameRenderer.getMainCamera().getPosition();
		return anchors.start().distanceToSqr(camera) <= MAX_RENDER_DISTANCE_SQR
				|| anchors.end().distanceToSqr(camera) <= MAX_RENDER_DISTANCE_SQR;
	}

	private static Rgba unpack(int color, float alphaScale) {
		float alpha = (color >>> 24) & 0xFF;
		alpha = alpha == 0 ? 1.0F : alpha / 255.0F;
		return new Rgba(((color >>> 16) & 0xFF) / 255.0F, ((color >>> 8) & 0xFF) / 255.0F,
				(color & 0xFF) / 255.0F, alpha * alphaScale);
	}

	static void renderGeometry(Matrix4f matrix, MultiBufferSource buffers, TendrilGeometry geometry,
			TendrilEffectConfig config, float growProgress, float alphaScale) {
		Rgba core = unpack(config.coreColor(), alphaScale);
		Rgba glow = unpack(config.glowColor(), alphaScale);
		VertexConsumer[] coreBuffer = new VertexConsumer[1];
		VertexConsumer[] glowBuffer = new VertexConsumer[1];
		emitGeometryPasses(geometry, config, growProgress, alphaScale, (pass, vertices) -> {
			if (vertices.isEmpty()) {
				return;
			}
			if (pass == GeometryPass.CORE) {
				if (coreBuffer[0] == null) {
					coreBuffer[0] = buffers.getBuffer(HLRenderTypeInit.TENDRIL_CORE);
				}
				renderQuads(matrix, coreBuffer[0], vertices, core);
				return;
			}
			if (glowBuffer[0] == null) {
				glowBuffer[0] = buffers.getBuffer(HLRenderTypeInit.TENDRIL_GLOW);
			}
			renderQuads(matrix, glowBuffer[0], vertices, glow);
		});
	}

	static void emitGeometryPasses(TendrilGeometry geometry, TendrilEffectConfig config, float growProgress,
			float alphaScale, GeometryPassConsumer consumer) {
		for (TendrilGeometry.Strand strand : geometry.strands()) {
			TendrilGeometry.TubeQuads coreQuads = TendrilGeometry.createTubeQuads(strand, growProgress, alphaScale);
			consumer.accept(GeometryPass.CORE, coreQuads.vertices());
		}

		if (unpack(config.glowColor(), alphaScale).a() <= 0.0F) {
			return;
		}
		for (TendrilGeometry.Strand strand : geometry.strands()) {
			TendrilGeometry.TubeQuads glowQuads =
					TendrilGeometry.createTubeQuads(strand, growProgress, alphaScale * 1.35F);
			consumer.accept(GeometryPass.GLOW, glowQuads.vertices());
		}
	}

	private static void renderQuads(Matrix4f matrix, VertexConsumer buffer, List<Vec3> vertices, Rgba color) {
		for (Vec3 vertex : vertices) {
			buffer.addVertex(matrix, (float) vertex.x, (float) vertex.y, (float) vertex.z).setColor(color.r(),
					color.g(), color.b(), color.a());
		}
	}

	private static TendrilGeometry.SurfaceResolver surfaceResolver(ClientLevel level, TendrilEffectConfig config) {
		if (config.mode() != TendrilEffectConfig.Mode.SURFACE || config.surfaceSnapDistance() <= 0.0F) {
			return TendrilGeometry.SurfaceResolver.NONE;
		}
		return (point, tangent, clamped) -> {
			Vec3 best = null;
			double bestDistance = Double.MAX_VALUE;
			for (Direction direction : Direction.values()) {
				Vec3 normal = Vec3.atLowerCornerOf(direction.getNormal());
				BlockHitResult hit = level.clip(new ClipContext(point, point.add(normal.scale(
						clamped.surfaceSnapDistance())), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE,
						Minecraft.getInstance().cameraEntity));
				if (hit.getType() != HitResult.Type.BLOCK) {
					continue;
				}
				Vec3 candidate = hit.getLocation()
						.add(Vec3.atLowerCornerOf(hit.getDirection().getNormal()).scale(clamped.surfaceLift()));
				double distance = candidate.distanceToSqr(point);
				if (distance < bestDistance) {
					bestDistance = distance;
					best = candidate;
				}
			}
			return Optional.ofNullable(best);
		};
	}
}
