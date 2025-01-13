package org.example.kittyfy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BridgePlaylistSong {

    /**
     * Removes all songs from the playlist and adds them and new songs again.
     * @param playlist
     * @throws Exception
     */
    public static void updateSongsInPlaylist(Playlist playlist) throws Exception {
        deleteSongsInPlaylist(playlist);

        addSongsToPlaylist(playlist); // re adds all songs to the playlist.
    }

    public static void deleteSongsInPlaylist(Playlist playlist) throws Exception {
        String sql = "Delete from dbo.TblPlaylistSong WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
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

    /**
     * Adds all songs from the object to the playlist.
     * @param playlist
     * @throws Exception
     */
    public static void addSongsToPlaylist(Playlist playlist) throws Exception {
        String sql = "INSERT INTO dbo.TblPlaylistSong (fldPlaylistID, fldSongID) VALUES (?, ?)";
        Connection conn = DB.getConnection();

        PreparedStatement pstmt;
        int affectedRows = 0;

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

    /**
     * Gets all songs in a specified playlist
     * @param playlistID
     * @return An Arraylist<Song> that contains all songs of the playlist.
     * @throws Exception
     */
    public static ArrayList<Song> getAllSongsInPlaylist(int playlistID) throws Exception {
        String sql = "Select * from dbo.TblPlaylistSong WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, playlistID);
        ArrayList<Song> songArrayList = new ArrayList<Song>(20);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            songArrayList.add(Song.getSong(rs.getInt("fldSongID")));
        }

        return songArrayList;
    }
}
