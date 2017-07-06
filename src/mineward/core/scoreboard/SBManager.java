package mineward.core.scoreboard;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@SuppressWarnings("deprecation")
public class SBManager {

    private static HashMap<UUID, Scoreboard> boards = new HashMap<UUID, Scoreboard>();

    public static Scoreboard getScoreboard(Player p) {
        if (boards.containsKey(p.getUniqueId())) {
            return boards.get(p.getUniqueId());
        }
        return createNewScoreboard(p);
    }

    private static Scoreboard createNewScoreboard(Player p) {
        if (boards.containsKey(p.getUniqueId())) {
            return boards.get(p.getUniqueId());
        }
        Scoreboard board = Bukkit.getServer().getScoreboardManager()
                .getNewScoreboard();
        updateScoreboard(p, board);
        return board;
    }

    public static void setPrefix(Player p, String name, String prefix) {
        for (Player target : Bukkit.getOnlinePlayers()) {
            Team team = getTeam(target, name);
            team.setPrefix(ChatColor.translateAlternateColorCodes('&', prefix));
            team.addPlayer(p);
            updateScoreboard(p, team.getScoreboard());
        }
    }

    public static void removeBoard(Player p) {
        p.setScoreboard(Bukkit.getServer().getScoreboardManager()
                .getNewScoreboard());
        boards.remove(p.getUniqueId());
    }

    private static Team addTeam(Scoreboard board, String team) {
        return board.registerNewTeam(team);
    }

    public static Team getTeam(Player p, String team) {
        Scoreboard board = getScoreboard(p);
        if (board.getTeam(team) != null) {
            return board.getTeam(team);
        }
        return addTeam(board, team);
    }

    public static void addPlayer(Player p, Team team) {
        Scoreboard board = team.getScoreboard();
        team.addPlayer(p);
        updateScoreboard(p, board);
    }

    public static void removePlayer(Player p, Team team) {
        Scoreboard board = team.getScoreboard();
        team.removePlayer(p);
        updateScoreboard(p, board);
    }

    public static void updateScoreboard(Player p, Scoreboard board) {
        boards.put(p.getUniqueId(), board);
        p.setScoreboard(board);
    }

    public static void setupSidebar(Player p) {
        Scoreboard board = getScoreboard(p);
        if (board.getObjective(DisplaySlot.SIDEBAR) == null) {
            Objective obj = board.registerNewObjective(p.getName(), "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        updateScoreboard(p, board);
    }

    public static void updateLine(Player p, int line, String s) {
        Scoreboard board = getScoreboard(p);
        if (board.getObjective(DisplaySlot.SIDEBAR) == null)
            setupSidebar(p);
        for (String a : board.getEntries()) {
            Score c = board.getObjective(DisplaySlot.SIDEBAR).getScore(a);
            if (c.getScore() == line) {
                if (c.getEntry().equals(s))
                    return;
                board.resetScores(c.getEntry());
                board.getObjective(DisplaySlot.SIDEBAR).getScore(s)
                        .setScore(line);
                return;
            }
        }
        board.getObjective(DisplaySlot.SIDEBAR).getScore(s).setScore(line);
        updateScoreboard(p, board);
    }

    public static HashMap<UUID, Scoreboard> getScoreboards() {
        return boards;
    }

}
