
package com.vincenthuto.hutoslib.common.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.data.DataTemplate;
import com.vincenthuto.hutoslib.common.data.DataTemplateInit;
import com.vincenthuto.hutoslib.common.data.book.BookChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookManager;
import com.vincenthuto.hutoslib.common.data.book.BookTemplate;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class ForgeMessageReloadBookContents {

	List<BookCodeModel> books;

	public ForgeMessageReloadBookContents() {
	}

	public ForgeMessageReloadBookContents(List<BookCodeModel> books) {
		this.books = books;
	}

	public List<BookCodeModel> getBooks() {
		return books;
	}

	public void setBooks(List<BookCodeModel> books) {
		this.books = books;
	}

	public static void encode(ForgeMessageReloadBookContents msg, FriendlyByteBuf buf) {
		List<BookCodeModel> books = BookManager.books;
		buf.writeInt(books.size());
		for (BookCodeModel b : books) {
			// Write Book location
			buf.writeResourceLocation(b.getResourceLocation());
			// Write book json
			b.getTemplate().serializeToJson(buf);
			// Write chapter size
			buf.writeInt(b.getChapters().size());
			// Write chapter jsons
			for (BookChapterTemplate chapter : b.getChapters()) {
				chapter.serializeToJson(buf);
				// Write Page size
				buf.writeInt(chapter.getPages().size());
				// Write page jsons
				for (DataTemplate page : chapter.getPages()) {
					buf.writeUtf(page.getProcessor());
					page.serializeToJson(buf);
				}
			}
		}
	}

	public static ForgeMessageReloadBookContents decode(FriendlyByteBuf buf) {

		ForgeMessageReloadBookContents msg = new ForgeMessageReloadBookContents();
		try {
			List<BookCodeModel> decodedBooks = new ArrayList<BookCodeModel>();

			int bookCount = buf.readInt();
			for (int i = 0; i < bookCount; i++) {
				ResourceLocation loc = buf.readResourceLocation();
				BookTemplate bookTemp = new BookTemplate();
				bookTemp = bookTemp.deserializeFromJson(buf);
				int chapterCount = buf.readInt();
				List<BookChapterTemplate> chapters = new ArrayList<BookChapterTemplate>();
				for (int j = 0; j < chapterCount; j++) {
					BookChapterTemplate chapterTemp = new BookChapterTemplate();
					chapterTemp = chapterTemp.deserializeFromJson(buf);
					List<DataTemplate> pages = new ArrayList<DataTemplate>();
					int pageCount = buf.readInt();
					for (int k = 0; k < pageCount; k++) {
						String pk = buf.readUtf();
						String[] pksplit = pk.split(":");
						ResourceLocation rl = new ResourceLocation(pksplit[0], pksplit[1]);
						DataTemplateInit.DATA_TEMPLATES.get().getEntries()
								.forEach(e -> System.out.println(e.getValue().getProcessorKey()));
						DataTemplate dt = DataTemplateInit.DATA_TEMPLATES.get().getValue(rl);

						if (dt != null) {
							DataTemplate d = dt.deserializeFromJson(buf);
							pages.add(d);
						}
//						Collections.sort(pages,
//								(obj1, obj2) -> Integer.compare(obj1.getPageOrder(), obj2.getPageOrder()));
					}
					chapterTemp.setPages(pages);
					chapters.add(chapterTemp);
				}
				Collections.sort(chapters,
						(obj1, obj2) -> Integer.compare(obj1.getChapterOrder(), obj2.getChapterOrder()));

				BookCodeModel book = new BookCodeModel(loc, bookTemp, chapters);
				decodedBooks.add(book);

			}
			msg.setBooks(decodedBooks);

		} catch (IllegalArgumentException | IndexOutOfBoundsException e) {
			System.out.println(e);
			return msg;
		}

		return msg;
	}

	public static void handle(ForgeMessageReloadBookContents msg, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		Optional<?> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if (!clientWorld.isPresent()) {
			return;
		}
		BookManager.setBooks(msg.books);
		ctxSupplier.get().setPacketHandled(true);
	}

	public static void sendToAll() {
		HLPacketHandler.MAINCHANNEL.send(PacketDistributor.ALL.noArg(), new ForgeMessageReloadBookContents());
	}

}
