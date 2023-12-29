package com.kyubin.chess.object.xy;

public class XY {
    public int x,y;

    public XY(int x,int y){
        this.x=x;
        this.y=y;
    }

    @Override
    public String toString() {
        return x+" "+y;
    }

    public boolean equals(XY obj) {
        return obj.x==x&&obj.y==y;
    }
}
