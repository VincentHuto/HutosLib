package com.vincenthuto.hutoslib.math;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class MultiblockPatternKey {
	private final String symbol;
	private final Block fallbackBlock;
	private final Identifier tagId;
	private final List<Block> displayBlocks;
	private List<Block> resolvedDisplayBlocks;

	private MultiblockPatternKey(String symbol, Block fallbackBlock, Identifier tagId, List<Block> displayBlocks) {
		this.symbol = symbol;
		this.fallbackBlock = fallbackBlock;
		this.tagId = tagId;
		this.displayBlocks = List.copyOf(displayBlocks);
	}

	public static MultiblockPatternKey block(String symbol, Block block) {
		return new MultiblockPatternKey(symbol, block, null, List.of(block));
	}

	public static MultiblockPatternKey tag(String symbol, Identifier tagId, Block fallbackBlock) {
		return new MultiblockPatternKey(symbol, fallbackBlock, tagId, List.of());
	}

	public static MultiblockPatternKey tag(String symbol, Identifier tagId, Block fallbackBlock,
			List<Block> displayBlocks) {
		return new MultiblockPatternKey(symbol, fallbackBlock, tagId, displayBlocks);
	}

	public String symbol() {
		return symbol;
	}

	public Block fallbackBlock() {
		return fallbackBlock;
	}

	public boolean isTag() {
		return tagId != null;
	}

	public Identifier tagId() {
		return tagId;
	}

	public String displayLabel() {
		return isTag() ? "#" + tagId : fallbackBlock.getName().getString();
	}

	public Block displayBlock(long cycleIndex) {
		List<Block> blocks = resolvedDisplayBlocks();
		if (blocks.isEmpty()) {
			return fallbackBlock;
		}
		return blocks.get(Math.floorMod(cycleIndex, blocks.size()));
	}

	public List<Block> displayBlocks() {
		return resolvedDisplayBlocks();
	}

	public boolean isAir() {
		return fallbackBlock == null || fallbackBlock == Blocks.AIR;
	}

	private List<Block> resolvedDisplayBlocks() {
		// Non-tagged keys: just use the static block list (always size 1).
		if (!isTag()) {
			if (!displayBlocks.isEmpty()) {
				return displayBlocks;
			}
			return fallbackBlock == null ? List.of() : List.of(fallbackBlock);
		}

		// Tagged keys: ALWAYS prefer a live tag lookup so the cycling viewers
		// reflect the current data-driven tag contents, regardless of what
		// (possibly stale or single-entry) snapshot was sent over the network
		// at recipe-sync time.
		if (resolvedDisplayBlocks != null && resolvedDisplayBlocks.size() > 1) {
			return resolvedDisplayBlocks;
		}

		TagKey<Block> tag = TagKey.create(Registries.BLOCK, tagId);
		List<Block> resolved = new ArrayList<>();
		for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
			Block block = holder.value();
			if (block != Blocks.AIR) {
				resolved.add(block);
			}
		}

		// Some setups only bind tags via BlockState#is(tag); fall back to a
		// slow registry scan if the named-tag lookup didn't find anything.
		if (resolved.size() <= 1) {
			List<Block> scanned = scanTaggedBlocks(tag);
			if (scanned.size() > resolved.size()) {
				resolved = scanned;
			}
		}

		// Final fallback: the network snapshot (if any) or the fallback block.
		if (resolved.isEmpty()) {
			if (!displayBlocks.isEmpty()) {
				resolved = displayBlocks;
			} else if (fallbackBlock != null) {
				resolved = List.of(fallbackBlock);
			}
		}

		// Only cache once we actually have more than one entry, so we keep
		// retrying until tags are bound (e.g. before the first datapack
		// reload completes on the client).
		if (resolved.size() > 1) {
			resolvedDisplayBlocks = resolved;
		} else {
			resolvedDisplayBlocks = null;
		}
		return resolved;
	}

	private static List<Block> scanTaggedBlocks(TagKey<Block> tag) {
		List<Block> blocks = new ArrayList<>();
		for (Block block : BuiltInRegistries.BLOCK) {
			if (block != Blocks.AIR && block.defaultBlockState().is(tag)) {
				blocks.add(block);
			}
		}
		return blocks;
	}
}
