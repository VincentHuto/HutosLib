# HutosLib – NeoForge Porting Primer: 1.21.1 → 1.21.11

> Written against the actual source in branch `neo-1.21.1`.
> Feed this file to GitHub Copilot as context before asking it to migrate any individual file.
> Assume the 1.21.1 build is clean and working.
> This primer covers **all breaking changes across 1.21.1 → 1.21.9 → 1.21.11** in one pass.

---

## 0. Project Snapshot

| Property | Current (1.21.1) | Target (1.21.11) |
|---|---|---|
| `minecraft_version` | `1.21.1` | `1.21.11` |
| `neo_version` | `21.1.172` | `21.11.x` (latest stable/beta) |
| MDG plugin | `net.neoforged.moddev 2.0.141` | `2.0.112+` ✓ already fine |
| Java | 21 | 21 ✓ unchanged |
| JEI | `19.21.0.247` | update to 1.21.11 build |

**This is a larger port than 1.21.1→1.21.9.** It accumulates breaking changes from every intermediate
version. The distinct areas requiring work in HutosLib are, in order of impact:

1. **`ResourceLocation` → `Identifier`** — 51 files, most widespread change (introduced in 1.21.11)
2. **`ParticleRenderType` anonymous classes → records** — 3 particle files + `HLRenderTypeInit` (introduced in 1.21.4)
3. **`RenderType` static methods → `RenderTypes`** — `HLRenderTypeInit`, `HLRenderStateShards` (introduced in 1.21.11)
4. **`javax.annotation` → `org.jspecify`** — 18 files (introduced in 1.21.11)
5. **FML API** — `FMLEnvironment.dist` field → method, one occurrence (introduced in 1.21.9)
6. **BER rewrite** — `RenderTileDisplayPedestal` (introduced in 1.21.9)
7. **Keybind category** — `HLClientEvents` string → `KeyMapping.Category` (introduced in 1.21.9)
8. **`IItemHandler` / `IItemHandlerModifiable`** — deprecated since 1.21.9, still present in 1.21.11 but flagged (3 files)
9. **`net.minecraft.Util` package** — moved in 1.21.11 (1 file)

---

## 1. Gradle Changes

### `gradle.properties`

```properties
minecraft_version=1.21.11
minecraft_version_range=[1.21.11,1.22)
neo_version=21.11.x
neo_version_range=[21.11,)

# Parchment: update to a 1.21.11-compatible version or remove the block
# if no 1.21.11 parchment mappings are available yet:
# parchment_minecraft_version=1.21.11
# parchment_mappings_version=<latest>

# Update JEI to 1.21.11 build:
jei_version=<latest 1.21.11 build>
```

### `build.gradle`

MDG 2.0.141 is already above the 2.0.112 minimum — no plugin change needed.

---

## 2. `ResourceLocation` → `Identifier` (51 files — most work)

**Introduced in 1.21.11.** `net.minecraft.resources.ResourceLocation` was renamed to
`net.minecraft.resources.Identifier`. This is a **compile error** in every file that uses it.

The rename is mechanical — a project-wide find-and-replace — but touches 51 files.

### Import replacement

```java
// OLD:
import net.minecraft.resources.ResourceLocation;

// NEW:
import net.minecraft.resources.Identifier;
```

### Call site replacements

```java
// Type declarations:
- ResourceLocation myLoc = ...
+ Identifier myLoc = ...

// Static factory methods (unchanged — just the class name changes):
- ResourceLocation.fromNamespaceAndPath("hutoslib", "thing")
+ Identifier.fromNamespaceAndPath("hutoslib", "thing")

- ResourceLocation.tryParse("neoforge:any")
+ Identifier.tryParse("neoforge:any")

- ResourceLocation.parse("some:thing")
+ Identifier.parse("some:thing")

// HutosLib.rloc() helper — update its return type too:
// In HutosLib.java:
- public static ResourceLocation rloc(String path) {
-     return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
- }
+ public static Identifier rloc(String path) {
+     return Identifier.fromNamespaceAndPath(MOD_ID, path);
+ }
```

