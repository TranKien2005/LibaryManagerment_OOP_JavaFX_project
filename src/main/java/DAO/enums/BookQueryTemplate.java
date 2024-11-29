package DAO.enums;

public enum BookQueryTemplate {
    GET_TRENDING("""
        SELECT 
            b.ID, b.Title, b.Author, b.Category, b.Publisher, 
            b.YearPublished, b.AvailableCopies, b.Description, 
            b.Image, b.Rating, b.NumberOfRatings,
            COUNT(br.BookID) AS borrow_count
        FROM Book b
        LEFT JOIN Borrow br ON b.ID = br.BookID 
            AND br.BorrowDate >= DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
        GROUP BY b.ID, b.Title, b.Author, b.Category, b.Publisher, 
                 b.YearPublished, b.AvailableCopies, b.Description,
                 b.Image, b.Rating, b.NumberOfRatings
        ORDER BY borrow_count DESC, b.Rating DESC
        LIMIT ?
    """),

    GET_FAVORITES("""
        WITH BorrowHistory AS (
            SELECT b.BookID, bk.Title, bk.Category, bk.Author, COUNT(*) AS BorrowCount
            FROM Borrow b
            JOIN Book bk ON b.BookID = bk.ID
            WHERE b.AccountID = ?
            GROUP BY b.BookID
        ),
        CategoryScore AS (
            SELECT Category, SUM(BorrowCount) AS CategoryScore 
            FROM BorrowHistory GROUP BY Category
        ),
        AuthorScore AS (
            SELECT Author, SUM(BorrowCount) AS AuthorScore 
            FROM BorrowHistory GROUP BY Author
        ),
        Ranking AS (
            SELECT b.*, COALESCE(cs.CategoryScore, 0) + COALESCE(au.AuthorScore, 0) AS RankingScore
            FROM Book b
            LEFT JOIN CategoryScore cs ON b.Category = cs.Category
            LEFT JOIN AuthorScore au ON b.Author = au.Author
            WHERE b.ID NOT IN (SELECT BookID FROM Borrow WHERE AccountID = ?)
        )
        SELECT * FROM Ranking ORDER BY RankingScore DESC LIMIT ?
    """),

    SEARCH_NEW_ARRIVALS("""
        SELECT * FROM Book 
        WHERE Title LIKE ? OR Category LIKE ? OR Author LIKE ? OR Description LIKE ?
        ORDER BY 
            CASE 
                WHEN Title LIKE ? THEN 1
                WHEN Category LIKE ? THEN 2 
                WHEN Author LIKE ? THEN 3
                WHEN Description LIKE ? THEN 4
                ELSE 5
            END,
            YearPublished DESC
        LIMIT ? OFFSET ?
    """);

    private final String query;

    BookQueryTemplate(String query) {
        this.query = query;
    }

    public String get() {
        return query;
    }
} 