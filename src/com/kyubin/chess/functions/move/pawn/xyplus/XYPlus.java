package com.kyubin.chess.functions.move.pawn.xyplus;

import com.kyubin.chess.object.xy.XY;

public class XYPlus {
    public XY xy;
    public boolean is_en_passant;

    public XYPlus(XY xy, boolean is_en_passant) {
        this.xy = xy;
        this.is_en_passant = is_en_passant;
    }
}