### All 51 affected files

Every file below has `ResourceLocation` in active code (not just comments or imports).
The rename is the same in each — change the type and factory class, nothing else changes.

**client/render:**
`FluidInfoArea`, `HLRenderHelper`, `HLRenderStateShards`, `LayerArmBanner`,
`RenderItemArmBanner`, `RenderItemGuideBook`

**client/screen:**
`BannerSlotScreen`, `HLAbstractSkillTreeScreen`, `HLSkillTree` (commented-out constants — update anyway),
`HLButtonArrow`, `HLButtonTextured`, `HLGuiGuideTitlePage`, `HLGuiGuidePageTOC`

**client:**
`HLLocHelper`

**common/banner:**
`BannerSlotCapability`, `BannerFinder` (via IBannerSlotItem), `BannerExtensionSlot`

**common/container:**
`BannerSlot`, `BannerExtensionSlot`, `IBannerSlot`, `IBannerSlotItem`,
`BannerSlotContainer`, `BannerSlotItemHandler`

**common/data/book:**
`BookPlaceboReloadListener`, `PageTemplate`, `BookDataTemplate`, `CraftingRecipeTemplate`,
`BookCodeModel`, `ChapterTemplate`, `BookTemplate`, `BookDataResource`

**common/data/skilltree:**
`SkillTemplate`, `BranchTemplate`, `SkillTreePlaceboReloadListener`, `TreeDataTemplate`,
`TreeDataResource`, `TreeCodeModel`, `TreeTemplate`

**common/data/shadow:**
`DynamicRegistryObject`, `IPlatformRecipeHelper`, `PlaceboJsonReloadListener`,
`RecipeHelper`, `TypeKeyed`, `SerializerMap`

**common/network:**
`ReloadListenerPacket`

**common/item:**
`ItemHLGuideBook`, `ItemKnapper`, `ItemArmBanner`, `ItemGuideBook`

**common/registry:**
`HLItemInit`

**common/recipe:**
`IModRecipe`

**math:**
`DimensionalPosition`

**HutosLib.java** (main mod class)

### Strategy for Copilot

Open each file, say: *"Replace all uses of `ResourceLocation` with `Identifier` and update the
import from `net.minecraft.resources.ResourceLocation` to `net.minecraft.resources.Identifier`.
Do not change any logic — only the class name changes."*

---

## 3. `ParticleRenderType` Anonymous Classes → Records (4 files)

**Introduced in 1.21.4.** `ParticleRenderType` became a record. Anonymous classes implementing
it (`new ParticleRenderType() { ... }`) no longer compile. The `begin(Tesselator, TextureManager)`
method is also gone — it is now baked into the record.

### Affected files

- `client/HLRenderTypeInit.java` — `GLOW_RENDER`, `DARK_GLOW_RENDER`
- `client/particle/ParticleLightning.java` — `LIGHTNING_BOLT_RENDER`

### Current broken pattern

```java
// DOES NOT COMPILE in 1.21.4+:
public static final ParticleRenderType GLOW_RENDER = new ParticleRenderType() {
    @Override
    public BufferBuilder begin(Tesselator tesselator, TextureManager textureManager) {
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getParticleShader);
        // ... setup ...
        return tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE);
    }

    @Override
    public String toString() { return "hutoslib:glow_rend"; }
};
```

### New pattern — `ParticleRenderType` is now a record

```java
// ParticleRenderType record takes: name (String) + RenderType it uses.
// The setup logic that was in begin() is now expressed purely through the RenderType.

// For GLOW_RENDER — additive blending particle:
public static final ParticleRenderType GLOW_RENDER = new ParticleRenderType(
    "hutoslib:glow_rend",
    HLRenderTypeInit.PARTICLE_GLOW   // your custom RenderType that encodes the blend state
);

// For DARK_GLOW_RENDER:
public static final ParticleRenderType DARK_GLOW_RENDER = new ParticleRenderType(
    "hutoslib:dark_glow_rend",
    HLRenderTypeInit.PARTICLE_DARK_GLOW
);
```

