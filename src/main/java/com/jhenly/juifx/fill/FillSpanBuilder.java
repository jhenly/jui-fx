package com.jhenly.juifx.fill;

import com.jhenly.juifx.fill.FillSpan.BorderStrokePosition;

import javafx.scene.paint.Color;

public class FillSpanBuilder {
    private Color from, to;
    int fromIndex, toIndex;
    int fromBsPos, toBsPos;
    
    public FillSpanBuilder() {
        from = to = null;
        fromIndex = toIndex = -1;
        fromBsPos = toBsPos = 0;
    }
    
    public FillSpanBuilder setFromIndex(int index) {
        return this;
    }
    
    public FillSpanBuilder setToIndex(int index) {
        return this;
    }
    
    public FillSpanBuilder setFromBorderStrokePosition(BorderStrokePosition strokePos) {
        return this;
    }
    
    public FillSpanBuilder setToBorderStrokePosition(BorderStrokePosition strokePos) {
        return this;
    }
}
