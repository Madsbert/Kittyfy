package org.example.kittyfy;

import javafx.scene.control.Button;
import org.jetbrains.annotations.NotNull;

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
    public String folderPath;

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

    public Playlist(String name, ArrayList<Song> songs, String selectedPicFolderFilepath) {
        this.name = name;
        this.songs = songs;
        this.folderPath = selectedPicFolderFilepath;
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

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public String getFolderPath() {
        return folderPath;
    }
    public void setFolderPath(String folderPath) {
        this.folderPath = folderPath;
    }

    public void addSong(Song song) {
        songs.add(song);
    }

    /**
     * ???
     * @param playlistName
     * @return
     */
    public static String getFolderPath(String playlistName) {
        String sql = "SELECT fldPictureFilepath FROM dbo.TblPlaylist Where fldPlaylistName = ?";
        Connection conn = DB.getConnection();
        try
        {
            ResultSet resultSet;
            PreparedStatement pstmt = conn.prepareStatement(sql);
            {
                pstmt.setString(1, playlistName);
                resultSet = pstmt.executeQuery();
            }
            if (resultSet.next()) {
                System.out.println(resultSet.getString("fldPictureFilepath"));
                return resultSet.getString("fldPictureFilepath").trim();

            } else {
                return null;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get folder path");
            return null;
        }
    }

    /**
     * Creates a playlist in the database.
     * @param playlist playlist object to create in the database.
     */
    public static int createPlaylist(Playlist playlist) {
        String sql = "INSERT INTO dbo.TblPlaylist (fldPlaylistName,fldLastPlayed,fldPictureFilepath) VALUES (?, ?, ?)";
        Connection conn = DB.getConnection();
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playlist.getName());
            pstmt.setLong(2, playlist.getLastPlayed());
            pstmt.setString(3,playlist.getFolderPath());

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
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to create playlist.");
        }
        return -1;
    }

    /**
     * Updates a playlist in the database.
     * @param playlist updated instance of the playlist.
     */
    public static void updatePlaylist(Playlist playlist) {
        String sql = "UPDATE dbo.TblPlaylist SET " +
                "fldPlaylistName = ?," +
                "fldLastPlayed = ?," +
                "fldPictureFilepath = ?  WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, playlist.getName());
            pstmt.setLong(2, playlist.getLastPlayed());
            pstmt.setString(3, playlist.getFolderPath());
            pstmt.setInt(4, playlist.getPlaylistId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Playlist updated successfully.");
            } else {
                System.out.println("Failed to update the playlist.");
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to update playlist.");
        }
    }

    /**
     * Reads a playlist from the database and creates a new playlist object.
     * @param playlistId ID of the playlist to read from the database.
     * @return Playlist object made from DB Data.
     */
    public static Playlist getPlaylist(int playlistId) {
        String sql = "SELECT * from dbo.TblPlaylist WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        try {
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
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get playlist.");
        }
        return null;
    }

    /**
     * Deletes playlist from the database.
     * @param playlist playlist instance to delete from the database.
     */
    public static void deletePlaylist(@NotNull Playlist playlist) {
        String sql = "DELETE FROM dbo.TblPlaylist WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlist.getPlaylistId());

            BridgePlaylistSong.deleteSongsInPlaylist(playlist);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Playlist deleted successfully.");
            }else {
                System.out.println("Failed to delete the playlist.");
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to delete the playlist.");
        }

    }

    /**
     * Gets all playlists from playlist table.
     * @return ArrayList with all playlists from the database.
     */
    public static ArrayList<Playlist> getAllPlaylists() {
        String sql = "Select * from dbo.TblPlaylist";
        Connection conn = DB.getConnection();
        try {
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
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get all playlists.");
        }
        return null;
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

}