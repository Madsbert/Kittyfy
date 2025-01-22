package org.example.kittyfy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * class for getting and setting songs in a playlist in Database
 */
public class BridgePlaylistSong {

    /**
     * Removes all songs from the playlist and adds them and new songs again.
     * @param playlist updated playlist instance.
     */
    public static void updateSongsInPlaylist(Playlist playlist) {
        deleteSongsInPlaylist(playlist);

        addSongsToPlaylist(playlist); // re adds all songs to the playlist.
    }

    /**
     * Deletes all songs from the playlist in the database.
     * @param playlist Playlist to remove all songs from.
     */
    public static void deleteSongsInPlaylist(Playlist playlist) {
        String sql = "Delete from dbo.TblPlaylistSong WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlist.getPlaylistId());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Cleared old playlist songs.");
            }
            else {
                System.out.println("Failed to delete old playlist songs.");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to delete old playlist songs.");
        }

    }

    /**
     * Adds all songs from the Playlist instance to the playlist song bridge table in the database.
     * @param playlist Playlist instance with songs to add
     */
    public static void addSongsToPlaylist(Playlist playlist){
        String sql = "INSERT INTO dbo.TblPlaylistSong (fldPlaylistID, fldSongID) VALUES (?, ?)";
        Connection conn = DB.getConnection();

        PreparedStatement pstmt;
        int affectedRows = 0;

        try {
            for (Song song : playlist.getSongs()) {
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, playlist.getPlaylistId());
                pstmt.setInt(2, song.getSongID());
                affectedRows += pstmt.executeUpdate();
            }

            if (affectedRows > 0) {
                System.out.printf("Set %d songs to playlist.", affectedRows);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to add songs to playlist.");
        }
    }

    /**
     * Deletes a singular song from the playlist song bridge table in the database.
     * @param playlist playlist from which a song is removed.
     * @param song song to be removed from the playlist.
     */
    public static void deleteSongFromPlaylist(Playlist playlist, Song song){
        String sql = "DELETE FROM dbo.TblPlaylistSong WHERE fldPlaylistID  = ? AND fldSongID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlist.getPlaylistId());
            pstmt.setInt(2, song.getSongID());
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Song deleted from the playlist.");
            }
            else {
                System.out.println("Failed to delete song from the playlist.");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to delete song from the playlist.");
        }
    }

    public static void addSongToPlaylist(Playlist playlist, Song song) throws Exception {
        String sql ="INSERT INTO dbo.TblPlaylistSong (fldPlaylistID, fldSongID) VALUES (?, ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, playlist.getPlaylistId());
        pstmt.setInt(2, song.getSongID());
        playlist.addSong(song);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Song added to the playlist.");
        }
        else {
            System.out.println("Failed to add song to playlist.");
        }
    }


    /**
     * Gets all songs from a specified playlist
     * @param playlistID int representing the ID of a playlist.
     * @return An Arraylist<Song> that contains all songs of the playlist.
     */
    public static ArrayList<Song> getAllSongsInPlaylist(int playlistID) {
        String sql = "Select * from dbo.TblPlaylistSong WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, playlistID);
            ArrayList<Song> songArrayList = new ArrayList<>(20);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                songArrayList.add(Song.getSong(rs.getInt("fldSongID")));
            }

            return songArrayList;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to retrieve all songs from the playlist.");
        }

        return MainController.allSongs;
    }
}
