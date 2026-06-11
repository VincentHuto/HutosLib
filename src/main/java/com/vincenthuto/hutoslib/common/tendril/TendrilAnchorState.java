package com.vincenthuto.hutoslib.common.tendril;

import java.util.Optional;

import net.minecraft.world.phys.Vec3;

public class TendrilAnchorState {
	private final TendrilAnchor anchor;
	private Vec3 lastResolved;

	public TendrilAnchorState(TendrilAnchor anchor) {
		this.anchor = anchor;
	}

	public Optional<Vec3> resolve(TendrilAnchor.EntityResolver resolver) {
		Optional<Vec3> resolved = anchor.resolve(resolver);
		resolved.ifPresent(value -> lastResolved = value);
		return resolved.isPresent() ? resolved : Optional.ofNullable(lastResolved);
	}
}
