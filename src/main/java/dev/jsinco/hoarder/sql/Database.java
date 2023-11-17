package dev.jsinco.hoarder.sql;

import dev.jsinco.hoarder.objects.TreasureItem;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.List;

public abstract class Database {


    public Database() {

    }


    public abstract Connection getConnection();

    public TreasureItem getTreasureItem(String identifier) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM treasure_items WHERE identifier = " + identifier + ";");
            ResultSet resultSet = statement.executeQuery();

            return new TreasureItem(identifier, resultSet.getInt("weight"), (ItemStack) resultSet.getObject("itemStack"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<TreasureItem> getAllTreasureItems() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM treasure_items;");
            ResultSet resultSet = statement.executeQuery();

            return new TreasureItem(identifier, resultSet.getInt("weight"), (ItemStack) resultSet.getObject("itemStack"));
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