The GL state setup (`RenderSystem.enableBlend()`, `blendFunc`, etc.) that was in `begin()`
**must move into a `RenderType`** (or `RenderPipeline` depending on the version). You need
to define dedicated `RenderType` constants for each particle visual, then pass those to the
`ParticleRenderType` record constructor.

### `HLRenderTypeInit` — also needs `GLOW_RENDER` / `DARK_GLOW_RENDER` field updates

The `HLRenderTypeInit` class extends `RenderType` (to access protected members). This pattern
may also break — see section 4 on `RenderType` → `RenderTypes`. Update `GLOW_RENDER` and
`DARK_GLOW_RENDER` here in the same pass.

### `ParticleLightning` — `LIGHTNING_BOLT_RENDER`

Same pattern. The `beginRenderCommon` helper method that was called from `begin()` needs to
be folded into a `RenderType` definition, or the particle switched to an existing render type
if the visual is close enough.

---

## 4. `RenderType` Static Methods → `RenderTypes` (2 files)

**Introduced in 1.21.11.** Vanilla render types have moved from static methods on `RenderType`
to a separate `RenderTypes` class. Also, `RenderType.create(...)` and `RenderType.CompositeState`
construction have been refactored.

### Affected files

- `client/HLRenderTypeInit.java`
- `client/render/HLRenderStateShards.java`

### Changes

```java
// Static render type accessors:
- RenderType.entityTranslucentCull(texture)
+ RenderTypes.entityTranslucentCull(texture)

- RenderType.entitySolid(texture)
+ RenderTypes.entitySolid(texture)

// RenderType.create() still exists but the CompositeState construction may differ.
// HLRenderStateShards.createDefault() calls RenderType.create() directly — verify the
// signature still matches. If it changed, consult how vanilla constructs RenderTypes in
// 1.21.11 (RenderTypes class) for the new pattern.

// HLRenderTypeInit extends RenderType to access protected CompositeState/shards.
// This pattern may break if RenderType internals changed. Check if the protected
// fields (POSITION_COLOR_SHADER, LIGHTNING_TRANSPARENCY, etc.) still exist on RenderType
// or moved to RenderTypes.
```

### `HLRenderStateShards` — `GUI_CUTOUT` / `GUI_TRANSLUCENT`

These use `RenderType.CompositeState.builder()` and `RenderType.create()` — verify both still
exist in 1.21.11. If `CompositeState` was refactored, follow how vanilla `RenderTypes` constructs
its types.

---

## 5. `javax.annotation` → `org.jspecify` (18 files)

**Introduced in 1.21.11.** Minecraft and NeoForge switched nullability annotations from
`javax.annotation.@Nullable` / `@Nonnull` to JSpecify's `org.jspecify.annotations.@Nullable`
and `@NonNull`. The `javax` versions still compile in most cases but will generate warnings
and may cause issues with NullMarked package checking.

### Import replacement

```java
// OLD:
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

// NEW:
import org.jspecify.annotations.Nullable;
import org.jspecify.annotations.NonNull;   // note: Nonnull → NonNull (capital N)
```

### Important: JSpecify annotations are "type use" only

```java
// WRONG — will not compile with JSpecify:
@Nullable Map.Entry<K, V> entry;

// CORRECT:
Map.@Nullable Entry<K, V> entry;

// Arrays — these have different meanings:
@Nullable Object[]   // non-null array of nullable Objects
Object @Nullable []  // nullable array of non-null Objects
```

### Affected files (18)

`BitLocation`, `MathUtils`, `ScreenBlockTintGetter`, `BoltParticleData`,
`TransformingVertexBuilder`, `BannerSlot`, `BannerExtensionSlot`, `IBannerSlot`,
`IBannerContainer`, `IBannerSlotItem`, `HLInvHelper`, `BannerSlotItemHandler`,
`HLBlockTagProvider`, `PSerializer`, `PlaceboJsonReloadListener`, `SerializerMap`,
`SimpleInventoryBlockEntity`, `BlockDisplayPedestal`

