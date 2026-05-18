package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.common.book.filter.EntryGatedBookFilter;
import com.vincenthuto.hutoslib.common.book.filter.IBookPageFilter;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;
import com.vincenthuto.hutoslib.common.book.knowledge.IBookKnowledge;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookDataTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookPlaceboReloadListener;
import com.vincenthuto.hutoslib.common.data.book.ChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.PageTemplate;
import com.vincenthuto.hutoslib.HutosLib;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ItemGuideBook extends Item {
	private static final Random RANDOM = new Random();
	private static final Map<UUID, BookAnimState> ANIM_STATES = new ConcurrentHashMap<>();
	private static final BookAnimState DEFAULT_STATE = new BookAnimState();
	private static final float OPEN_SPEED = 0.12F;
	private static final float CLOSE_SPEED = 0.16F;
	private static final int HAND_SWAP_OPEN_DELAY_TICKS = 4;

	private Identifier texture;
	private String bookPrefix;
	private Function<Player, Optional<IBookKnowledge>> knowledgeProvider =
			player -> Optional.of(BookKnowledgeProvider.get(player));
	private IBookPageFilter pageFilterOverride;

	public ItemGuideBook(Properties prop, Identifier loc) {
		super(prop);
		this.texture = loc;
	}

	public static BookAnimState getOrCreateState(UUID entityUuid) {
		if (entityUuid == null) {
			return DEFAULT_STATE;
		}
		return ANIM_STATES.computeIfAbsent(entityUuid, id -> new BookAnimState());
	}

	public static void clearState(UUID entityUuid) {
		if (entityUuid != null) {
			ANIM_STATES.remove(entityUuid);
		}
	}

	public BookCodeModel applyVisibilityFilters(BookCodeModel loadedBook, Player player) {
		IBookPageFilter filter = pageFilterOverride != null ? pageFilterOverride : loadedBook.getPageFilter();
		BookCodeModel filtered = filter.filter(loadedBook, player);
		IBookKnowledge knowledge = getKnowledgeProvider().apply(player).orElse(null);
		filtered = knowledge != null
				? EntryGatedBookFilter.INSTANCE.filter(filtered, knowledge)
				: EntryGatedBookFilter.INSTANCE.filter(filtered, player);
		filtered.setPageFilter(filter);
		filtered.setTheme(loadedBook.getTheme());
		return filtered;
	}

	public ItemGuideBook withBookPrefix(String prefix) {
		this.bookPrefix = prefix;
		return this;
	}

	public ItemGuideBook withKnowledgeProvider(Function<Player, Optional<IBookKnowledge>> provider) {
		this.knowledgeProvider = provider;
		return this;
	}

	public ItemGuideBook withPageFilter(IBookPageFilter filter) {
		this.pageFilterOverride = filter;
		return this;
	}

	public String getBookPrefix() {
		return bookPrefix;
	}

	public Function<Player, Optional<IBookKnowledge>> getKnowledgeProvider() {
		return knowledgeProvider;
	}

	public Identifier getTexture() {
		return texture;
	}

	public Set<Identifier> collectVisiblePageIds(Player player) {
		String prefix = bookPrefix == null || bookPrefix.isBlank() ? "guide" : bookPrefix.replaceAll("/+$", "");
		BookCodeModel loaded = BookPlaceboReloadListener.INSTANCE.getBookByTitle(HutosLib.rloc(prefix));
		if (loaded == null) {
			return Set.of();
		}
		BookCodeModel filtered = applyVisibilityFilters(loaded, player);
		Set<Identifier> ids = new HashSet<>();
		List<ChapterTemplate> chapters = filtered.getChapters();
		if (chapters == null) {
			return Set.of();
		}
		for (ChapterTemplate chapter : chapters) {
			if (chapter.getPages() == null) {
				continue;
			}
			for (BookDataTemplate page : chapter.getPages()) {
				if (page instanceof PageTemplate && page.getId() != null) {
					ids.add(page.getId());
				}
			}
		}
		return ids;
	}

	public void setTexture(Identifier texture) {
		this.texture = texture;
	}

	public static void clientTickAnimation(Player player) {
		if (player == null) {
			return;
		}

		BookAnimState state = getOrCreateState(player.getUUID());
		state.ticks++;

		ItemStack mainHand = player.getMainHandItem();
		ItemStack offHand = player.getOffhandItem();
		boolean holdingGuideBook = mainHand.getItem() instanceof ItemGuideBook || offHand.getItem() instanceof ItemGuideBook;
		if (holdingGuideBook) {
			if (!state.wasHeld) {
				state.close = 0.0F;
				state.heldTicks = 0;
				state.wasHeld = true;
			}
			state.heldTicks++;

			state.pageTurningSpeed = state.nextPageTurningSpeed;
			state.pageAngle = state.nextPageAngle;
			state.nextPageTurningSpeed += 0.1F;
			if (state.nextPageTurningSpeed < 0.5F || RANDOM.nextInt(40) == 0) {
				float previousFlipTarget = state.flipT;
				do {
					state.flipT += RANDOM.nextInt(4) - RANDOM.nextInt(4);
				} while (previousFlipTarget == state.flipT);
			}

			while (state.nextPageAngle >= (float) Math.PI) {
				state.nextPageAngle -= (float) Math.PI * 2F;
			}
			while (state.nextPageAngle < -(float) Math.PI) {
				state.nextPageAngle += (float) Math.PI * 2F;
			}
			while (state.tRot >= (float) Math.PI) {
				state.tRot -= (float) Math.PI * 2F;
			}
			while (state.tRot < -(float) Math.PI) {
				state.tRot += (float) Math.PI * 2F;
			}

			float angleDelta;
			for (angleDelta = state.tRot - state.nextPageAngle; angleDelta >= (float) Math.PI;
				 angleDelta -= (float) Math.PI * 2F) {
			}
			while (angleDelta < -(float) Math.PI) {
				angleDelta += (float) Math.PI * 2F;
			}

			state.nextPageAngle += angleDelta * 0.4F;
			state.nextPageTurningSpeed = Mth.clamp(state.nextPageTurningSpeed, 0.0F, 1.0F);
			state.oFlip = state.flip;
			float flipDelta = (state.flipT - state.flip) * 0.4F;
			flipDelta = Mth.clamp(flipDelta, -0.2F, 0.2F);
			state.flipA += (flipDelta - state.flipA) * 0.9F;
			state.flip += state.flipA;
			if (state.heldTicks > HAND_SWAP_OPEN_DELAY_TICKS) {
				state.close = Mth.clamp(state.close + OPEN_SPEED, 0.0F, 1.0F);
			}
		} else {
			state.wasHeld = false;
			state.heldTicks = 0;
			state.close = Mth.clamp(state.close - CLOSE_SPEED, 0.0F, 1.0F);
		}
	}
}
