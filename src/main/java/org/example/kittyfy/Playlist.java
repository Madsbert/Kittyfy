package org.example.kittyfy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;

public class Playlist {

    private int playlistId;
    private String name;
    private long lastPlayed;
    private ArrayList<Song> songs;

    public long getLastPlayed() {
        return lastPlayed;
    }

    /**
     * Updates the lastPlayed time to the current epoch second.
     */
    public void updateLastPlayed() {
        setLastPlayed(Instant.now().getEpochSecond());
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public Playlist(String name, ArrayList<Song> songs) {
        this.name = name;
        this.songs = songs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Song> getSongs() {
        return songs;
    }

    public void setSongs(ArrayList<Song> songs) {
        this.songs = songs;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    /**
     * Creates a playlist in the database.
     * @param playlist
     * @throws Exception
     */
    public static void createPlaylist(Playlist playlist) throws Exception {
        String sql = "INSERT INTO dbo.tblPlaylist VALUES (?, ?, ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, playlist.getName());
        pstmt.setLong(2, playlist.getLastPlayed());
        pstmt.setInt(3, playlist.getPlaylistId());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Playlist created successfully.");
        } else {
            System.out.println("Failed to update the playlist.");
        }
    }

    /**
     * Updates a playlist in the database.
     * @param playlist
     * @throws Exception
     */
    public void updatePlaylist(Playlist playlist) throws Exception {
        String sql = "UPDATE dbo.tblPlaylist SET " +
                "fldPlaylistName = ?," +
                "fldLastPlayed = ? WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, playlist.getName());
        pstmt.setLong(2, playlist.getLastPlayed());
        pstmt.setInt(3, playlist.getPlaylistId());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Playlist updated successfully.");
        } else {
            System.out.println("Failed to update the playlist.");
        }
    }

    /**
     * Reads a playlist from the database and creates a new playlist object.
     * @param playlistId
     * @return Playlist object made from DB Data.
     * @throws Exception
     */
    public static Playlist getPlaylist(int playlistId) throws Exception {
        String sql = "SELECT * from dbo.tblPlaylist WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, playlistId);
        ResultSet resultSet = pstmt.executeQuery();

        String playlistName = "Not set Error";
        long lastPlayed = 0;
        int playlistID = 0;
        if (resultSet.next()) {
            playlistName = resultSet.getString("fldPlaylistName");
            lastPlayed = resultSet.getLong("fldLastPlayed");
            playlistID = resultSet.getInt("fldPlaylistID");
        }

        Playlist newPlaylist = new Playlist(playlistName, BridgePlaylistSong.getAllSongsInPlaylist(playlistID));
        newPlaylist.setLastPlayed(lastPlayed);
        newPlaylist.setPlaylistId(playlistID);

        return newPlaylist;
    }

    /**
     * Deletes playlist from the database.
     * @param playlist
     * @throws Exception
     */
    public static void deletePlaylist(Playlist playlist) throws Exception {
        String sql = "DELETE FROM dbo.tblPlaylist WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, playlist.getPlaylistId());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Playlist deleted successfully.");
        }else {
            System.out.println("Failed to delete the playlist.");
        }
    }

    /**
     * Gets all playlists from playlist table.
     * @return
     * @throws Exception
     */
    public static ArrayList<Playlist> getAllPlaylists() throws Exception {
        String sql = "Select * from dbo.tblPlaylist";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet resultSet = pstmt.executeQuery();
        ArrayList<Playlist> allPlaylists = new ArrayList<>();
        while (resultSet.next()) {
            String playlistName = "Not set Error";
            long lastPlayed = 0;
            int playlistID = 0;
            if (resultSet.next()) {
                playlistName = resultSet.getString("fldPlaylistName");
                lastPlayed = resultSet.getLong("fldLastPlayed");
                playlistID = resultSet.getInt("fldPlaylistID");

            }
            Playlist newPlaylist = new Playlist(playlistName, BridgePlaylistSong.getAllSongsInPlaylist(playlistID));
            newPlaylist.setLastPlayed(lastPlayed);
            newPlaylist.setPlaylistId(playlistID);
            allPlaylists.add(newPlaylist);
        }
        return allPlaylists;
    }

}
