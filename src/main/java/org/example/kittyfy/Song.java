package org.example.kittyfy;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Song {

    private final String title;
    private ArrayList<String> artist;
    private final String genre;
    private final String filePath;
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

    public ArrayList<String> getArtist() {
        return artist;
    }

    public void setArtist(ArrayList<String> artist) {
        this.artist = artist;
    }

    public String getGenre() {
        return genre;
    }

    public String getFilePath() {
        return filePath.trim();
    }

    public int getSongID() {
        return songID;
    }

    public void setSongID(int songID) {
        this.songID = songID;
    }

    /**
     * Creates a song in the database.
     * @param song Instance of song to add to the database.
     * @return song ID with an Int.
     */
    public static int createSong(Song song) {
        String sql = "INSERT INTO dbo.TblSong (fldSongName, fldGenreID, fldFilepath) VALUES (?, ?, ?)";
        Connection conn = DB.getConnection();
        try {
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
                        if (!BridgeSongArtist.hasArtist(artist))
                        {
                            // If the artist is not present in the Database.
                            BridgeSongArtist.createArtist(artist);
                        }
                        artistID = BridgeSongArtist.getArtistID(artist);
                        BridgeSongArtist.createSongArtist(rs.getInt("fldSongID"), artistID);
                    }

                    System.out.println("Song created successfully.");
                    return rs.getInt("fldSongID");
                }
            }else {
                System.out.println("Failed to create the songs.");
            }
            return -1;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to create the songs.");
        }
        return -1;
    }

    /**
     * Gets a specific song from the database by ID.
     * @param songID of the song.
     * @return Song with the specification of the song in the Database.
     */
    public static Song getSong(int songID) {
        String sql = "SELECT * FROM dbo.TblSong Where fldSongID = ?";
        try {
            PreparedStatement pstmt = DB.getConnection().prepareStatement(sql);
            pstmt.setInt(1, songID);

            Song curSong = null;
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                String name = resultSet.getString("fldSongName").trim();
                int genreID = resultSet.getInt("fldGenreID");
                String filePath = resultSet.getString("fldFilePath").trim();

                sql = "SELECT fldGenreName FROM dbo.TblGenre Where fldGenreID = ?";
                pstmt = DB.getConnection().prepareStatement(sql);
                pstmt.setInt(1, genreID);

                String genreName = "";
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    genreName = rs.getString(1);
                }

                curSong = new Song(name, genreName, filePath);

                ArrayList<String> artists = new ArrayList<>();
                ArrayList<Integer> songIDs = BridgeSongArtist.getAllArtistIDsFromSongIDs(songID);
                assert songIDs != null;
                for(int artistID : songIDs){
                    String artistName = BridgeSongArtist.getArtistName(artistID);
                    assert artistName != null;
                    artists.add(artistName.trim());
                }
                curSong.setArtist(artists);
                curSong.setSongID(songID);
            }
            resultSet.close();
            return curSong;
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println("Failed to get the songs.");
        }
        return null;
    }

    /**
     * Returns the existence of a song on the database tabel TblSong.
     * @param songName name of song to look for.
     * @return true if it exists, false if not
     */
    public static boolean doesSongExist(String songName) {
        String sql = "SELECT COUNT(*) FROM dbo.TblSong Where fldSongName = ?";
        PreparedStatement pstmt;
        try {
            Connection conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, songName);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getInt(1) == 1;
            }
            return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to lookup song.");
        }
        return false;
    }

    /**
     * Returns the existence of a song on the database tabel TblSong.
     * @param genreName genre to lookup.
     * @return true if it exists, false if not
     */
    public static boolean doesGenreExist(String genreName) {
        String sql = "SELECT COUNT(*) FROM dbo.TblGenre Where fldGenreName = ?";
        PreparedStatement pstmt;
        try {
            Connection conn = DB.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genreName);
            ResultSet result = pstmt.executeQuery();
            if (result.next()) {
                return result.getInt(1) == 1;
            }
            return false;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to lookup genre.");
        }
        return false;
    }

    /**
     * Gets a specific song from the database by name.
     * @param songName of the song.
     * @return Song with the specification of the song in the Database.
     */
    public static Song getSong(String songName) {
        String sql = "SELECT * FROM dbo.TblSong Where fldSongName = ?";
        try {
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
                ArrayList<Integer> artistIDs = BridgeSongArtist.getAllArtistIDsFromSongIDs(resultSet.getInt("fldSongID"));
                assert artistIDs != null;
                for (int artistID : artistIDs)
                {
                    artistNames.add(BridgeSongArtist.getArtistName(artistID));
                }
                curSong.setArtist(artistNames);
                curSong.setSongID (resultSet.getInt("fldSongID"));
            }
            resultSet.close();
            return curSong;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get the songs.");
        }
        return null;
    }

    /**
     * Creates a genre.
     * @param genreName genre to create.
     */
    public static void createGenre(String genreName) {
        String sql = "INSERT INTO dbo.tblGenre VALUES (fldGenreName = ?)";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genreName);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Genre created successfully.");
            }else{
                System.out.println("Failed to update the genre.");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to create the genre.");
        }
    }

    /**
     * Reads genre name by genre ID.
     * @param genreID ID of genre to find name of.
     * @return returns genre name.
     */
    public static String getGenreName(int genreID) {
        String sql = "SELECT * from dbo.tblGenre WHERE fldGenreID = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, genreID);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("fldGenreName");
            }
            return "no";
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get the genre name.");
        }
        return "missing";
    }

    /**
     * Gets all genres from the database and returns them as an arraylist.
     * @return an array named "genreNames".
     */
    public static ArrayList<String> getAllGenreNames() {
        String sql = "SELECT fldGenreName FROM dbo.tblGenre";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet resultSet = pstmt.executeQuery();
            ArrayList<String> genreNames = new ArrayList<>();
            while (resultSet.next()) {
                genreNames.add(resultSet.getString("fldGenreName"));
            }
            return genreNames;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get the genre names.");
        }
        return null;
    }

    /**
     * Converts genre name into the id for the genre.
     * @param genreName name og genre to get id of
     * @return Genre ID.
     */
    public static int getGenreID(String genreName) {
        String sql = "SELECT fldGenreID FROM dbo.tblGenre WHERE fldGenreName = ?";
        Connection conn = DB.getConnection();
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genreName);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("fldGenreID");
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("Failed to get the genre ID.");
        }

        return -1;
    }
}

