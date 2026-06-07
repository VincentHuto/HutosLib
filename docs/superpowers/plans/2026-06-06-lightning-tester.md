# Lightning Tester Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Build GUI-backed Lightning Tester item and block tools for exercising both HutosLib lightning render paths.

**Architecture:** Use one shared `LightningTestConfig` model for validation, serialization, UI state, item data, block entity data, and spawn translation. Add client screens that edit local config copies and send save/test packets to the server. Add one server-to-client spawn packet that renders either the custom `BoltRenderer` path or the registered particle path.

**Tech Stack:** Java 21, NeoForge 1.21.1, vanilla `Screen`, NeoForge custom payload packets, vanilla custom item data, block entities.

---

### Task 1: Shared Config

**Files:**
- Create: `src/main/java/com/vincenthuto/hutoslib/common/lightning/LightningTestConfig.java`
- Test: `src/test/java/com/vincenthuto/hutoslib/common/lightning/LightningTestConfigTest.java`

- [ ] Add tests for default config, clamp behavior, and packet round-trip.
- [ ] Implement enum backend, config fields, clamp, encode, decode, copy, and item custom-data helpers.
- [ ] Run `.\gradlew.bat test`.

### Task 2: Spawn Service and Packets

**Files:**
- Create: `src/main/java/com/vincenthuto/hutoslib/common/lightning/LightningTesterSpawner.java`
- Create: `src/main/java/com/vincenthuto/hutoslib/common/network/PacketSpawnLightningTest.java`
- Create: `src/main/java/com/vincenthuto/hutoslib/common/network/PacketLightningTesterItem.java`
- Create: `src/main/java/com/vincenthuto/hutoslib/common/network/PacketLightningTesterBlock.java`
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/network/HLPacketHandler.java`

- [ ] Add client spawn packet for both backends.
- [ ] Add item save/test packet.
- [ ] Add block save/test packet.
- [ ] Register packets.
- [ ] Run `.\gradlew.bat compileJava`.

### Task 3: Item Tool

**Files:**
- Create: `src/main/java/com/vincenthuto/hutoslib/common/item/ItemLightningTester.java`
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/registry/HLItemInit.java`

- [ ] Register `lightning_tester`.
- [ ] Normal use spawns with saved item config.
- [ ] Shift use opens the item tester screen client-side.
- [ ] Run `.\gradlew.bat compileJava`.

### Task 4: Block Tool

**Files:**
- Create: `src/main/java/com/vincenthuto/hutoslib/common/block/BlockLightningTester.java`
- Create: `src/main/java/com/vincenthuto/hutoslib/common/block/entity/LightningTesterBlockEntity.java`
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/registry/HLBlockInit.java`
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/block/entity/HLBlockEntityInit.java`

- [ ] Register `lightning_tester_block`.
- [ ] Store config on block entity.
- [ ] Normal use spawns from block.
- [ ] Shift use opens block tester screen client-side.
- [ ] Add optional server tick repeat.
- [ ] Run `.\gradlew.bat compileJava`.

### Task 5: Screens

**Files:**
- Create: `src/main/java/com/vincenthuto/hutoslib/client/screen/lightning/LightningTesterScreen.java`
- Create: `src/main/java/com/vincenthuto/hutoslib/client/screen/lightning/LightningTesterItemScreen.java`
- Create: `src/main/java/com/vincenthuto/hutoslib/client/screen/lightning/LightningTesterBlockScreen.java`

- [ ] Build shared controls for backend, color, range, offsets, timing, shape, seed, and repeat.
- [ ] Add Test, Save, and Done buttons.
- [ ] Send packet updates.
- [ ] Run `.\gradlew.bat compileJava`.

### Task 6: Data

**Files:**
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/data/HLBlockStateProvider.java`
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/data/HLItemModelProvider.java`
- Modify: `src/main/java/com/vincenthuto/hutoslib/common/data/HLRecipeProvider.java`
- Modify: `src/main/resources/assets/hutoslib/lang/en_us.json`

- [ ] Add generated block state/model coverage.
- [ ] Add item model coverage.
- [ ] Add simple recipes.
- [ ] Add language keys.
- [ ] Run `.\gradlew.bat compileJava test`.
