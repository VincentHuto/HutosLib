
package com.vincenthuto.hutoslib.common.network;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import com.vincenthuto.hutoslib.common.data.book.BookChapterTemplate;
import com.vincenthuto.hutoslib.common.data.book.BookCodeModel;
import com.vincenthuto.hutoslib.common.data.book.BookManager;
import com.vincenthuto.hutoslib.common.data.book.BookPageTemplate;
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
			buf.writeUtf(b.getTemplate().getTitle());
			buf.writeUtf(b.getTemplate().getSubtitle());
			buf.writeUtf(b.getTemplate().getCoverLoc());
			buf.writeUtf(b.getTemplate().getText());
			buf.writeUtf(b.getTemplate().getIcon());

			// Write chapter size
			buf.writeInt(b.getChapters().size());

			// Write chapter jsons
			for (BookChapterTemplate chapter : b.getChapters()) {
				buf.writeUtf(chapter.getColor());
				buf.writeUtf(chapter.getTitle());
				buf.writeUtf(chapter.getSubtitle());
				buf.writeUtf(chapter.getIcon());

				// Write Page size
				buf.writeInt(chapter.getPages().size());

				// Write page jsons
				for (BookPageTemplate page : chapter.getPages()) {
					buf.writeInt(page.getPage());
					buf.writeUtf(page.getTitle());
					buf.writeUtf(page.getSubtitle());
					buf.writeUtf(page.getText());
					buf.writeUtf(page.getIcon());
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

				String bookTitle = buf.readUtf();
				String bookSubtitle = buf.readUtf();
				String bookCoverLoc = buf.readUtf();
				String bookText = buf.readUtf();
				String bookIcon = buf.readUtf();

				BookTemplate bookTemp = new BookTemplate(bookTitle, bookSubtitle, bookCoverLoc, bookText, bookIcon);

				int chapterCount = buf.readInt();

				List<BookChapterTemplate> chapters = new ArrayList<BookChapterTemplate>();
				for (int j = 0; j < chapterCount; j++) {
					String chapterColor = buf.readUtf();
					String chapterTitle = buf.readUtf();
					String chapterSubtitle = buf.readUtf();
					String chapterIcon = buf.readUtf();

					int pageCount = buf.readInt();
					List<BookPageTemplate> pages = new ArrayList<BookPageTemplate>();
					for (int k = 0; k < pageCount; k++) {
						int pageNum = buf.readInt();
						String pageTitle = buf.readUtf();
						String pageSubtitle = buf.readUtf();
						String pageText = buf.readUtf();
						String pageIcon = buf.readUtf();

						BookPageTemplate pageTemp = new BookPageTemplate(pageNum, pageTitle, pageSubtitle, pageText,
								pageIcon);
						pages.add(pageTemp);
					}
					BookChapterTemplate chapterTemp = new BookChapterTemplate(chapterColor, chapterTitle,
							chapterSubtitle, chapterIcon, pages);

					chapters.add(chapterTemp);
				}
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
