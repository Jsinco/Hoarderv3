package dev.jsinco.hoarder.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class HoarderSellEvent extends Event implements Cancellable {
    private Player seller;
    private int amountSold;
    private double price;
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public HoarderSellEvent(Player seller, int amountSold, double price){
        this.seller = seller;
        this.amountSold = amountSold;
        this.price = price;
        this.isCancelled = false;
    }

    public HoarderSellEvent(Player seller, int amountSold, int price) {
        this(seller, amountSold, (double) price);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public Player getSeller() {
        return seller;
    }

    public int getAmountSold() {
        return amountSold;
    }

    public double getPrice() {
        return price;
    }
}
