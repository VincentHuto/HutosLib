# Field notes and book stands

The Dictation Table is a HutosLib block that accepts one `ItemGuideBook`. Normal use reads the placed book, sneak-use returns it, and breaking the table drops it. Reading retains the item's visibility filters and knowledge provider. A reader opened from a stand closes if the player moves more than eight blocks away or the book is removed. Cross-book links still require the destination book in inventory.

Register a `FieldNotes.Provider` under a unique resource ID during enqueued common setup to opt into the inventory field-notes badge. `available(player)` should require the mod's discovery books to be loaded. With no available providers the badge is absent. `pending` and `tooltip` summarize the mod's synchronized knowledge; HutosLib does not duplicate its persistence or packets.

`accepts(book)` and `acceptsDesk(block)` scope dictation to the correct book and station. `canDictate` must be a read-only, client/server-safe check for applicable unwritten notes. It must not test affordability: the server-side `dictate` callback validates and charges costs, changes knowledge atomically, and synchronizes it. A failed payment should keep the notes pending. No applicable notes means the desk reads the book instead. Hemomancy registers its existing Liber knowledge and only accepts the Harbinger Escritoire.

Desk subclasses retain the shared interaction/storage contract and can customize book height, orientation, and the pending glow color. The default table has no glow. Registered subclasses need their own block entity type, returning the shared `DictationTableBlockEntity` with that type, and the shared renderer registration. The old Items container NBT is preserved.
