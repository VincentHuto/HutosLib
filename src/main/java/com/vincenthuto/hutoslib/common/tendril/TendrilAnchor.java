package com.vincenthuto.hutoslib.common.tendril;

import java.util.Optional;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public sealed interface TendrilAnchor permits TendrilAnchor.Point, TendrilAnchor.Entity {

	enum AnchorPoint {
		FEET, CENTER, EYES;

		static AnchorPoint byName(String name) {
			for (AnchorPoint point : values()) {
				if (point.name().equals(name)) {
					return point;
				}
			}
			return CENTER;
		}
	}

	@FunctionalInterface
	interface EntityResolver {
		Optional<Vec3> resolve(int entityId, AnchorPoint anchorPoint, Vec3 offset);
	}

	record Point(Vec3 position) implements TendrilAnchor {
		@Override
		public Optional<Vec3> resolve(EntityResolver resolver) {
			return Optional.of(position);
		}
	}

	record Entity(int entityId, AnchorPoint anchorPoint, Vec3 offset) implements TendrilAnchor {
		public Entity {
			anchorPoint = anchorPoint == null ? AnchorPoint.CENTER : anchorPoint;
			offset = offset == null ? Vec3.ZERO : offset;
		}

		@Override
		public Optional<Vec3> resolve(EntityResolver resolver) {
			return resolver.resolve(entityId, anchorPoint, offset);
		}
	}

	Optional<Vec3> resolve(EntityResolver resolver);

	static EntityResolver forLevel(Level level) {
		return (entityId, anchorPoint, offset) -> {
			net.minecraft.world.entity.Entity entity = level.getEntity(entityId);
			if (entity == null) {
				return Optional.empty();
			}
			Vec3 base = switch (anchorPoint) {
			case FEET -> entity.position();
			case EYES -> entity instanceof LivingEntity living ? living.getEyePosition()
					: entity.position().add(0, entity.getBbHeight() * 0.85D, 0);
			case CENTER -> entity.position().add(0, entity.getBbHeight() * 0.5D, 0);
			};
			return Optional.of(base.add(offset));
		};
	}

	static TendrilAnchor fromBuffer(FriendlyByteBuf buf) {
		String type = buf.readUtf();
		if ("entity".equals(type)) {
			return new Entity(buf.readInt(), AnchorPoint.byName(buf.readUtf()),
					new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
		}
		return new Point(new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()));
	}

	static void toBuffer(FriendlyByteBuf buf, TendrilAnchor anchor) {
		if (anchor instanceof Entity entity) {
			buf.writeUtf("entity");
			buf.writeInt(entity.entityId());
			buf.writeUtf(entity.anchorPoint().name());
			writeVec(buf, entity.offset());
			return;
		}
		if (anchor instanceof Point point) {
			buf.writeUtf("point");
			writeVec(buf, point.position());
			return;
		}
		throw new IllegalArgumentException("Unsupported tendril anchor: " + anchor);
	}

	private static void writeVec(FriendlyByteBuf buf, Vec3 vec) {
		buf.writeDouble(vec.x);
		buf.writeDouble(vec.y);
		buf.writeDouble(vec.z);
	}
}
