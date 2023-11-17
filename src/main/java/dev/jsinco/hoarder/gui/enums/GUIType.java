package dev.jsinco.hoarder.gui.enums;

import dev.jsinco.hoarder.gui.PaginatedGUI;

import java.util.List;

public enum GUIType {

    MAIN(List.of("active_item", "clock"), null),
    STATS(null, PaginatedGUI.PageItem.STATISTIC),
    TREASURE(null, PaginatedGUI.PageItem.TREASURE),
    CLAIM_PRIZE(null,PaginatedGUI.PageItem.CLAIM_PRIZE);

    GUIType(List<String> guiSpecificItems, PaginatedGUI.PageItem pageItem) {
    }
}
