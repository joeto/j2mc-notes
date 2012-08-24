package to.joe.j2mc.notes;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import to.joe.j2mc.notes.util.Note;

import to.joe.j2mc.core.J2MC_Manager;

public class Manager {

    J2MC_Notes plugin;

    public Manager(J2MC_Notes notes) {
        this.plugin = notes;
    }

    public void AddNote(String sender, String target, String message, boolean admin) {
        try {
            PreparedStatement ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("INSERT INTO notes (`from`,`to`,`message`,`time`,`adminBusiness`) VALUES (?,?,?,?,?)");
            ps.setString(1, sender);
            ps.setString(2, target);
            ps.setString(3, message);
            ps.setTimestamp(4, new Timestamp(new Date().getTime()));
            ps.setBoolean(5, admin);
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Error recording a note: " + e.getMessage());
        }
    }

    public ArrayList<Note> grabNotes(String target) {
        final ArrayList<Note> notes = new ArrayList<Note>();
        PreparedStatement ps;
        try {
            ps = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("SELECT * FROM notes where received=0 and `to`=?");
            ps.setString(1, target);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                notes.add(new Note(rs.getString("from"), rs.getString("message"), new Date(rs.getTimestamp("time").getTime()), rs.getBoolean("adminBusiness")));
            }
            PreparedStatement prep = J2MC_Manager.getMySQL().getFreshPreparedStatementHotFromTheOven("UPDATE notes SET received=1 where `to`=?");
            prep.setString(1, target);
            prep.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Unable to load user notes from MySQL: " + e.getMessage());
        }
        return notes;
    }

}
