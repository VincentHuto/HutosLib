# Lightning Tester Design

## Goal

Add two development tools for testing HutosLib lightning effects in-game:

- A Lightning Tester item for player-to-target and player-to-entity effects.
- A Lightning Tester block for block-emitter effects.

Both tools must expose full parameter editing through GUI screens opened by shift right-click. The item and block screens may differ, but should feel and behave similarly.

## Shared Configuration

Both tools use a shared `LightningTestConfig` model. It owns defaults, validation, serialization, and spawn parameter translation.

Config fields:

- effect backend: `BoltRenderer` or registered particle packet path
- color preset
- outer color
- inner color
- range
- target offset X/Y/Z
- ticks per meter
- speed
- max age
- fract count
- max offset
- bolt size
- seed mode: random or fixed
- fixed seed
- block repeat enabled
- block repeat interval

Validation clamps values to sensible development ranges so malformed packets or hand-edited data cannot create extreme particle loads.

## Item Behavior

Normal right-click in air fires a lightning effect from the player eye position to the looked-at point within configured range.

Normal right-click on an entity fires from the player eye position to the entity center.

Shift right-click opens `LightningTesterItemScreen`. The screen edits a local copy of the held stack's config. `Save` sends the edited config to the server, which writes it back to the held item. `Test` sends the edited config to the server and spawns immediately without requiring a prior save.

Item config is stored on the item stack using vanilla custom data.

## Block Behavior

The block is a `BaseEntityBlock` with a `LightningTesterBlockEntity`.

Normal right-click spawns lightning from the block center to the configured offset target.

Shift right-click opens `LightningTesterBlockScreen`. The screen edits a local copy of the block entity config. `Save` sends the edited config to the server, which stores it on the block entity and syncs the block update. `Test` sends the edited config to the server and spawns immediately from that block.

If repeat is enabled, the block entity ticks server-side and spawns on the configured interval.

## GUI Design

Both screens use a compact, utilitarian dev-tool layout:

- title
- backend toggle
- color preset controls
- numeric steppers for range, offset, timing, and shape values
- seed mode toggle and seed adjustment
- block-only repeat controls
- `Test`, `Save`, and `Done` buttons

The screens share helper widgets and formatting where practical. No decorative or marketing-style UI.

## Networking

Client-to-server packets:

- save item tester config
- test item tester config
- save block tester config
- test block tester config

Server handlers validate the config before applying or spawning. Spawn work happens on the server and dispatches the final visual effect to nearby clients.

## Data Generation

Add registry entries, language keys, block/item models, and a simple recipe for the block and item. If no bespoke texture exists, use generated item/block models pointing at simple placeholder textures or existing generated model patterns.

## Testing

Unit tests cover:

- config defaults and clamping
- config serialization round-trip
- target calculation behavior that can be tested without a live Minecraft client

Compile verification covers the MC/NeoForge integration points.
