package dev.jsinco.hoarder.storage.sql;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import dev.jsinco.hoarder.Hoarder;
import dev.jsinco.hoarder.manager.FileManager;
import dev.jsinco.hoarder.objects.HoarderPlayer;
import dev.jsinco.hoarder.objects.TreasureItem;
import dev.jsinco.hoarder.storage.DataManager;
import dev.jsinco.hoarder.utilities.BukkitSerialization;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;

public abstract class Database implements DataManager {

    private String prefix;
    private boolean usingSQLite = false; // SQLite changes some syntax. TODO: Better method for this? I'm not that good with SQL at the time of writing this
    private static final Hoarder plugin = Hoarder.getInstance();

    public Database() {}


    public abstract Connection getConnection();

    protected void initializeDatabase(boolean usingSQLite) {
        this.prefix = plugin.getConfig().getString("storage.table_prefix");

        List<String> initStatements = new ArrayList<>(List.of(
                "USE " + plugin.getConfig().getString("storage.database") + ";",
                "CREATE TABLE IF NOT EXISTS " + prefix + "data (event VARCHAR(500) PRIMARY KEY, endtime LONG, material VARCHAR(500), sellprice DECIMAL(15, 2));",
                "CREATE TABLE IF NOT EXISTS " + prefix + "treasure_items (identifier VARCHAR(3072) PRIMARY KEY, weight INT, itemstack VARCHAR(3072));",
                "CREATE TABLE IF NOT EXISTS " + prefix + "players (uuid VARCHAR(36) PRIMARY KEY, points INT NOT NULL DEFAULT 0, claimabletreasures INT NOT NULL DEFAULT 0);",
                "CREATE TABLE IF NOT EXISTS " + prefix + "cache (uuid VARCHAR(36) PRIMARY KEY, position INT);"
        ));

        if (usingSQLite) {
            initStatements.remove(0);
            this.usingSQLite = true;
        }
        try {
            for (String statement : initStatements) {
                PreparedStatement preparedStatement = getConnection().prepareStatement(statement);
                preparedStatement.execute();
                preparedStatement.close();
            }

        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize database", e);
        }
    }


    // Event data


