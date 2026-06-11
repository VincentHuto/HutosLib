package com.vincenthuto.hutoslib.common.tendril;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;

class TendrilAnchorTest {

	@Test
	void pointAnchorBufferRoundTripPreservesPosition() {
		TendrilAnchor anchor = new TendrilAnchor.Point(new Vec3(1.0, 2.0, 3.0));
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		TendrilAnchor.toBuffer(buf, anchor);

		assertEquals(anchor, TendrilAnchor.fromBuffer(buf));
	}

	@Test
	void entityAnchorBufferRoundTripPreservesEntityPointAndOffset() {
		TendrilAnchor anchor = new TendrilAnchor.Entity(42, TendrilAnchor.AnchorPoint.CENTER,
				new Vec3(0.25, 0.5, -0.25));
		FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

		TendrilAnchor.toBuffer(buf, anchor);

		assertEquals(anchor, TendrilAnchor.fromBuffer(buf));
	}

	@Test
	void anchorStateFallsBackToLastResolvedEntityPosition() {
		TendrilAnchor anchor = new TendrilAnchor.Entity(42, TendrilAnchor.AnchorPoint.CENTER, Vec3.ZERO);
		TendrilAnchorState state = new TendrilAnchorState(anchor);

		Optional<Vec3> first = state.resolve((entityId, anchorPoint, offset) -> Optional.of(new Vec3(4.0, 5.0, 6.0)));
		Optional<Vec3> fallback = state.resolve((entityId, anchorPoint, offset) -> Optional.empty());

		assertEquals(new Vec3(4.0, 5.0, 6.0), first.orElseThrow());
		assertEquals(new Vec3(4.0, 5.0, 6.0), fallback.orElseThrow());
	}

	@Test
	void neverResolvedEntityAnchorReturnsEmpty() {
		TendrilAnchor anchor = new TendrilAnchor.Entity(42, TendrilAnchor.AnchorPoint.CENTER, Vec3.ZERO);
		TendrilAnchorState state = new TendrilAnchorState(anchor);

		Optional<Vec3> resolved = state.resolve((entityId, anchorPoint, offset) -> Optional.empty());

		assertTrue(resolved.isEmpty());
	}
}
