package com.refactoredsolarpanels.menu;

public final class SyncedData {
    private SyncedData() {
    }

    public static int low(int value) {
        return value & 0xFFFF;
    }

    public static int high(int value) {
        return (value >>> 16) & 0xFFFF;
    }

    public static int combine(int low, int high) {
        return ((high & 0xFFFF) << 16) | (low & 0xFFFF);
    }
}