    @Override
    public void setEventEndTime(long time) {
        try {
            PreparedStatement statement;
            if (!usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "data (event, endtime) VALUES (?, ?) ON DUPLICATE KEY UPDATE endtime = VALUES(endtime);");
                statement.setString(1, "main");
                statement.setLong(2, time);
            } else {
                statement = getConnection().prepareStatement("UPDATE " + prefix + "data SET endtime = ?;");
                statement.setLong(1, time);
            }


            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set event end time in database", e);
        }
    }


    @Override
    public long getEventEndTime() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix +"data WHERE event = ?;");
            statement.setString(1, "main");

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
            PreparedStatement statement;
            if (!usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "data (event, material) VALUES (?, ?) ON DUPLICATE KEY UPDATE material = VALUES(material);");
            } else {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "data (event, material) VALUES (?, ?);");
            }
            statement.setString(1, "main");
            statement.setString(2, material.name());

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
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "data WHERE event = ?;");
            statement.setString(1, "main");
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
            PreparedStatement statement;
            if (!usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "data (event, sellprice) VALUES (?, ?) ON DUPLICATE KEY UPDATE sellprice = VALUES(sellprice);");
                statement.setString(1, "main");
                statement.setDouble(2, price);
            } else {
                statement = getConnection().prepareStatement("UPDATE " + prefix + "data SET sellprice = ?;");
                statement.setDouble(1, price);
            }


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
            PreparedStatement statement;
            if (!usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = points + VALUES(points);");
                statement.setInt(2, amount);
            } else {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "players (uuid, points, claimabletreasures) VALUES (?, COALESCE((SELECT points FROM " + prefix + "players WHERE uuid = ?), 0) + ?, COALESCE((SELECT claimabletreasures FROM " + prefix + "players WHERE uuid = ?), 0));");
                statement.setString(2, uuid);
                statement.setInt(3, amount);
                statement.setString(4, uuid);
            }
            statement.setString(1, uuid);



            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add points to database", e);
        }
    }

    @Override
    public void removePoints(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement;

            if (usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = points - VALUES(points);");
                statement.setInt(2, amount);
            } else {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "players (uuid, points, claimabletreasures) VALUES (?, COALESCE((SELECT points FROM " + prefix + "players WHERE uuid = ?), 0) - ?, COALESCE((SELECT claimabletreasures FROM " + prefix + "players WHERE uuid = ?), 0));");
                statement.setString(2, uuid);
                statement.setInt(3, amount);
                statement.setString(4, uuid);
            }
            statement.setString(1, uuid);

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
    public void setPoints(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement;
            if (usingSQLite) {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "players (uuid, points, claimabletreasures) VALUES (?, ?, COALESCE((SELECT claimabletreasures FROM " + prefix + "players WHERE uuid = ?), 0));");
                statement.setString(3, uuid);
            } else {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, points) VALUES (?, ?) ON DUPLICATE KEY UPDATE points = VALUES(points);");
            }
            statement.setString(1, uuid);
            statement.setInt(2, amount);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set points in database", e);
        }
    }

    @Override
    public void addClaimableTreasures(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement;
            if (!usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, claimabletreasures) VALUES (?, ?) ON DUPLICATE KEY UPDATE claimabletreasures = claimabletreasures + VALUES(claimabletreasures);");
                statement.setInt(2, amount);
            } else {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "players (uuid, points, claimabletreasures) VALUES (?, COALESCE((SELECT points FROM " + prefix + "players WHERE uuid = ?), 0), COALESCE((SELECT claimabletreasures FROM " + prefix + "players WHERE uuid = ?), 0) + ?);");
                statement.setString(2, uuid);
                statement.setString(3, uuid);
                statement.setInt(4, amount);
            }
            statement.setString(1, uuid);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add claimable treasures to database", e);
        }
    }

    @Override
    public void removeClaimableTreasures(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement;
            if (!usingSQLite) {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, claimabletreasures) VALUES (?, ?) ON DUPLICATE KEY UPDATE claimabletreasures = claimabletreasures - VALUES(claimabletreasures);");
                statement.setInt(2, amount);
            } else {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "players (uuid, points, claimabletreasures) VALUES (?, COALESCE((SELECT points FROM " + prefix + "players WHERE uuid = ?), 0), COALESCE((SELECT claimabletreasures FROM " + prefix + "players WHERE uuid = ?), 0) - ?);");
                statement.setString(2, uuid);
                statement.setString(3, uuid);
                statement.setInt(4, amount);
            }
            statement.setString(1, uuid);

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



    public void setClaimableTreasures(@NotNull String uuid, int amount) {
        try {
            PreparedStatement statement;
            if (usingSQLite) {
                statement = getConnection().prepareStatement("INSERT OR REPLACE INTO " + prefix + "players (uuid, points, claimabletreasures) VALUES (?, COALESCE((SELECT points FROM " + prefix + "players WHERE uuid = ?), 0), ?);");
                statement.setString(2, uuid);
            } else {
                statement = getConnection().prepareStatement("INSERT INTO " + prefix + "players (uuid, claimabletreasures) VALUES (?, ?) ON DUPLICATE KEY UPDATE claimabletreasures = VALUES(claimabletreasures);");
            }
            statement.setString(1, uuid);
            statement.setInt(2, amount);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set claimable treasures in database", e);
        }
    }

    // Event necessities
    @Override
    public void resetAllPoints() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+prefix+"players;");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                PreparedStatement statement2 = getConnection().prepareStatement("UPDATE "+prefix+"players SET points = 0 WHERE uuid = ?;");
                statement2.setString(1, resultSet.getString("uuid"));
                statement2.executeUpdate();
                statement2.close();
            }
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reset all points in database", e);
        }
    }

    @Override
    public @NotNull Map<String, Integer> getEventPlayers() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+prefix+"players;");
            ResultSet resultSet = statement.executeQuery();

            Map<String, Integer> eventPlayers = new HashMap<>();

            while (resultSet.next()) {
                int points = resultSet.getInt("points");
                if (points != 0) {
                    eventPlayers.put(resultSet.getString("uuid"), points);
                }
            }
            statement.close();
            return eventPlayers;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get event players from database", e);
        }
        return Collections.emptyMap();
    }



    @Override
    public @NotNull List<String> getAllHoarderPlayersUUIDS() {
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
    public void addTreasureItem(@NotNull String identifier, int weight, ItemStack itemStack) {
        //Gson gson = new Gson();
        //Type gsonType = new TypeToken<HashMap>(){}.getType();
        //String gsonString = gson.toJson(itemStack.serialize(),gsonType);
        String serialized = BukkitSerialization.itemStackToBase64(itemStack);

        try {
            PreparedStatement statement;
            if (usingSQLite) {
                statement = getConnection().prepareStatement("INSERT OR IGNORE INTO " + prefix +"treasure_items(identifier, weight, itemstack) VALUES (?, ?, ?);");
            } else {
                statement = getConnection().prepareStatement("INSERT IGNORE INTO " + prefix +"treasure_items(identifier, weight, itemstack) VALUES (?, ?, ?);");
            }
            statement.setString(1, identifier);
            statement.setInt(2, weight);
            statement.setString(3, serialized);

            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add treasure item to database", e);
        }
    }

    @Override
    public void modifyTreasureItem(String identifier, int newWeight, String newidentifier) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("UPDATE " + prefix + "treasure_items SET identifier = ?, weight = ? WHERE identifier = ?;");
            statement.setString(1, newidentifier);
            statement.setInt(2, newWeight);
            statement.setString(3, identifier);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to modify treasure item in database", e);
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


            //Gson gson = new Gson();
            if (!resultSet.next()) return null;

            String id = resultSet.getString("identifier");
            int weight = resultSet.getInt("weight");
            // ItemStack itemStack = ItemStack.deserialize(gson.fromJson(resultSet.getString("itemstack"), Map.class));
            ItemStack itemStack = BukkitSerialization.itemStackFromBase64(resultSet.getString("itemstack"));

            statement.close();
            return new TreasureItem(id, weight, itemStack);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get treasure item from database", e);
        }
        return null;
    }

    @NotNull
    @Override
    public List<TreasureItem> getAllTreasureItems() {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM "+prefix+"treasure_items;");
            ResultSet resultSet = statement.executeQuery();

            List<TreasureItem> treasureItems = new ArrayList<>();
            //Gson gson = new Gson();
            while (resultSet.next()) {
                String identifier = resultSet.getString("identifier");
                int weight = resultSet.getInt("weight");
                //ItemStack itemStack = ItemStack.deserialize(gson.fromJson(resultSet.getString("itemstack"), Map.class));
                ItemStack itemStack = BukkitSerialization.itemStackFromBase64(resultSet.getString("itemstack"));
                treasureItems.add(new TreasureItem(identifier, weight, itemStack));
            }
            statement.close();
            return treasureItems;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get all treasure items from database", e);
            return Collections.emptyList();
        }
    }

    @Override
    public void addMsgQueuedPlayer(@NotNull String uuid, int position) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("INSERT INTO " + prefix + "cache (uuid, position) VALUES (?, ?);");
            statement.setString(1, uuid);
            statement.setInt(2, position);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to add player to msg queue cache", e);
        }
    }

    @Override
    public void removeMsgQueuedPlayer(@NotNull String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("DELETE FROM " + prefix + "cache WHERE uuid = ?;");
            statement.setString(1, uuid);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to remove player from msg queue cache", e);
        }
    }


    @Override
    public boolean isMsgQueuedPlayer(@NotNull String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "cache WHERE uuid = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            boolean isMsgQueued = resultSet.next();
            statement.close();
            return isMsgQueued;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to check if player is in msg queue cache", e);
            return false;
        }
    }

    @Override
    public int getMsgQueuedPlayerPosition(@NotNull String uuid) {
        try {
            PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + prefix + "cache WHERE uuid = ?;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            int position = resultSet.getInt("position");
            statement.close();
            return position;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get player position in msg queue cache", e);
        }
        return -1;
    }

    // SQL/File

    @NotNull
    @Override
    public Connection getSQLConnection() {
        return getConnection();
    }

    @Override
    public void closeConnection() {
        try {
            getConnection().close();
            plugin.getLogger().log(Level.INFO, "Successfully closed connection to database");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to close connection to database", e);
        }
    }

    @NotNull
    @Override
    public FileManager getFile() {
        throw new UnsupportedOperationException("SQL does not support this method! It is meant for flatfile usage!");
    }

    @Override
    public void saveFile() {
        throw new UnsupportedOperationException("SQL does not support this method! It is meant for flatfile usage!");
    }


}
