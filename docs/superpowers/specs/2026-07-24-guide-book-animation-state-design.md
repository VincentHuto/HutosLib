# Guide Book Animation State Design

## Goal

Allow a held guide book to animate open independently when the same player
owns other guide-book item types, while preventing duplicate stacks of one
item type from advancing its animation more than once per game tick.

## Root Cause

`ItemGuideBook` currently stores one `BookAnimState` per player UUID.
`inventoryTick` runs once for every guide-book stack in the inventory, so the
held stack opens that shared state while each unheld stack closes it during
the same tick. `RenderItemGuideBook` then reads the same shared state for all
guide-book item types.

## Architecture

HutosLib will retain a central client-side animation cache, but its key will
combine a player UUID with the identity of the `ItemGuideBook` singleton.
Each guide-book item type therefore receives an independent `BookAnimState`
for each player.

`BookAnimState` will record the last game tick on which it was updated.
`ItemGuideBook.inventoryTick` will ignore subsequent updates for the same
player and item type during that tick. Whether the state opens or closes will
be based on whether either hand contains that item type, rather than on the
particular inventory stack whose callback ran.

## Public State Lookup

The animation lookup will accept both the player UUID and the guide-book item:

```java
ItemGuideBook.getOrCreateState(UUID playerUuid, ItemGuideBook book)
```

A null UUID or null book will return the existing closed fallback state.
The old UUID-only lookup will be removed so consumers cannot accidentally
reintroduce cross-book state sharing.

`clearState(UUID)` will remove every cached state whose key belongs to the
specified player. The existing client logout event will continue calling this
method, so disconnect cleanup remains centralized.

## Consumers

HutosLib's `RenderItemGuideBook` will pass the rendered `ItemGuideBook` to the
new lookup.

Hemomancy's `DictationTableRenderer` will pass the guide-book item stored in
the table to the new lookup. Its table-specific behavior remains unchanged:
the book is always rendered open, while page-flip values are sampled from the
matching local-player/item-type state.

## Testing

Regression coverage will verify:

1. Two different guide-book item identities owned by one player resolve to
   different animation states.
2. Repeated updates for one player/item type in the same game tick advance
   the state only once.
3. Different item types can each update during the same game tick.
4. Clearing one player removes every item-type state belonging to that player
   without removing another player's states.

The test will exercise production state-cache and tick-guard behavior rather
than reproduce the algorithm in test-only code. Targeted tests will run first,
followed by the full HutosLib test suite and compilation of the Hemomancy
consumer against the updated local HutosLib API.

## Scope

The change is limited to guide-book animation state, its two known render
consumers, disconnect cleanup behavior, and regression tests. It will not
introduce per-stack animation state or alter the Book of Observances item
implementation.
