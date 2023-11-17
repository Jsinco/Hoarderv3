package dev.jsinco.hoarder.sql;

import dev.jsinco.hoarder.Hoarder;

import java.sql.Connection;

public class MySQL extends Database {

    private final String address;
    private final String username;
    private final String password;

    public MySQL(Hoarder plugin) {
        super(plugin);
        this.address = "jdbc:mysql://" + plugin.getConfig().getString("storage.address");
        this.username = plugin.getConfig().getString("storage.username");
        this.password = plugin.getConfig().getString("storage.password");
    }

    @Override
    public Connection getConnection() {
        return null;
    }


}
