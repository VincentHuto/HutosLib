package com.vincenthuto.hutoslib.common.event;

public record Identifier(String modId, String name) {
    public Identifier {
        if (modId == null || name == null) {
            throw new NullPointerException("modId or identifier is null");
        }
    }

    public String get() {
        return modId + ":" + name;
    }
}