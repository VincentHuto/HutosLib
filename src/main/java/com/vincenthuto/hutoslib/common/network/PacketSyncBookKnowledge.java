package com.vincenthuto.hutoslib.common.network;

import java.util.UUID;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.book.knowledge.BookKnowledge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sends a full {@link BookKnowledge} snapshot from the server to the owning
 * client. Each mod that stores its own {@code AttachmentType<? extends
 * BookKnowledge>} should extend this packet (or send it directly) whenever
 * the player's knowledge changes.
 *
 * <p>The payload is: player UUID (16 bytes) + raw NBT blob of
 * {@link BookKnowledge#serializeNBT}.
 *
 * <p>This packet is registered in {@link HLPacketHandler}; mods that need it
 * can send it via
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
            this.data = NbtIo.read(buf, NbtAccounter.unlimitedHeap());
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
            NbtIo.write(data, buf);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to write BookKnowledge NBT to network buffer", e);
        }
    }

    /** The UUID of the player whose knowledge this packet describes. */
    public UUID getPlayerUuid() {
        return playerUuid;
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
