package org.example.kittyfy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class DB {

    private static final String URL = "jdbc:sqlserver://localhost;databaseName=KittyfyDB";
    private static final String USERNAME = "sa"; // replace with your username
    private static final String PASSWORD = "Kittyfy1234"; // replace with your password
    private static Connection conn;

    /**
     * Establishes connection to the Database.
     * @return
     */
    public static Connection getConnection() {
        if (conn == null) {
            try
            {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Connected to the database.");
            }
            catch (Exception e)
            {
                System.out.println("Failed to connect to database.");
            }

        }

        return conn;
    }


    /**
     * Updates a playlist in the database.
     * @param playlist
     * @throws Exception
     */
    public void updatePlaylist(Playlist playlist) throws Exception {
        String sql = "UPDATE dbo.tblPlaylist SET " +
                "fldPlaylistName = ?," +
                "fldLastPlayed = ? WHERE fldID = ?";
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
     * Removes all songs from the playlist and adds them and new songs again.
     * @param playlist
     * @throws Exception
     */
    public void updateSongsInPlaylist(Playlist playlist) throws Exception {
        String sql = "Delete from dbo.tblPlaylistSong WHERE fldPlaylistID = ?";
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

        addSongsToPlaylist(playlist); // re adds all songs to the playlist.
    }

    /**
     * Adds all songs from the object to the playlist.
     * @param playlist
     * @throws Exception
     */
    public void addSongsToPlaylist(Playlist playlist) throws Exception {
        String sql = "INSERT INTO dbo.tblPlaylistSong " +
                "VALUES(fldPlaylistID = ?, fldSongID = ?)";
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

    public Playlist getPlaylist(int playlistId) throws Exception {
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

        Playlist newPlaylist = new Playlist(playlistName, getAllSongsInPlaylist());
        newPlaylist.setLastPlayed(lastPlayed);
        newPlaylist.setPlaylistId(playlistID);

        return newPlaylist;
    }

    public ArrayList<Song> getAllSongsInPlaylist() throws Exception
    {
        String sql = "Select * from dbo.tblPlaylistSong WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);

        ArrayList<Song> songArrayList = new ArrayList<Song>(20);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            songArrayList.add(getSong(rs.getInt("fldSongID")));
        }

        return songArrayList;
    }

    public Song getSong(int songID) throws Exception {
        String sql = "SELECT * FROM dbo.TblSong Where fldSongID = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, songID);

        Song curSong = null;
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            String name = resultSet.getString("fldSongName");
            int genreID = resultSet.getInt("fldGenreID");
            String filePath = resultSet.getString("fldFilePath");

            sql = "SELECT fldGenreName FROM dbo.tblGenre Where fldGenreID = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, genreID);

            String genreName = "";
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                genreName = rs.getString(0);
            }
            curSong = new Song(name, genreName, filePath);
        }

        return curSong;
    }
}
