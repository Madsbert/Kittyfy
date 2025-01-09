package org.example.kittyfy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BridgeSongArtist {

    /**
     * Creates song artist bridge.
     * @param songID
     * @param artistID
     * @throws Exception
     */
    public static void createSongArtist(int songID, int artistID) throws Exception {
        String sql = "INSERT INTO dbo.tblSongArtist VALUES (fldSongID = ?, fldArtistID = ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, songID);
        pstmt.setInt(2, artistID);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Song artist created successfully.");
        }else{
            System.out.println("Failed to create the song artist.");
        }
    }

    /**
     * Gets arraylist of songIDs from the artistID.
     * SongArtist table.
     * @param artistID
     * @return
     * @throws Exception
     */
    public static ArrayList<Integer> getAllSongIDsFromArtistID(int artistID) throws Exception {
        String sql = "SELECT * from dbo.tblSongArtist WHERE fldArtistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, artistID);
        ResultSet resultSet = pstmt.executeQuery();

        ArrayList<Integer> songIDs = new ArrayList<>(20);
        while (resultSet.next()) {
            songIDs.add(resultSet.getInt("fldSongID"));
        }
        return songIDs;
    }

    /**
     * Reads arraylist of artistIDs from the songID.
     * SongArtist table.
     * @param songID
     * @return
     * @throws Exception
     */
    public static ArrayList<Integer> getAllArtistIDsFromSongIDs(int songID) throws Exception {
        String sql = "SELECT * from dbo.tblSongArtist WHERE fldSongID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, songID);
        ResultSet resultSet = pstmt.executeQuery();

        ArrayList<Integer> artistIDs = new ArrayList<>(20);
        while (resultSet.next()) {
            artistIDs.add(resultSet.getInt("fldArtistID"));
        }
        return artistIDs;
    }

    /**
     * Reads artist id by artist name.
     * @param artistName
     * @return
     * @throws Exception
     */
    public static int getArtistID(String artistName) throws Exception {
        String sql = "SELECT fldArtistID FROM dbo.tblArtist WHERE fldArtistName = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, artistName);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("fldArtistID");
        } else {
            return -1;
        }
    }

    /**
     * Reads artist name by artist ID.
     * @param artistID
     * @return
     * @throws Exception
     */
    public static String getArtistName(int artistID) throws Exception {
        String sql = "SELECT * from dbo.tblArtist WHERE fldArtistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, artistID);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("fldArtistName");
        }return "no";
    }

    /**
     * Creates an artist in the database.
     * @param artistName
     * @throws Exception
     */
    public static void createArtist(String artistName) throws Exception {
        String sql = "INSERT INTO dbo.tblArtist VALUES (fldArtistName = ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, artistName);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Artist created successfully.");
        }else{
            System.out.println("Failed to update the artist.");
        }
    }
}
