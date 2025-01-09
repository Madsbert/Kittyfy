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
     * Removes all songs from the playlist and adds them and new songs again.
     * @param playlist
     * @throws Exception
     */
    public void updateSongsInPlaylist(Playlist playlist) throws Exception {
        deleteSongsInPlaylist(playlist);

        addSongsToPlaylist(playlist); // re adds all songs to the playlist.
    }

    public void deleteSongsInPlaylist(Playlist playlist) throws Exception {
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

    /**
     * Reads a playlist from the database and creates a new playlist object.
     * @param playlistId
     * @return
     * @throws Exception
     */
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

        Playlist newPlaylist = new Playlist(playlistName, getAllSongsInPlaylist(playlistID));
        newPlaylist.setLastPlayed(lastPlayed);
        newPlaylist.setPlaylistId(playlistID);

        return newPlaylist;
    }

    /**
     * Gets all songs in a specified playlist
     * @param playlistID
     * @return An Arraylist<Song> that contains all songs of the playlist.
     * @throws Exception
     */
    public ArrayList<Song> getAllSongsInPlaylist(int playlistID) throws Exception {
        String sql = "Select * from dbo.tblPlaylistSong WHERE fldPlaylistID = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, playlistID);
        ArrayList<Song> songArrayList = new ArrayList<Song>(20);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            songArrayList.add(getSong(rs.getInt("fldSongID")));
        }

        return songArrayList;
    }

    /**
     * Deletes playlist from the database.
     * @param playlist
     * @throws Exception
     */
    public void deletePlaylist(Playlist playlist) throws Exception {
        String sql = "DELETE FROM dbo.tblPlaylist WHERE fldPlaylistID = ?";
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
    public ArrayList<Playlist> getAllPlaylists() throws Exception {
        String sql = "Select * from dbo.tblPlaylist";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        ResultSet resultSet = pstmt.executeQuery();
        ArrayList<Playlist> allPlaylists = new ArrayList<>();
        while (resultSet.next()) {
            String playlistName = "Not set Error";
            long lastPlayed = 0;
            int playlistID = 0;
            if (resultSet.next()) {
                playlistName = resultSet.getString("fldPlaylistName");
                lastPlayed = resultSet.getLong("fldLastPlayed");
                playlistID = resultSet.getInt("fldPlaylistID");

            }
            Playlist newPlaylist = new Playlist(playlistName, getAllSongsInPlaylist(playlistID));
            newPlaylist.setLastPlayed(lastPlayed);
            newPlaylist.setPlaylistId(playlistID);
            allPlaylists.add(newPlaylist);
        }
        return allPlaylists;
    }

    /**
     * Creates a song in the database.
     * @param song
     * @throws Exception
     */
    public void createSong(Song song) throws Exception {
        String sql = "INSERT INTO dbo.tblSong VALUES (fldSongName = ?, fldGenreID = ?, fldFilepath ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, song.getTitle());
        pstmt.setInt(2, getGenreID(song.getGenre()));
        pstmt.setString(3, song.getFilePath());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Song created successfully.");
        }else {
            System.out.println("Failed to update the songs.");
        }
    }

    /**
     * Creates song artist bridge.
     * @param song
     * @throws Exception
     */
    public void createSongArtist(Song song) throws Exception {
        String sql = "INSERT INTO dbo.tblSongArtist VALUES (fldSongID = ?, fldArtistID = ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, song.getSongID());
        pstmt.setInt(2, getArtistID(song.getArtist()));
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
    public ArrayList<Integer> getAllSongIDsFromArtistID(int artistID) throws Exception {
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
    public ArrayList<Integer> getAllArtistIDsFromSongIDs(int songID) throws Exception {
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
    public int getArtistID(String artistName) throws Exception {
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
    public String getArtistName(int artistID) throws Exception {
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
     * @param song
     * @throws Exception
     */
    public void createArtist(Song song) throws Exception {
        String sql = "INSERT INTO dbo.tblArtist VALUES (fldArtistName = ?)";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, song.getArtist());
        int affectedRows = pstmt.executeUpdate();
        if (affectedRows > 0) {
            System.out.println("Artist created successfully.");
        }else{
            System.out.println("Failed to update the artist.");
        }
    }

    /**
     * Creates a genre.
     * @param genreName
     * @throws Exception
     */
    public void createGenre(String genreName) throws Exception {
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
    public String getGenreName(int genreID) throws Exception {
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
     * Gets a specific song from the database by ID.
     * @param songID of the song.
     * @return Song with the specification of the song in the Database.
     * @throws Exception
     */
    //Ikke færdig.
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
            curSong.setSongID(songID);
        }

        return curSong;
    }
    /**
     * Gets a specific song from the database by name.
     * @param songName of the song.
     * @return Song with the specification of the song in the Database.
     * @throws Exception
     */
    public Song getSong(String songName) throws Exception {
        String sql = "SELECT * FROM dbo.TblSong Where fldSongName = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, songName);

        Song curSong = null;
        ResultSet resultSet = pstmt.executeQuery();
        if (resultSet.next()) {
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
            curSong = new Song(songName, genreName, filePath);
            curSong.setArtist("fldArtistID"); //???
            curSong.setSongID (resultSet.getInt("fldSongID"));
        }

        return curSong;
    }

    /**
     * Converts genre name into the id for the genre.
     * @param genreName
     * @return Genre ID.
     * @throws Exception
     */
    public int getGenreID(String genreName) throws Exception {
        String sql = "SELECT fldGenreID FROM dbo.tblGenre WHERE fldGenreName = ?";
        Connection conn = DB.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, genreName);
        ResultSet resultSet = pstmt.executeQuery();
        return resultSet.getInt("fldGenreID");
    }
}