### Strategy

In each file: replace `javax.annotation.Nullable` → `org.jspecify.annotations.Nullable`,
and `javax.annotation.Nonnull` → `org.jspecify.annotations.NonNull`. Then check any annotated
generic type usages (like `@Nonnull ItemStack`) — these will work as-is for simple cases.

---

## 6. FML API Change — `FMLEnvironment.dist` (1 file)

**Introduced in 1.21.9.** Same as the 1.21.1→1.21.9 primer — field becomes a method call.

**File:** `HutosLib.java` constructor

```java
// OLD:
if (FMLEnvironment.dist.isClient()) {

// NEW:
if (FMLEnvironment.getDist().isClient()) {
```

Also search for `FMLEnvironment.production` → `FMLEnvironment.isProduction()` and
`FMLLoader.getGamePath()` → `FMLLoader.getCurrent().getGameDir()` — neither is currently
present in the source but grep to confirm.

---

## 7. BER Rewrite — `RenderTileDisplayPedestal` (1 file)

**Introduced in 1.21.9.** Same pattern documented in the 1.21.1→1.21.9 primer.
`BlockEntityRenderer.render()` is gone; three-method pattern required.

**File:** `client/render/block/RenderTileDisplayPedestal.java`

State fields needed:
```java
class DisplayPedestalRenderState extends BlockEntityRenderState {
    List<ItemStack> inventorySnapshot = new ArrayList<>();
    long gameTime;
}
```

Three methods: `createRenderState()` → `extractRenderState()` → `submit()`.
- Copy `te.inventory` (deep copy each `ItemStack`) and `te.getLevel().getGameTime()` in `extractRenderState()`
- Move all geometry + `itemRenderer.renderStatic(...)` to `submit()`
- Replace `MultiBufferSource bufferIn` with `SubmitNodeCollector collector`
- Replace `combinedLightIn` with `camera.packedLight`
- Pass `null` for the level arg to `renderStatic` (was already `null` in original)

---

## 8. Keybind Category Change — `HLClientEvents` (1 file)

**Introduced in 1.21.9.** Same as the 1.21.1→1.21.9 primer.

**File:** `client/HLClientEvents.java`

```java
// ADD constant:
public static final KeyMapping.Category ARMBANNER_CATEGORY =
    new KeyMapping.Category(Identifier.fromNamespaceAndPath("hutoslib", "armbanner"));
// Note: uses Identifier not ResourceLocation after the rename above

// UPDATE KeyMapping declaration:
// "key.armbanner.category" → ARMBANNER_CATEGORY

// ADD to initKeybinds():
ev.registerCategory(ARMBANNER_CATEGORY);   // must be first
```

---

## 9. `IItemHandler` / `IItemHandlerModifiable` — Deprecated (3 files)

**Deprecated for removal since 1.21.9.** As of 1.21.11 these are still present but annotated
`@Deprecated(forRemoval = true)`. They will compile with warnings. Removal is planned for a
future cycle.

**Affected files:** `BannerFinder`, `HLInvHelper`, `BannerSlotItemHandler`

### Current usage in HutosLib

- `BannerFinder.findBannerInInventory(IItemHandler inventory, ...)` — reads slots via `getSlots()` and `getStackInSlot()`, no writes
- `HLInvHelper.getInventory(...)` — returns `IItemHandler` from capability query; callers just read slots
- `BannerSlotItemHandler` — implements `IBannerSlot` backed by `IItemHandlerModifiable`; calls `setStackInSlot()` and `getStackInSlot()`

### Recommended migration (do this to avoid future breakage)

