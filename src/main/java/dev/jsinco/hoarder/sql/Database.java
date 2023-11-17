package dev.jsinco.hoarder.sql;

import dev.jsinco.hoarder.Hoarder;
import dev.jsinco.hoarder.objects.TreasureItem;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class Database {

    private final Hoarder plugin;

    public Database(Hoarder plugin) {
        this.plugin = plugin;
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

            List<TreasureItem> treasureItems = new ArrayList<>();

            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                ResultSet rs = getConnection().prepareStatement("SELECT * FROM treasure_items WHERE " + columnName + ";").executeQuery();
                treasureItems.add(new TreasureItem(rs.getString("identifier"), rs.getInt("weight"), (ItemStack) rs.getObject("itemStack")));
            }

            return treasureItems;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public long getHoarderEndTime() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM simple_data;");
            ResultSet resultSet = statement.executeQuery();

            return resultSet.getInt("hoarder_endtime");
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void initializeTables() {
        try {

        }
    }
}
