package com.vincenthuto.hutoslib.client.book;

/** A turn keeps the old content until its midpoint and never acknowledges read dwell. */
public final class BookTurn {
    private int elapsed, duration;
    private Runnable pending;
    public boolean active() { return duration > 0 && elapsed < duration; }
    public void start(int ticks, Runnable swap) {
        if (active()) return;
        if (ticks <= 0) { swap.run(); return; }
        elapsed = 0;
        duration = ticks;
        pending = swap;
    }
    public void tick() {
        if (!active()) return;
        elapsed++;
        if (pending != null && elapsed >= (duration + 1) / 2) {
            Runnable swap = pending;
            pending = null;
            swap.run();
        }
    }
    public float progress(float partialTick) {
        if (!active()) return 1;
        float t = Math.clamp((elapsed + partialTick) / duration, 0, 1);
        return t * t * (3 - 2 * t);
    }
    public void cancel() { pending = null; duration = 0; elapsed = 0; }
}
