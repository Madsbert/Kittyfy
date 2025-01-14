package org.example.kittyfy;

import javafx.scene.control.Button;

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
    public Button playlistButton;

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
    public static int createPlaylist(Playlist playlist) throws Exception {
        String sql = "INSERT INTO dbo.TblPlaylist (fldPlaylistName,fldLastPlayed) VALUES (?, ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, playlist.getName());
        pstmt.setLong(2, playlist.getLastPlayed());

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Playlist created successfully.");
            sql = "SELECT * FROM dbo.TblPlaylist WHERE fldPlaylistName = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playlist.getName());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("fldPlaylistId");
            }
        } else {
            System.out.println("Failed to update the playlist.");
        }
        return -1;
    }

    /**
     * Updates a playlist in the database.
     * @param playlist
     * @throws Exception
     */
    public void updatePlaylist(Playlist playlist) throws Exception {
        String sql = "UPDATE dbo.TblPlaylist SET " +
                "fldPlaylistName = ?," +
                "fldLastPlayed = ? WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, playlist.getName());
        pstmt.setInt(2, playlist.getPlaylistId());
        pstmt.setLong(3, playlist.getLastPlayed());
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
        String sql = "SELECT * from dbo.TblPlaylist WHERE fldPlaylistID = ?";
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
        String sql = "DELETE FROM dbo.TblPlaylist WHERE fldPlaylistID = ?";
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
        String sql = "Select * from dbo.TblPlaylist";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet resultSet = pstmt.executeQuery();
        ArrayList<Playlist> allPlaylists = new ArrayList<>();

        while (resultSet.next()) {
           String playlistName = resultSet.getString("fldPlaylistName").trim();
           long lastPlayed = resultSet.getLong("fldLastPlayed");
           int playlistID = resultSet.getInt("fldPlaylistID");

            Playlist newPlaylist = new Playlist(playlistName, BridgePlaylistSong.getAllSongsInPlaylist(playlistID));
            newPlaylist.setLastPlayed(lastPlayed);
            newPlaylist.setPlaylistId(playlistID);
            allPlaylists.add(newPlaylist);
        }
        return allPlaylists;
    }

    /**
     * Gets the playlist index of a wanted song.
     * @param song which index is wanted.
     * @return Index of song in playlist Arraylist. Returns -1 if song is not in the playlist.
     */
    public int getSongIndex(Song song)
    {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getFilePath().equals(song.getFilePath())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the playlist index of a wanted song.
     * @param songTitle Title of the song wanted.
     * @return Index of song in playlist Arraylist. Returns -1 if song is not in the playlist.
     */
    public int getSongIndex(String songTitle)
    {
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getTitle().equals(songTitle)) {
                return i;
            }
        }
        return -1;
    }

}
