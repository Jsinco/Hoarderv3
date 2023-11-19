package dev.jsinco.hoarder.storage.sql;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.jsinco.hoarder.Hoarder;
import dev.jsinco.hoarder.objects.HoarderPlayer;
import dev.jsinco.hoarder.objects.TreasureItem;
import dev.jsinco.hoarder.storage.DataManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;

public abstract class Database implements DataManager {

    private String prefix;
    private static final Hoarder plugin = Hoarder.getInstance();

    public Database() {}


    public abstract Connection getConnection();

    protected void initializeDatabase(boolean usingSQLite) {
        this.prefix = plugin.getConfig().getString("storage.table_prefix");

        List<String> initStatements = new ArrayList<>(List.of(
                "USE " + plugin.getConfig().getString("storage.database") + ";",
                "CREATE TABLE IF NOT EXISTS " + prefix + "data (endtime LONG, material VARCHAR(500), sellprice DECIMAL(15, 2));",
                "CREATE TABLE IF NOT EXISTS " + prefix + "treasure_items (identifier VARCHAR(3072) PRIMARY KEY, weight INT, itemstack VARCHAR(3072));",
                "CREATE TABLE IF NOT EXISTS " + prefix + "players (uuid VARCHAR(36), points INT, claimabletreasures INT);"
        ));

        if (usingSQLite) initStatements.remove(0);
        try {
            for (String statement : initStatements) {
                PreparedStatement preparedStatement = getConnection().prepareStatement(statement);
                preparedStatement.execute();
                preparedStatement.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // Event data


    @Override
    public void setEventEndTime(long time) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + prefix + "data SET endtime = ?;");
            statement.setLong(1, time);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set event end time in database", e);
        }
    }


