package com.nerounero.afk;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class Afk extends JavaPlugin implements CommandExecutor,Listener {

    private final Set<Player> afkPlayers = new HashSet<>();
    private final Map<Player, Location> lastLocations = new HashMap<>();

    @Override
    public void onEnable() {
        this.getCommand("afk").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            toggleAFK(player);
            return true;
        }
        return false;
    }

    private void toggleAFK(Player player) {
        if (afkPlayers.contains(player)) {
            afkPlayers.remove(player);
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + "は放置から戻りました。");
            player.setPlayerListName(player.getName());
        } else {
            afkPlayers.add(player);
            Bukkit.broadcastMessage(ChatColor.YELLOW + player.getName() + "は放置中です。");
            player.setPlayerListName(player.getName() + ChatColor.GRAY + " [放置中]");
            lastLocations.put(player, player.getLocation());
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (afkPlayers.contains(player)) {
            Location lastLocation = lastLocations.get(player);
            if (lastLocation != null && lastLocation.distance(player.getLocation()) >= 5) {
                sendAFKReminder(player);
                lastLocations.put(player, player.getLocation());
            }
        }
    }

    private void sendAFKReminder(Player player) {
        TextComponent message = new TextComponent(ChatColor.YELLOW + "戻りましたか？解除しましょう!! ");
        TextComponent button = new TextComponent(ChatColor.GREEN + "[解除]");
        button.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/afk"));
        message.addExtra(button);
        player.spigot().sendMessage(message);
    }
}
