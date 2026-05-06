package com.vincenthuto.hutoslib.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.pattern.BlockPattern;

public class MultiblockPattern {

	BlockPattern pattern;
	Map<String, Block> symbolList;
	Map<String, MultiblockPatternKey> keyList;
	String[][] patternArray;

	/***
	 *
	 * This array is formatted with each String[] is 1 aisle in the BlockPattern
	 * While this may seem redundant when already using BlockPattern, it is so I can
	 * get relative block placement when rendering in GUI AKA the book pattern of
	 * RRR RBR RRR would need 3 String[1] "3 Aisle thick, 1 tall" The pattern for
	 * the living staff would be 1 String[3] "1 Aisle thick, 3 tall"
	 *
	 ***/

	public MultiblockPattern(BlockPattern pattern, Map<String, Block> symbolList, String[][] patternArray) {
		this.pattern = pattern;
		this.symbolList = symbolList;
		this.keyList = symbolList.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
				entry -> MultiblockPatternKey.block(entry.getKey(), entry.getValue()), (a, b) -> b,
				LinkedHashMap::new));
		this.patternArray = patternArray;
	}

	public MultiblockPattern(BlockPattern pattern, Map<String, MultiblockPatternKey> keyList, String[][] patternArray,
			boolean useDisplayKeys) {
		this.pattern = pattern;
		this.keyList = keyList;
		this.symbolList = keyList.entrySet().stream().collect(Collectors.toMap(Entry::getKey,
				entry -> entry.getValue().fallbackBlock(), (a, b) -> b, LinkedHashMap::new));
		this.patternArray = patternArray;
	}

	public HashMap<Block, Integer> getBlockCount(boolean sortAscending) {
		HashMap<Block, Integer> distinct = new HashMap<>();
		for (String[] element : patternArray) {
			for (int j = 0; j < element.length; j++) {
				for (int k = 0; k < element[j].length(); k++) {
					String curr = String.valueOf(element[j].charAt(k));
					Block block = symbolList.get(curr);
					if (block != null && !block.defaultBlockState().isAir()) {
						if (!distinct.containsKey(block)) {
							distinct.put(block, 1);
						} else {
							Integer incr = distinct.get(block) + 1;
							distinct.put(block, incr);
						}
					}
				}

			}
		}
		List<Map.Entry<Block, Integer>> list = new LinkedList<>(distinct.entrySet());

		list.sort((o1, o2) -> sortAscending
				? o1.getValue().compareTo(o2.getValue()) == 0 ? (o1.getKey() == o2.getKey() ? 0 : 1)
						: o1.getValue().compareTo(o2.getValue())
				: o2.getValue().compareTo(o1.getValue()) == 0 ? (o2.getKey() == o1.getKey() ? 0 : 1)
						: o2.getValue().compareTo(o1.getValue()));
		return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
	}

	public List<MaterialCount> getMaterialCounts(boolean sortAscending) {
		Map<MultiblockPatternKey, Integer> distinct = new LinkedHashMap<>();
		for (String[] element : patternArray) {
			for (String row : element) {
				for (int k = 0; k < row.length(); k++) {
					MultiblockPatternKey key = keyList.get(String.valueOf(row.charAt(k)));
					if (key == null || key.isAir()) {
						continue;
					}
					distinct.merge(key, 1, Integer::sum);
				}
			}
		}
		List<MaterialCount> list = distinct.entrySet().stream()
				.map(entry -> new MaterialCount(entry.getKey(), entry.getValue()))
				.collect(Collectors.toCollection(ArrayList::new));
		list.sort((o1, o2) -> {
			int count = sortAscending
					? Integer.compare(o1.count(), o2.count())
					: Integer.compare(o2.count(), o1.count());
			if (count != 0) {
				return count;
			}
			return o1.key().displayLabel().compareTo(o2.key().displayLabel());
		});
		return list;
	}

	public BlockPattern getBlockPattern() {
		return pattern;
	}

	public List<BlockPosBlockPair> getBlockPosBlockList() {
		return getBlockPosBlockList(false, 0);
	}

	public List<BlockPosBlockPair> getDisplayBlockPosBlockList(long cycleIndex) {
		return getBlockPosBlockList(true, cycleIndex);
	}

	private List<BlockPosBlockPair> getBlockPosBlockList(boolean display, long cycleIndex) {
		List<BlockPosBlockPair> list = new ArrayList<>();
		// Block
		for (int T = 0; T < patternArray.length; T++) {
			String[] currentAisle = patternArray[T];
			int height = currentAisle.length;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < currentAisle[i].toCharArray().length; j++) {
					String symbol = String.valueOf(currentAisle[i].toCharArray()[j]);
					MultiblockPatternKey key = keyList.get(symbol);
					Block block = display && key != null ? key.displayBlock(cycleIndex) : symbolList.get(symbol);
					list.add(new BlockPosBlockPair(block,
							new BlockPos(j, (height - i - 1), T)));
				}
			}
		}
		return list;
	}

	public String[][] getPatternArray() {
		return patternArray;
	}

	public List<Block> getRelativeBlockList() {
		List<Block> blockList = new ArrayList<>();
		for (String[] currentAisle : patternArray) {
			int height = currentAisle.length;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < currentAisle[i].toCharArray().length; j++) {
					blockList.add(symbolList.get(String.valueOf(currentAisle[i].toCharArray()[j])));
				}
			}
		}
		return blockList;
	}

	public List<BlockPos> getRelativeBlockPosList() {
		List<BlockPos> blockList = new ArrayList<>();
		for (int T = 0; T < patternArray.length; T++) {
			String[] currentAisle = patternArray[T];
			int height = currentAisle.length;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < currentAisle[i].toCharArray().length; j++) {
					blockList.add(new BlockPos(j, (height - i - 1), T));
				}
			}
		}
		return blockList;
	}

	public Map<String, Block> getSymbolList() {
		return symbolList;
	}

	public Map<String, MultiblockPatternKey> getKeyList() {
		return keyList;
	}

	public void printMultiblockLayout() {
		for (int T = 0; T < patternArray.length; T++) {
			String[] currentAisle = patternArray[T];
			int height = currentAisle.length;
			System.out.println();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < currentAisle[i].toCharArray().length; j++) {
					String coords = "(X:" + j + ",Y:" + (height - i - 1) + ",Z:" + T + ")";
					System.out.print(
							coords + ": " + symbolList.get(String.valueOf(currentAisle[i].toCharArray()[j])) + "\t");
				}
				System.out.println("");
			}
		}
	}

	public void setPattern(BlockPattern pattern) {
		this.pattern = pattern;
	}

	public record MaterialCount(MultiblockPatternKey key, int count) {
	}

}
