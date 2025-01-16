package org.example.kittyfy;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class BridgeSongArtist {

    /**
     * Creates song artist bridge.
     * @param songID int representing the id of the song.
     * @param artistID int representing the artist id for the bridge.
     */
    public static void createSongArtist(int songID, int artistID) {
        String sql = "INSERT INTO dbo.TblSongArtist (fldSongID, fldArtistID) VALUES (?, ?)";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, songID);
            pstmt.setInt(2, artistID);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows >= 0) {
                System.out.println("---Created SongArtist---");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to create SongArtist bridge");
        }
    }

    /**
     * Gets arraylist of songIDs from the artistID.
     * SongArtist table.
     * @param artistID Artist id from which to find songs from.
     * @return ArrayList containing song id made by the artist.
     */
    public static ArrayList<Integer> getAllSongIDsFromArtistID(int artistID) {
        String sql = "SELECT * from dbo.TblSongArtist WHERE fldArtistID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, artistID);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Integer> songIDs = new ArrayList<>(20);
            while (resultSet.next()) {
                songIDs.add(resultSet.getInt("fldSongID"));
            }
            return songIDs;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get songIDs from artistID");
        }
        return null;
    }

    /**
     * Reads arraylist of artistIDs from the songID.
     * SongArtist table.
     * @param songID int ID of the song from which to search for artists from.
     * @return ArrayList of artistIDs connected to the songID.
     */
    public static ArrayList<Integer> getAllArtistIDsFromSongIDs(int songID) {
        String sql = "SELECT * from dbo.TblSongArtist WHERE fldSongID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, songID);
            ResultSet resultSet = pstmt.executeQuery();

            ArrayList<Integer> artistIDs = new ArrayList<>(20);
            while (resultSet.next()) {
                artistIDs.add(resultSet.getInt("fldArtistID"));
            }
            return artistIDs;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get artistIDs from songID");
        }
        return null;
    }

    /**
     * Reads artist id by artist name.
     * @param artistName name of artist whose ID is wanted.
     * @return int representing an artists ID.
     */
    public static int getArtistID(String artistName) {
        String sql = "SELECT fldArtistID FROM dbo.TblArtist WHERE fldArtistName = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, artistName);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("fldArtistID");
            } else {
                return -1;
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get artistID from artistName");
        }
        return -1;
    }

    /**
     * Checks if an artist with a specified name is inside the database.
     * @param artistName artist to look for.
     * @return true if artist is already present, false if it is not.
     */
    public static boolean hasArtist(String artistName) {
        String sql = "SELECT COUNT(*) FROM dbo.TblArtist WHERE fldArtistName = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, artistName);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) >= 1;
            }
            return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to find the artist");
        }
        return false;
    }

    /**
     * Reads artist name by artist ID.
     * @param artistID ID of the artist.
     * @return String name of the artist.
     */
    public static String getArtistName(int artistID) {
        String sql = "SELECT * from dbo.TblArtist WHERE fldArtistID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, artistID);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("fldArtistName");
            }
            return "no";
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get artistName from artistID");
        }
        return null;
    }

    /**
     * Creates an artist in the database.
     * @param artistName name of the artist to create.
     */
    public static void createArtist(String artistName) {
        String sql = "INSERT INTO dbo.TblArtist(fldArtistName) VALUES (?)";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, artistName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows >= 0) {
                System.out.println("---Created Artist---");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to create Artist");
        }
    }
}
