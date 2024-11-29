package DAO.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import model.Document;

/**
 * Implementation of RowMapper for Document entities.
 * Maps database rows from the Book table to Document domain objects.
 * Handles both basic document properties and additional metadata like ratings and reviews.
 */
public class DocumentMapper implements RowMapper<Document> {
    /**
     * Maps a database row to a Document object.
     * Includes mapping of basic properties and additional metadata such as
     * description, cover image, rating, and review count.
     *
     * @param rs The ResultSet containing the database row
     * @return A new Document object populated with data from the ResultSet
     * @throws SQLException if a database access error occurs or this method is called on a closed ResultSet
     */
    @Override
    public Document mapRow(ResultSet rs) throws SQLException {
        Document document = new Document(
            rs.getInt("ID"),
            rs.getString("Title"),
            rs.getString("Author"),
            rs.getString("Category"),
            rs.getString("Publisher"),
            rs.getInt("YearPublished"),
            rs.getInt("AvailableCopies")
        );
        document.setDescription(rs.getString("Description"));
        document.setCoverImage(rs.getBinaryStream("Image"));
        document.setRating(rs.getDouble("Rating"));
        document.setReviewCount(rs.getInt("NumberOfRatings"));
        return document;
    }
} 