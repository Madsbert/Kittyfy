package org.example.kittyfy;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Song {

    private String title;
    private ArrayList<String> artist;
    private String genre;
    private String filePath;
    private int songID;

    public Song(String title, String genre, String filePath) {
        this.title = title;
        this.genre = genre;
        this.filePath = filePath;
    }

    public Song(String title, ArrayList<String> artist, String genre, String filePath) {
        this.title = title;
        this.artist = artist;
        this.genre = genre;
        this.filePath = filePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<String> getArtist() {
        return artist;
    }

    public void setArtist(ArrayList<String> artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getFilePath() {
        return filePath.trim();
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    /**
     * Creates a song in the database.
     * @param song
     * @throws Exception
     */
    public static int createSong(Song song) throws Exception {
        String sql = "INSERT INTO dbo.TblSong (fldSongName, fldGenreID, fldFilepath) VALUES (?, ?, ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, song.getTitle());
        pstmt.setInt(2, getGenreID(song.getGenre()));
        pstmt.setString(3, song.getFilePath());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            sql = "SELECT fldSongID FROM dbo.TblSong WHERE fldSongName = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, song.getTitle());

            int artistID;

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                for (String artist : song.getArtist())
                {
                    if (BridgeSongArtist.hasArtist(artist))
                    {
                        // If the artist already exists
                        artistID = BridgeSongArtist.getArtistID(artist);
                    }
                    else
                    {
                        // If the artist is not present in the Database.
                        BridgeSongArtist.createArtist(artist);
                        artistID = BridgeSongArtist.getArtistID(artist);
                    }
                    BridgeSongArtist.createSongArtist(rs.getInt("fldSongID"), artistID);
                }

                System.out.println("Song created successfully.");
                return rs.getInt("fldSongID");
            }
        }else {
            System.out.println("Failed to update the songs.");
        }
        return -1;
    }

    /**
     * Gets a specific song from the database by ID.
     * @param songID of the song.
     * @return Song with the specification of the song in the Database.
     * @throws Exception
     */
    public static Song getSong(int songID) throws Exception {
        String sql = "SELECT * FROM dbo.TblSong Where fldSongID = ?";
        PreparedStatement pstmt = DB.getConnection().prepareStatement(sql);
        pstmt.setInt(1, songID);

        Song curSong = null;
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            String name = resultSet.getString("fldSongName");
            int genreID = resultSet.getInt("fldGenreID");
            String filePath = resultSet.getString("fldFilePath");

            sql = "SELECT fldGenreName FROM dbo.tblGenre Where fldGenreID = ?";
            pstmt = DB.getConnection().prepareStatement(sql);
            pstmt.setInt(1, genreID);

            String genreName = "";
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                genreName = rs.getString(0);
            }
            curSong = new Song(name, genreName, filePath);
            curSong.setSongID(songID);
        }

        return curSong;
    }

    public static boolean doesSongExist(String songName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM dbo.TblSong Where fldSongName = ?";
        PreparedStatement pstmt;
        try {
            Connection conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
        }
        catch (Exception e) {
            DB.getConnection().rollback();
            return doesSongExist(songName);
        }

        pstmt.setString(1, songName);
        ResultSet result = pstmt.executeQuery();
        if (result.next()) {
            return result.getInt(1) == 1;
        }
        return false;
    }

    /**
     * Gets a specific song from the database by name.
     * @param songName of the song.
     * @return Song with the specification of the song in the Database.
     * @throws Exception
     */
    public static Song getSong(String songName) throws Exception {
        String sql = "SELECT * FROM dbo.TblSong Where fldSongName = ?";
        PreparedStatement pstmt = DB.getConnection().prepareStatement(sql);
        pstmt.setString(1, songName);

        Song curSong = null;
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            int genreID = resultSet.getInt("fldGenreID");
            String filePath = resultSet.getString("fldFilePath").trim();

            String genreName = Song.getGenreName(genreID).trim();

            curSong = new Song(songName, genreName, filePath);

            ArrayList<String> artistNames = new ArrayList<>();

            for (int artistID : BridgeSongArtist.getAllArtistIDsFromSongIDs(resultSet.getInt("fldSongID")))
            {
                artistNames.add(BridgeSongArtist.getArtistName(artistID));
            }
            curSong.setArtist(artistNames);
            curSong.setSongID (resultSet.getInt("fldSongID"));
        }

        return curSong;
    }

    /**
     * Creates a genre.
     * @param genreName
     * @throws Exception
     */
    public static void createGenre(String genreName) throws Exception {
        String sql = "INSERT INTO dbo.tblGenre VALUES (fldGenreName = ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, genreName);
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Genre created successfully.");
        }else{
            System.out.println("Failed to update the genre.");
        }
    }


    /**
     * Reads genre name by genre ID.
     * @param genreID
     * @return
     * @throws Exception
     */
    public static String getGenreName(int genreID) throws Exception {
        String sql = "SELECT * from dbo.tblGenre WHERE fldGenreID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, genreID);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getString("fldGenreName");
        } return "no";
    }

    /**
     * Converts genre name into the id for the genre.
     * @param genreName
     * @return Genre ID.
     * @throws Exception
     */
    public static int getGenreID(String genreName) throws Exception {
        String sql = "SELECT fldGenreID FROM dbo.tblGenre WHERE fldGenreName = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, genreName);
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt("fldGenreID");
        }
        return -1;
    }
}

