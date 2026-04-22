package com.vincenthuto.hutoslib.common.network.shadow;

import net.neoforged.neoforge.network.handling.IPayloadContext;

public class MessageHelper {

/**
 * Handles a packet by enqueuing work on the main thread.
 *
 * @param work The runnable to execute.
 * @param ctx  The payload context.
 */
public static void handlePacket(Runnable work, IPayloadContext ctx) {
ctx.enqueueWork(work);
}
}
