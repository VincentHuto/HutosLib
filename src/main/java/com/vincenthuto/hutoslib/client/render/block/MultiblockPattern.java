package com.vincenthuto.hutoslib.client.render.block;

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
		this.patternArray = patternArray;
	}

	public BlockPattern getBlockPattern() {
		return pattern;
	}

	public void setPattern(BlockPattern pattern) {
		this.pattern = pattern;
	}

	public Map<String, Block> getSymbolList() {
		return symbolList;
	}

	public String[][] getPatternArray() {
		return patternArray;
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

	public List<Block> getRelativeBlockList() {
		List<Block> blockList = new ArrayList<>();
		for (int T = 0; T < patternArray.length; T++) {
			String[] currentAisle = patternArray[T];
			int height = currentAisle.length;
			System.out.println();
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < currentAisle[i].toCharArray().length; j++) {
					blockList.add(symbolList.get(String.valueOf(currentAisle[i].toCharArray()[j])));
				}
			}
		}
		return blockList;
	}

	public List<BlockPosBlockPair> getBlockPosBlockList() {
		List<BlockPosBlockPair> list = new ArrayList<BlockPosBlockPair>();
		// Block
		for (int T = 0; T < patternArray.length; T++) {
			String[] currentAisle = patternArray[T];
			int height = currentAisle.length;
			for (int i = 0; i < height; i++) {
				for (int j = 0; j < currentAisle[i].toCharArray().length; j++) {
					list.add(new BlockPosBlockPair(symbolList.get(String.valueOf(currentAisle[i].toCharArray()[j])),
							new BlockPos(j, (height - i - 1), T)));
				}
			}
		}
		return list;
	}

	public HashMap<Block, Integer> getBlockCount(boolean sortAscending) {
		HashMap<Block, Integer> distinct = new HashMap<Block, Integer>();
		for (int i = 0; i < patternArray.length; i++) {
			for (int j = 0; j < patternArray[i].length; j++) {
				for (int k = 0; k < patternArray[i][j].length(); k++) {
					String curr = String.valueOf(patternArray[i][j].charAt(k));
					if (curr != "A") {
						if (!distinct.containsKey(symbolList.get(String.valueOf(curr)))) {
							distinct.put(symbolList.get(String.valueOf(curr)), 1);
						} else {
							Integer incr = distinct.get(symbolList.get(String.valueOf(curr))) + 1;
							distinct.put(symbolList.get(String.valueOf(curr)), incr);
						}
					}
				}

			}
		}
		List<Map.Entry<Block, Integer>> list = new LinkedList<Map.Entry<Block, Integer>>(distinct.entrySet());

		list.sort((o1, o2) -> sortAscending
				? o1.getValue().compareTo(o2.getValue()) == 0 ? (o1.getKey() == o2.getKey() ? 0 : 1)
						: o1.getValue().compareTo(o2.getValue())
				: o2.getValue().compareTo(o1.getValue()) == 0 ? (o2.getKey() == o1.getKey() ? 0 : 1)
						: o2.getValue().compareTo(o1.getValue()));
		return list.stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue, (a, b) -> b, LinkedHashMap::new));
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

}
