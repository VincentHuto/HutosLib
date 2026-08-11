package com.vincenthuto.hutoslib.client.particle.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import com.vincenthuto.hutoslib.common.tendril.TendrilEffectConfig;

import net.minecraft.world.phys.Vec3;

public record TendrilGeometry(List<TendrilGeometry.Strand> strands) {

	@FunctionalInterface
	public interface SurfaceResolver {
		SurfaceResolver NONE = (point, tangent, config) -> Optional.empty();

		Optional<Vec3> snap(Vec3 point, Vec3 tangent, TendrilEffectConfig config);
	}

	public record Ring(Vec3 center, float width, Vec3 right, Vec3 up, float progress) {
		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof Ring other)) {
				return false;
			}
			return same(center, other.center) && Float.compare(width, other.width) == 0 && same(right, other.right)
					&& same(up, other.up) && Float.compare(progress, other.progress) == 0;
		}

		@Override
		public int hashCode() {
			return Objects.hash(center.x, center.y, center.z, width, right.x, right.y, right.z, up.x, up.y, up.z,
					progress);
		}
	}

	public record Strand(List<Ring> rings, boolean branch, int depth) {
	}

	public record TubeQuads(List<Vec3> vertices) {
	}

	private record Basis(Vec3 direction, Vec3 right, Vec3 up) {
	}

	public static TendrilGeometry generate(Vec3 start, Vec3 end, TendrilEffectConfig config, long seed, float time) {
		return generate(start, end, config, seed, time, SurfaceResolver.NONE, Vec3.ZERO);
	}

	public static TendrilGeometry generate(Vec3 start, Vec3 end, TendrilEffectConfig config, long seed, float time,
			SurfaceResolver surfaceResolver) {
		return generate(start, end, config, seed, time, surfaceResolver, Vec3.ZERO);
	}

	public static TendrilGeometry generate(Vec3 start, Vec3 end, TendrilEffectConfig config, long seed, float time,
			SurfaceResolver surfaceResolver, Vec3 preferredUp) {
		TendrilEffectConfig clamped = config.clamped();
		Random random = new Random(seed);
		List<Strand> strands = new ArrayList<>();
		List<Strand> branchParents = new ArrayList<>();
		Basis basis = basis(end.subtract(start), preferredUp);
		int strandCount = clamped.strandCount();
		for (int i = 0; i < strandCount; i++) {
			float offsetScale = strandCount == 1 ? 0.0F : clamped.baseWidth() * 0.7F;
			double angle = Math.PI * 2.0D * i / strandCount;
			Vec3 offset = basis.right().scale(Math.cos(angle) * offsetScale)
					.add(basis.up().scale(Math.sin(angle) * offsetScale));
			float strandCurl = clamped.curl() * (0.75F + random.nextFloat() * 0.5F);
			float phase = random.nextFloat() * (float) (Math.PI * 2.0D);
			Strand strand = createStrand(start.add(offset), end.add(offset), clamped, seed + i * 31L, time, 0,
				false, clamped.baseWidth(), strandCurl, phase, clamped.segments(), surfaceResolver, preferredUp);
			strands.add(strand);
			branchParents.add(strand);
		}

		if (clamped.branchDepth() > 0 && clamped.branchCount() > 0) {
			for (int i = 0; i < clamped.branchCount(); i++) {
				Strand parent = chooseBranchParent(branchParents, random, clamped.branchDepth());
				if (parent == null || parent.rings().size() < 3) {
					break;
				}
				int startIndex = 1 + random.nextInt(Math.max(1, parent.rings().size() - 2));
				Ring fork = parent.rings().get(startIndex);
				int depth = Math.min(clamped.branchDepth(), parent.depth() + 1);
				Vec3 parentDir = tangentAt(parent, startIndex);
				Vec3 lateral = fork.right().scale((random.nextBoolean() ? 1.0D : -1.0D)
						* (0.35D + random.nextDouble() * clamped.branchSpread()));
				Vec3 lift = fork.up().scale((random.nextDouble() - 0.25D) * clamped.branchSpread());
				Vec3 branchDir = safeNormalize(parentDir.scale(0.55D).add(lateral).add(lift), parentDir);
				float distance = (float) start.distanceTo(end);
				float length = Math.max(clamped.baseWidth() * 2.0F,
						distance * clamped.branchLength() * (0.35F + random.nextFloat() * 0.45F));
				float branchWidth = Math.max(0.01F, fork.width() * 0.65F);
				Strand branch = createStrand(fork.center(), fork.center().add(branchDir.scale(length)), clamped,
						seed + 1000L + i * 97L, time, depth, true, branchWidth,
						clamped.curl() * (random.nextFloat() - 0.5F), random.nextFloat() * 6.28318F,
						Math.max(2, clamped.segments() / 3), surfaceResolver, preferredUp);
				strands.add(branch);
				if (depth < clamped.branchDepth()) {
					branchParents.add(branch);
				}
			}
		}

		return new TendrilGeometry(List.copyOf(strands));
	}

	public static TubeQuads createTubeQuads(Strand strand, float widthScale) {
		return createTubeQuads(strand, 1.0F, widthScale);
	}

	public static TubeQuads createTubeQuads(Strand strand, float visibleProgress, float widthScale) {
		List<Ring> rings = strand.rings();
		List<Vec3> vertices = new ArrayList<>();
		if (rings.size() < 2 || visibleProgress <= 0.0F || widthScale <= 0.0F) {
			return new TubeQuads(vertices);
		}
		int segments = rings.size() - 1;
		float scaled = Math.min(1.0F, visibleProgress) * segments;
		int fullSegments = Math.min(segments, (int) Math.floor(scaled));
		float partial = scaled - fullSegments;
		for (int i = 0; i < fullSegments; i++) {
			addTubeSegment(vertices, rings.get(i), rings.get(i + 1), widthScale);
		}
		if (fullSegments < segments && partial > 1.0E-4F) {
			addTubeSegment(vertices, rings.get(fullSegments), interpolate(rings.get(fullSegments),
					rings.get(fullSegments + 1), partial), widthScale);
		}
		return new TubeQuads(List.copyOf(vertices));
	}

	private static void addTubeSegment(List<Vec3> vertices, Ring from, Ring to, float widthScale) {
		int tubeFaces = 4;
		for (int face = 0; face < tubeFaces; face++) {
			double angle0 = Math.PI * 2.0D * face / tubeFaces;
			double angle1 = Math.PI * 2.0D * (face + 1) / tubeFaces;
			Vec3 from0 = tubePoint(from, angle0, widthScale);
			Vec3 from1 = tubePoint(from, angle1, widthScale);
			Vec3 to0 = tubePoint(to, angle0, widthScale);
			Vec3 to1 = tubePoint(to, angle1, widthScale);
			vertices.add(from0);
			vertices.add(from1);
			vertices.add(to1);
			vertices.add(to0);
		}
	}

	private static Basis basis(Vec3 diff, Vec3 preferredUp) {
		Vec3 direction = safeNormalize(diff, new Vec3(1, 0, 0));
		if (preferredUp != null && preferredUp.lengthSqr() > 1.0E-8D) {
			Vec3 projectedUp = preferredUp.subtract(direction.scale(preferredUp.dot(direction)));
			if (projectedUp.lengthSqr() > 1.0E-8D) {
				Vec3 up = safeNormalize(projectedUp, new Vec3(0, 1, 0));
				Vec3 right = safeNormalize(direction.cross(up), new Vec3(0, 0, 1));
				return new Basis(direction, right, safeNormalize(right.cross(direction), up));
			}
		}
		Vec3 axis = Math.abs(direction.y) < 0.75D ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
		Vec3 right = direction.cross(axis);
		if (right.lengthSqr() < 1.0E-8D) {
			right = direction.cross(new Vec3(0, 0, 1));
		}
		right = safeNormalize(right, new Vec3(0, 0, 1));
		Vec3 up = safeNormalize(right.cross(direction), new Vec3(0, 1, 0));
		return new Basis(direction, right, up);
	}

	private static Strand chooseBranchParent(List<Strand> parents, Random random, int branchDepth) {
		List<Strand> candidates = parents.stream().filter(parent -> parent.depth() < branchDepth).toList();
		if (candidates.isEmpty()) {
			return null;
		}
		return candidates.get(random.nextInt(candidates.size()));
	}

	private static Strand createStrand(Vec3 start, Vec3 end, TendrilEffectConfig config, long seed, float time,
			int depth, boolean branch, float baseWidth, float curl, float phase, int segments,
			SurfaceResolver surfaceResolver, Vec3 preferredUp) {
		Basis basis = basis(end.subtract(start), preferredUp);
		Random random = new Random(seed);
		List<Ring> rings = new ArrayList<>();
		Vec3 diff = end.subtract(start);
		float sideSign = random.nextBoolean() ? 1.0F : -1.0F;
		float liftSign = random.nextBoolean() ? 1.0F : -1.0F;
		for (int i = 0; i <= segments; i++) {
			float progress = (float) i / segments;
			float curve = (float) Math.sin(progress * Math.PI);
			float curled = curl * curve * sideSign;
			float sag = config.sag() * curve;
			float writhe = (float) Math.sin(time * config.writheFrequency() + phase + progress * 5.0F)
					* config.writheAmplitude() * curve;
			float crossWrithe = (float) Math.cos(time * config.writheFrequency() * 0.73F + phase + progress * 4.0F)
					* config.writheAmplitude() * 0.45F * curve;
			Vec3 center = start.add(diff.scale(progress)).add(basis.right().scale(curled + writhe))
					.add(basis.up().scale(crossWrithe * liftSign - sag));
			Vec3 tangent = i < segments ? diff.add(basis.right().scale(curl * Math.cos(progress * Math.PI)))
					: diff;
			if (config.mode() == TendrilEffectConfig.Mode.SURFACE) {
				center = surfaceResolver.snap(center, tangent, config).orElse(center);
			}
			float width = baseWidth * (1.0F - progress * (1.0F - config.tipScale()));
			rings.add(new Ring(center, width, basis.right(), basis.up(), progress));
		}
		return new Strand(List.copyOf(rings), branch, depth);
	}

	private static Ring interpolate(Ring from, Ring to, float amount) {
		return new Ring(lerp(from.center(), to.center(), amount), from.width() + (to.width() - from.width()) * amount,
				safeNormalize(lerp(from.right(), to.right(), amount), from.right()),
				safeNormalize(lerp(from.up(), to.up(), amount), from.up()),
				from.progress() + (to.progress() - from.progress()) * amount);
	}

	private static Vec3 lerp(Vec3 from, Vec3 to, float amount) {
		return from.add(to.subtract(from).scale(amount));
	}

	private static boolean same(Vec3 left, Vec3 right) {
		return Double.compare(left.x, right.x) == 0 && Double.compare(left.y, right.y) == 0
				&& Double.compare(left.z, right.z) == 0;
	}

	private static Vec3 safeNormalize(Vec3 value, Vec3 fallback) {
		return value.lengthSqr() < 1.0E-8D ? fallback : value.normalize();
	}

	private static Vec3 tangentAt(Strand strand, int index) {
		List<Ring> rings = strand.rings();
		int before = Math.max(0, index - 1);
		int after = Math.min(rings.size() - 1, index + 1);
		return safeNormalize(rings.get(after).center().subtract(rings.get(before).center()), new Vec3(1, 0, 0));
	}

	private static Vec3 tubePoint(Ring ring, double angle, float widthScale) {
		return ring.center().add(ring.right().scale(Math.cos(angle) * ring.width() * widthScale))
				.add(ring.up().scale(Math.sin(angle) * ring.width() * widthScale));
	}
}