```java
// BannerFinder — replace IItemHandler parameter with ResourceHandler<ItemResource>:
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.handlers.ResourceHandler;

protected final Optional<? extends BannerGetter> findBannerInInventory(
        ResourceHandler<ItemResource> inventory,
        IntFunction<? extends BannerGetter> getterFactory) {
    for (int i = 0; i < inventory.size(); i++) {
        if (!inventory.getResource(i).isEmpty() &&
            inventory.getResource(i).toStack().getItem() instanceof ItemArmBanner) {
            return Optional.of(getterFactory.apply(i));
        }
    }
    return Optional.empty();
}

// HLInvHelper.getInventory — change return type:
public static ResourceHandler<ItemResource> getInventory(Level world, BlockPos pos, Direction side) {
    BlockEntity te = world.getBlockEntity(pos);
    if (te == null) return null;
    ResourceHandler<ItemResource> ret =
        world.getCapability(Capabilities.Item.BLOCK, pos, world.getBlockState(pos), te, side);
    if (ret == null) {
        ret = world.getCapability(Capabilities.Item.BLOCK, pos, world.getBlockState(pos), te, null);
    }
    return ret;
}

// BannerSlotItemHandler — replace IItemHandlerModifiable with ItemStacksResourceHandler
// or update setContents/getContents to use ResourceHandler<ItemResource> API
```

If deferring this migration, the deprecated classes will still compile in 1.21.11 —
just add `@SuppressWarnings("deprecation")` as a temporary measure and track it for cleanup.

---

## 10. `net.minecraft.Util` Package Change (1 file)

**Introduced in 1.21.11.** `net.minecraft.Util` was moved or its package changed.

**File:** `client/render/HLRenderStateShards.java` — imports `net.minecraft.Util` for `Util.memoize()`

```java
// OLD:
import net.minecraft.Util;

// NEW — check the exact new package in 1.21.11. Likely:
import net.minecraft.util.Util;
// or check the porting primer for the exact rename
```

---

## 11. `@OnlyIn` — Now Marker Only (1 occurrence)

**Introduced in 1.21.6/1.21.7.** `RuntimeDistCleaner` was removed, making `@OnlyIn` a purely
marker annotation with no runtime stripping effect. This does not break compilation but means
any code guarded by `@OnlyIn(Dist.CLIENT)` may run on the server if not also guarded by a
dist check at runtime.

**File:** `math/Vector3.java` line 277

```java
@OnlyIn(Dist.CLIENT)
// ... some method
```

Verify this method cannot be called on the server side. If it uses client-only classes, add
a runtime dist guard or move the method to a client-only class.

---

## 12. `BakedQuad` Changes — Not Affected

**Introduced in 1.21.11.** `BakedQuad` no longer stores data as `int[]` — it now has explicit
vertex position and UV fields. HutosLib does not use `BakedQuad` directly — confirmed by grep.
No changes needed.

---

## 13. What Is NOT Affected

- ❌ Transfer API (IFluidHandler/IFluidHandlerItem/IEnergyStorage → ResourceHandler) — HutosLib doesn't use these directly. `IFluidTank` in `FluidInfoArea` is the read-only display interface, unchanged.
- ❌ Networking — already on `RegisterPayloadHandlersEvent` / `CustomPacketPayload`
- ❌ Attachment types — already using `NeoForgeRegistries.Keys.ATTACHMENT_TYPES`
- ❌ Screens (`GuiGraphics` rename) — `GuiGraphicsExtractor` / `Screen#extractRenderState` is a **26.1** change, not 1.21.11
- ❌ Java version — stays at 21 (Java 25 is a 26.1 change)
- ❌ Parchment removal — Parchment still available in 1.21.11 (removed in 26.1)

---

## 14. Migration Checklist

