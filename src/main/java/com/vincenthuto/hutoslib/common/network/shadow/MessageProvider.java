package com.vincenthuto.hutoslib.common.network.shadow;

import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * A Message Provider encapsulates the default components that make up a packet.
 *
 * @param <T> The type of the message.
 */
public interface MessageProvider<T> {

@SuppressWarnings("unchecked")
default Class<T> getMsgClass() {
return (Class<T>) this.getClass();
}

/**
 * Writes the message to the byte buffer.
 */
public abstract void write(T msg, FriendlyByteBuf buf);

/**
 * Reads the message from a byte buffer.
 */
public abstract T read(FriendlyByteBuf buf);

/**
 * Handle the message.
 */
public abstract void handle(T msg, IPayloadContext ctx);
}
