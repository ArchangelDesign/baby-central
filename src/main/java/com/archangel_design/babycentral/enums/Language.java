package com.archangel_design.babycentral.enums;

public enum Language {
    en("en");

    Language(final String shortcut) {
        this.shortcut = shortcut;
    }

    private final String shortcut;

    @Override
    public String toString() {
        return shortcut;
    }
}