```
[ ] 1. gradle.properties — bump minecraft_version, neo_version, jei_version
[ ] 2. ResourceLocation → Identifier — rename across all 51 files (find-and-replace):
    [ ] Update imports: ResourceLocation → Identifier
    [ ] Update HutosLib.rloc() return type
    [ ] Update all variable declarations and factory method calls
    [ ] After rename: update HLClientEvents keybind category to use Identifier
[ ] 3. ParticleRenderType anonymous classes → records (4 locations):
    [ ] HLRenderTypeInit.GLOW_RENDER
    [ ] HLRenderTypeInit.DARK_GLOW_RENDER
    [ ] ParticleLightning.LIGHTNING_BOLT_RENDER
    [ ] Define backing RenderType constants for each particle
[ ] 4. RenderType → RenderTypes static methods:
    [ ] HLRenderTypeInit — verify CompositeState/shader constants still accessible
    [ ] HLRenderStateShards — verify RenderType.create() / CompositeState.builder()
[ ] 5. javax.annotation → org.jspecify (18 files):
    [ ] Replace Nullable/Nonnull imports
    [ ] Check any annotated generic type positions
[ ] 6. HutosLib.java — FMLEnvironment.getDist()
[ ] 7. RenderTileDisplayPedestal — BER three-method rewrite
[ ] 8. HLClientEvents — KeyMapping.Category + registerCategory()
[ ] 9. IItemHandler deprecation — add @SuppressWarnings or migrate to ResourceHandler
[ ] 10. HLRenderStateShards — fix net.minecraft.Util import package
[ ] 11. Vector3.java — verify @OnlyIn method safety
[ ] 12. runClient smoke test
```

---

## 15. Search Terms for Copilot

| Search for | Action |
|---|---|
| `import net.minecraft.resources.ResourceLocation` | → `import net.minecraft.resources.Identifier` |
| `ResourceLocation` in type positions | → `Identifier` |
| `ResourceLocation.fromNamespaceAndPath` | → `Identifier.fromNamespaceAndPath` |
| `ResourceLocation.tryParse` | → `Identifier.tryParse` |
| `ResourceLocation.parse` | → `Identifier.parse` |
| `new ParticleRenderType()` | → `new ParticleRenderType(name, renderType)` record constructor |
| `public BufferBuilder begin(Tesselator` in particle files | Old ParticleRenderType method — move state to RenderType |
| `import net.minecraft.Util` | → check new package in 1.21.11 |
| `import javax.annotation.Nullable` | → `import org.jspecify.annotations.Nullable` |
| `import javax.annotation.Nonnull` | → `import org.jspecify.annotations.NonNull` |
| `FMLEnvironment.dist` | → `FMLEnvironment.getDist()` |
| `implements BlockEntityRenderer<` | → three-method BER rewrite |
| `void render(` in `RenderTileDisplayPedestal` | → split into `extractRenderState` + `submit` |
| `MultiBufferSource bufferIn` in render/block | → `SubmitNodeCollector collector` |
| `"key.armbanner.category"` | → `ARMBANNER_CATEGORY` (KeyMapping.Category / Identifier) |
| `RenderType.entityTranslucentCull` | → `RenderTypes.entityTranslucentCull` |

---

## 16. Reference Links

- [NeoForge 21.9 Release Notes](https://neoforged.net/news/21.9release/) — BER, keybind, FML changes
- [Transfer API Rework Blog Post](https://neoforged.net/news/21.9-transfer-rework/) — IItemHandler deprecation
- [NeoForge 21.11 Release Notes](https://neoforged.net/news/21.11release/) — ResourceLocation rename, RenderTypes, JSpecify, BakedQuad
- [1.21.11 Porting Primer — ChampionAsh5357](https://github.com/neoforged/.github/blob/main/primers/1.21.11/index.md) — full vanilla change list
- [1.21.4 Porting Primer](https://github.com/neoforged/.github/blob/main/primers/1.21.4/index.md) — ParticleRenderType record change
- **Workflow tip:** Keep a working 1.21.1 workspace open alongside 1.21.11. When a method
  won't compile, right-click → **Go to vanilla usage** in IntelliJ (scope: **All Places**),
  then compare how vanilla changed that call between versions.

---

*Based on `VincentHuto/HutosLib` branch `neo-1.21.1` — targeting NeoForge 21.11.x*
