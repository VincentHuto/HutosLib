package com.hutoslib.util;

import java.util.List;

import net.minecraft.entity.Entity;

public class EntityUtil {

	/***
	 * @param type   What type of entities to get be it
	 *               Mobs,Living,Players,Items,Etc
	 * @param center Entity to center around
	 * @param radius How large to grow the bounding box
	 */
	public static List<?> getEntitesInRadius(Class<? extends Entity> type, Entity center, double radius) {
		return center.world.getEntitiesWithinAABB(type, center.getBoundingBox().grow(radius));
	}

}