    @Override
    public long getEventEndTime() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix +"data WHERE endtime;");
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) return -1;

            long endTime = resultSet.getLong("endtime");
            statement.close();
            return endTime;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get event end time in database", e);
            return -1;
        }
    }

    @Override
    public void setEventMaterial(@NotNull Material material) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + prefix + "data SET material = ?;");
            statement.setString(1, material.name());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set event material in database", e);
        }
    }

    @NotNull
    @Override
    public Material getEventMaterial() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "data WHERE material;");
            ResultSet resultSet = statement.executeQuery();


            if (!resultSet.next()) return Material.AIR;

            Material material = Material.valueOf(resultSet.getString("material"));
            statement.close();
            return material;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get event material in database", e);
            return Material.AIR;
        }
    }

    @Override
    public void setEventSellPrice(double price) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + prefix + "data SET sellprice = ?;");
            statement.setDouble(1, price);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set event sell price in database", e);
        }
    }


    @Override
    public double getEventSellPrice() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix +"data WHERE sellprice;");
            ResultSet resultSet = statement.executeQuery();


            if (!resultSet.next()) return 0;

            double sellPrice = resultSet.getDouble("sellprice");
            statement.close();
            return sellPrice;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get event sell price in database", e);
            return 0;
        }
    }


    // Hoarder players


    @Override
    public void addPoints(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = points + VALUES(points)");
            statement.setString(1, uuid);
            statement.setInt(2, amount);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add points to database", e);
        }
    }

    @Override
    public void removePoints(@NotNull String uuid, int amount) {
        try { // TODO: if we remove points from a player that hasnt been added to DB yet what will happen with this SQL statement?
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = points - VALUES(points)");
            statement.setString(1, uuid);
            statement.setInt(2, amount);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove points from database", e);
        }
    }



    @Override
    public int getPoints(@NotNull String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix +"players WHERE uuid = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            if (!resultSet.next()) return 0;

            int points = resultSet.getInt("points");

            statement.close();
            return points;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get points from database", e);
            return 0;
        }
    }

    @Override
    public void addClaimableTreasures(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, claimabletreasures) VALUES (?, ?) ON DUPLICATE KEY UPDATE claimabletreasures = claimabletreasures + VALUES(claimabletreasures)");
            statement.setString(1, uuid);
            statement.setInt(2, amount);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add claimable treasures to database", e);
        }
    }

    @Override
    public void removeClaimableTreasures(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, claimabletreasures) VALUES (?, ?) ON DUPLICATE KEY UPDATE claimabletreasures = claimabletreasures - VALUES(claimabletreasures)");
            statement.setString(1, uuid);
            statement.setInt(2, amount);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove claimable treasures from database", e);
        }
    }

    @Override
    public int getClaimableTreasures(@NotNull String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "players WHERE uuid = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int ct = resultSet.getInt("claimabletreasures");
                statement.close();
                return ct;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get claimable treasures from database", e);
        }
        return 0;
    }

    @NotNull
    @Override
    public List<String> getAllHoarderPlayersUUIDS() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "players;");
            ResultSet resultSet = statement.executeQuery();

            List<String> hoarderPlayers = new ArrayList<>();

            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                PreparedStatement pS = getConnection().prepareStatement("SELECT * FROM " + prefix + "treasure_items WHERE " + columnName + ";");
                ResultSet rs = pS.executeQuery();


                if (rs.next()) {
                    hoarderPlayers.add(rs.getString("uuid"));
                }
                pS.close();
            }
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get all hoarder players from database", e);
        }
        return Collections.emptyList();
    }

    @NotNull
    @Override
    public List<HoarderPlayer> getAllHoarderPlayers() {
        List<HoarderPlayer> hoarderPlayers = new ArrayList<>();
        for (String uuid : getAllHoarderPlayersUUIDS()) {
            hoarderPlayers.add(new HoarderPlayer(uuid));
        }
        return hoarderPlayers;
    }

    // Treasure


    @Override
    public void addTreasureItem(@NotNull TreasureItem treasureItem) {
        addTreasureItem(treasureItem.getIdentifier(), treasureItem.getWeight(), treasureItem.getItemStack());
    }

    @Override
    public void addTreasureItem(String identifier, int weight, ItemStack itemStack) {
        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap>(){}.getType();
        String gsonString = gson.toJson(itemStack.serialize(),gsonType);

        try {
            PreparedStatement statement = getConnection()
                    .prepareStatement("INSERT INTO " + prefix +"treasure_items(identifier, weight, itemstack) VALUES (?, ?, ?);");
            statement.setString(1, identifier);
            statement.setInt(2, weight);
            statement.setString(3, gsonString);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add treasure item to database", e);
        }
    }

    @Override
    public void removeTreasureItem(@NotNull String identifier) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM " + prefix + "treasure_items WHERE identifier = ?");
            statement.setString(1, identifier);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove treasure item from database", e);
        }
    }

    @Nullable
    @Override
    public TreasureItem getTreasureItem(@NotNull String identifier) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "treasure_items WHERE identifier = ?;");
            statement.setString(1, identifier);
            ResultSet resultSet = statement.executeQuery();


            Gson gson = new Gson();
            if (!resultSet.next()) return null;

            String id = resultSet.getString("identifier");
            int weight = resultSet.getInt("weight");
            ItemStack itemStack = ItemStack.deserialize(gson.fromJson(resultSet.getString("itemstack"), Map.class));

            statement.close();
            return new TreasureItem(id, weight, itemStack);
        } catch (SQLException e) {
            e.printStackTrace();
            plugin.getLogger().log(Level.SEVERE, "Failed to get treasure item from database", e);
        }
        return null;
    }

    @Nullable
    @Override
    public List<TreasureItem> getAllTreasureItems() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "treasure_items;");
            ResultSet resultSet = statement.executeQuery();

            List<TreasureItem> treasureItems = new ArrayList<>();
            Gson gson = new Gson();

            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String columnName = resultSet.getMetaData().getColumnName(i);
                PreparedStatement pS = getConnection().prepareStatement("SELECT * FROM " + prefix +"treasure_items WHERE " + columnName + ";");
                ResultSet rs = pS.executeQuery();
                statement.close();

                String id = resultSet.getString("identifier");
                int weight = resultSet.getInt("weight");
                ItemStack itemStack = ItemStack.deserialize(gson.fromJson(resultSet.getString("itemstack"), Map.class));

                treasureItems.add(new TreasureItem(id, weight, itemStack));
            }
            statement.close();
            return treasureItems;
        } catch (SQLException e) {
            // plugin.getLogger().log(Level.SEVERE, "Failed to get all treasure items from database", e);
            return Collections.emptyList();
        }
    }



}
