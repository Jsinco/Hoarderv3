package dev.jsinco.hoarder.gui;

public enum GUIType {

    MAIN("guis/main.yml"),
    STATS("guis/stats.yml"),
    TREASURE("guis/treasure.yml"),
    CLAIM_TREASURE("guis/claim_treasure.yml");

    private final String filePath;

    GUIType(String filePath) {
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
