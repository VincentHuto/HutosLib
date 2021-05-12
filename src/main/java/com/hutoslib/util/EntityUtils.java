package com.hutoslib.util;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

public class EntityUtils {

	/***
	 * @param type   What type of entities to get be it
	 *               Mobs,Living,Players,Items,Etc
	 * @param center Entity to center around
	 * @param radius How large to grow the bounding box
	 */
	public static List<?> getEntitesInRadius(Class<? extends Entity> type, Entity center, double radius) {
		return center.world.getEntitiesWithinAABB(type, center.getBoundingBox().grow(radius));
	}

	@Nullable
	public static EntityRayTraceResult rayTraceEntities(Entity shooter, double range,
			@Nullable Predicate<Entity> filter) {
		Vector3d eyes = shooter.getEyePosition(1f);
		Vector3d end = eyes.add(shooter.getLookVec().mul(range, range, range));

		Entity result = null;
		double distance = range * range;
		for (Entity entity : shooter.world.getEntitiesInAABBexcluding(shooter, shooter.getBoundingBox().grow(range),
				filter)) {
			Optional<Vector3d> opt = entity.getBoundingBox().grow(0.3).rayTrace(eyes, end);
			if (opt.isPresent()) {
				double dist = eyes.squareDistanceTo(opt.get());
				if (dist < distance) {
					result = entity;
					distance = dist;
				}
			}
		}

		return result == null ? null : new EntityRayTraceResult(result);
	}

}
