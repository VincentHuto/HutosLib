package com.vincenthuto.hutoslib.common.network;

import java.util.UUID;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledge;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledgeProvider;

import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Sends a full {@link BookKnowledge} snapshot from the server to the owning
 * client. Each mod that stores its own {@code AttachmentType<? extends
 * BookKnowledge>} should extend this packet (or send it directly) whenever
 * the player's knowledge changes.
 *
 * <p>The payload is: player UUID (16 bytes) + raw NBT blob of
 * {@link BookKnowledge#serializeNBT}.
 *
 * <p>This packet is registered in {@link HLPacketHandler}. On the client side the
 * handler applies the received snapshot to the local player's
 * {@link com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes#BOOK_KNOWLEDGE} attachment.
 * Mods with their own {@code AttachmentType<? extends BookKnowledge>} can send this packet from
 * the server and apply it themselves via
 * {@link #applyTo(BookKnowledge, net.minecraft.core.HolderLookup.Provider)}.
 *
 * <p>To send from a server handler:
 * {@code PacketDistributor.sendToPlayer(serverPlayer, new PacketSyncBookKnowledge(uuid, knowledge, registries))}.
 */
public class PacketSyncBookKnowledge implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketSyncBookKnowledge> TYPE =
            new CustomPacketPayload.Type<>(HutosLib.rloc("packet_sync_book_knowledge"));

    public static final StreamCodec<FriendlyByteBuf, PacketSyncBookKnowledge> CODEC =
            StreamCodec.of((buf, msg) -> msg.encode(buf), PacketSyncBookKnowledge::new);

    private final UUID playerUuid;
    private final CompoundTag data;

    /** Decode constructor (called by the CODEC on the receiving side). */
    public PacketSyncBookKnowledge(FriendlyByteBuf buf) {
        this.playerUuid = buf.readUUID();
        try {
            this.data = NbtIo.read(new ByteBufInputStream(buf), NbtAccounter.unlimitedHeap());
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to read BookKnowledge NBT from network buffer", e);
        }
    }

    /**
     * Encode constructor.
     *
     * @param playerUuid the UUID of the player whose knowledge is being synced
     * @param knowledge  the knowledge object to serialize
     * @param registries the server's holder-lookup provider (required by
     *                   {@link net.neoforged.neoforge.common.util.INBTSerializable})
     */
    public PacketSyncBookKnowledge(UUID playerUuid, BookKnowledge knowledge,
            net.minecraft.core.HolderLookup.Provider registries) {
        this.playerUuid = playerUuid;
        this.data = knowledge.serializeNBT(registries);
    }

    private void encode(FriendlyByteBuf buf) {
        buf.writeUUID(playerUuid);
        try {
            NbtIo.write(data, new ByteBufOutputStream(buf));
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to write BookKnowledge NBT to network buffer", e);
        }
    }

    /** The UUID of the player whose knowledge this packet describes. */
    public UUID getPlayerUuid() {
        return playerUuid;
    }

    /**
     * Client-side handler registered in {@link HLPacketHandler}.
     * Applies the snapshot to the local player's {@link HLAttachmentTypes#BOOK_KNOWLEDGE}
     * attachment when the packet's UUID matches the local player.
     */
    public static void handle(PacketSyncBookKnowledge msg, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            var player = ctx.player();
            if (player != null && player.getUUID().equals(msg.playerUuid)) {
                BookKnowledge knowledge = BookKnowledgeProvider.get(player);
                msg.applyTo(knowledge, player.level().registryAccess());
            }
        });
    }

    /**
     * Applies the received NBT to {@code target} using the supplied registries.
     * Typical usage inside the client-side handler:
     * <pre>{@code
     * msg.applyTo(knowledge, ctx.player().level().registryAccess());
     * }</pre>
     */
    public void applyTo(BookKnowledge target, HolderLookup.Provider registries) {
        target.deserializeNBT(registries, data);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
