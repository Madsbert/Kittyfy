package org.example.kittyfy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

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
        String sql = "UPDATE dbo.tblDogs SET " +
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
     * Creates a playlist in the database.
     * @param playlist
     * @throws Exception
     */
    public void createPlaylist(Playlist playlist) throws Exception {
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
     * Deletes playlist from the database.
     * @param playlist
     * @throws Exception
     */
    public void deletePlaylist(Playlist playlist) throws Exception {
        String sql = "DELETE FROM dbo.tblPlaylist WHERE fldID = ? and fldPlaylistName = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, playlist.getPlaylistId());
        pstmt.setString(2, playlist.getName());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Playlist deleted successfully.");
        }else {
            System.out.println("Failed to delete the playlist.");
        }
}
    public void createSong(Song song) throws Exception {
        String sql = "INSERT INTO dbo.tblSong VALUES (?, ?, ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, song.getTitle());
        pstmt.setString(2, song.getArtist());
        //pstmt.setInt(3, song.getGenreID());
        //pstmt.setInt(4, song.getSongID());
}
}
