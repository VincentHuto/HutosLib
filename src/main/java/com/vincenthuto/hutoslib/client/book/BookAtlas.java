package com.vincenthuto.hutoslib.client.book;

/** Whole atlas regions; state cells are selected by the renderer inside these bounds. */
public enum BookAtlas {
    N(0, 0, 72, 10), B(80, 0, 40, 20), Mc(128, 0, 40, 8), Mr(128, 8, 40, 8), Bd(168, 0, 5, 5),
    Tf(0, 24, 72, 20), Th(80, 24, 96, 22), Rb(184, 24, 7, 48), Rg(192, 24, 4, 6),
    S(0, 48, 52, 26), F(64, 48, 16, 14), Bt(88, 48, 48, 14), P(144, 48, 16, 16), Fr(168, 48, 16, 16),
    Dv(0, 80, 5, 1), Ld(8, 80, 3, 1), W(16, 80, 8, 8), Pp(32, 80, 16, 10), Ch(56, 80, 12, 12), Sp(72, 80, 24, 24);

    public final int u, v, width, height;
    BookAtlas(int u, int v, int width, int height) {
        this.u = u;
        this.v = v;
        this.width = width;
        this.height = height;
    }
}
