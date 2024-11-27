DROP DATABASE IF EXISTS LibraryManagement;
CREATE DATABASE LibraryManagement;

-- Sử dụng cơ sở dữ liệu LibraryManagement
USE LibraryManagement;

-- Tạo bảng Account (Tài khoản)
CREATE TABLE IF NOT EXISTS Account (
    AccountID INT AUTO_INCREMENT PRIMARY KEY, -- không cần viết vào lúc thêm
    Username VARCHAR(50) NOT NULL UNIQUE,
    Password VARCHAR(255) NOT NULL,
    AccountType ENUM('Manager', 'User') NOT NULL
);

-- Tạo bảng User (Người dùng)
CREATE TABLE IF NOT EXISTS User (
    AccountID INT PRIMARY KEY, -- là thực thể yếu tham chiếu đến tài khoản
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Phone VARCHAR(15),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
);

-- Tạo bảng Book (Tài liệu) không có trường ISBN
CREATE TABLE IF NOT EXISTS Book (
    ID INT AUTO_INCREMENT PRIMARY KEY, -- không cần viết vào lúc thêm
    Title VARCHAR(255) NOT NULL,
    Author VARCHAR(100), -- tạm thời mỗi sách một tác giả để dễ quản lý
    Category VARCHAR(50),
    Publisher VARCHAR(100), -- nhà sản xuất
    YearPublished YEAR, -- năm phát hành
    AvailableCopies INT NOT NULL CHECK (AvailableCopies >= 0),
    Image LONGBLOB DEFAULT NULL, -- Lưu trữ hình ảnh dưới dạng BLOB
    Description TEXT DEFAULT NULL,
    Rating FLOAT DEFAULT 0 CHECK (Rating >= 0 AND Rating <= 5),
    NumberOfRatings INT DEFAULT 0 CHECK (NumberOfRatings >= 0)
);

-- Tạo bảng Borrow (Mượn tài liệu)
CREATE TABLE IF NOT EXISTS Borrow (
    BorrowID INT AUTO_INCREMENT PRIMARY KEY, -- không cần viết vào lúc thêm
    AccountID INT,
    BookID INT,
    BorrowDate DATE NOT NULL,
    ExpectedReturnDate DATE NOT NULL,
    Status ENUM('Borrowed', 'Returned') NOT NULL DEFAULT 'Borrowed', -- Mặc định là được mượn không cần ghi vào lúc thêm
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE,
    FOREIGN KEY (BookID) REFERENCES Book(ID) ON DELETE CASCADE
);

-- Tạo bảng ReturnTable (Trả tài liệu)
CREATE TABLE IF NOT EXISTS ReturnTable (
    ReturnID INT AUTO_INCREMENT PRIMARY KEY, -- không cần viết vào lúc thêm
    BorrowID INT,
    ReturnDate DATE NOT NULL,
    DamagePercentage INT CHECK (DamagePercentage >= 0 AND DamagePercentage <= 100),
    FOREIGN KEY (BorrowID) REFERENCES Borrow(BorrowID) ON DELETE CASCADE
);

-- Tạo bảng Manager (Người quản lý)
CREATE TABLE IF NOT EXISTS Manager (
    AccountID INT PRIMARY KEY, -- là thực thể yếu tham chiếu đến tài khoản
    FullName VARCHAR(100) NOT NULL,
    Email VARCHAR(100) NOT NULL UNIQUE,
    Phone VARCHAR(15),
    FOREIGN KEY (AccountID) REFERENCES Account(AccountID) ON DELETE CASCADE
);

-- Tạo chỉ mục cho bảng Account dựa trên Username
CREATE INDEX idx_account_username ON Account (Username);

-- Tạo chỉ mục cho bảng User dựa trên Email
CREATE INDEX idx_user_email ON User (Email);

-- Tạo chỉ mục cho bảng Book dựa trên Title
CREATE INDEX idx_book_title ON Book (Title);

-- Tạo chỉ mục cho bảng Book dựa trên Author
CREATE INDEX idx_book_author ON Book (Author);

-- Tạo chỉ mục cho bảng Book dựa trên Category
CREATE INDEX idx_book_category ON Book (Category);

-- Tạo chỉ mục cho bảng Borrow dựa trên AccountID và BookID
CREATE INDEX idx_borrow_account_book ON Borrow (AccountID, BookID);

-- Tạo chỉ mục cho bảng Borrow dựa trên BorrowDate
CREATE INDEX idx_borrow_borrowdate ON Borrow (BorrowDate);

-- Tạo chỉ mục cho bảng ReturnTable dựa trên BorrowID
CREATE INDEX idx_return_borrow ON ReturnTable (BorrowID);

-- Tạo chỉ mục cho bảng ReturnTable dựa trên ReturnDate
CREATE INDEX idx_return_returndate ON ReturnTable (ReturnDate);

-- Tạo trigger để ngăn mượn sách khi số lượng sách còn 0
DELIMITER //
CREATE TRIGGER before_borrow_insert
BEFORE INSERT ON Borrow
FOR EACH ROW
BEGIN
    DECLARE available INT;
    SELECT AvailableCopies INTO available FROM Book WHERE ID = NEW.BookID;
    IF available <= 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể mượn sách vì đã hết số lượng.';
    END IF;
END;
//
DELIMITER ;

-- Tạo trigger để khi thêm một bản ghi Borrow mới thì giảm AvailableCopies
DELIMITER //
CREATE TRIGGER after_borrow_insert
AFTER INSERT ON Borrow
FOR EACH ROW
BEGIN
    UPDATE Book 
    SET AvailableCopies = AvailableCopies - 1 
    WHERE ID = NEW.BookID;
END;
//
DELIMITER ;

-- Tạo trigger để khi xóa một bản ghi Borrow chưa được trả lại thì cập nhật AvailableCopies
DELIMITER //
CREATE TRIGGER after_borrow_delete
AFTER DELETE ON Borrow
FOR EACH ROW
BEGIN
    IF OLD.Status = 'Borrowed' THEN
        UPDATE Book 
        SET AvailableCopies = AvailableCopies + 1 
        WHERE ID = OLD.BookID;
    END IF;
END;
//
DELIMITER ;

-- Tạo trigger để sau khi thêm một return mới vào thì tăng số sách có sẵn của sách được mượn đồng thời chuyển borrow thành đã được trả
DELIMITER //
CREATE TRIGGER after_return_insert
AFTER INSERT ON ReturnTable
FOR EACH ROW
BEGIN
    -- Tăng số sách có sẵn của sách được mượn
    UPDATE Book 
    SET AvailableCopies = AvailableCopies + 1 
    WHERE ID = (SELECT BookID FROM Borrow WHERE BorrowID = NEW.BorrowID);
    
    -- Chuyển borrow thành đã được trả
    UPDATE Borrow
    SET Status = 'Returned'
    WHERE BorrowID = NEW.BorrowID;
END;
//
DELIMITER ;

-- Tạo trigger để khi xóa một bản ghi ReturnTable thì giảm số sách có sẵn của sách được mượn đồng thời chuyển borrow thành chưa được trả
DELIMITER //
CREATE TRIGGER after_return_delete
AFTER DELETE ON ReturnTable
FOR EACH ROW
BEGIN
    -- Giảm số sách có sẵn của sách được mượn
    UPDATE Book 
    SET AvailableCopies = AvailableCopies - 1 
    WHERE ID = (SELECT BookID FROM Borrow WHERE BorrowID = OLD.BorrowID);
    
    -- Chuyển borrow thành chưa được trả
    UPDATE Borrow
    SET Status = 'Borrowed'
    WHERE BorrowID = OLD.BorrowID;
END;
//
DELIMITER ;

-- Tạo trigger để ngăn cập nhật tài khoản ngoại trừ mật khẩu
DELIMITER //
CREATE TRIGGER before_account_update
BEFORE UPDATE ON Account
FOR EACH ROW
BEGIN
    IF NEW.Username <> OLD.Username OR NEW.AccountType <> OLD.AccountType THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể cập nhật tài khoản ngoại trừ mật khẩu.';
    END IF;
END;
//
DELIMITER ;

-- Tạo trigger để ngăn người dùng mới tham chiếu đến tài khoản của người quản lý và ngược lại
DELIMITER //
CREATE TRIGGER before_user_insert
BEFORE INSERT ON User
FOR EACH ROW
BEGIN
    DECLARE account_type ENUM('Manager', 'User');
    SELECT AccountType INTO account_type FROM Account WHERE AccountID = NEW.AccountID;
    IF account_type = 'Manager' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể thêm người dùng tham chiếu đến tài khoản của người quản lý.';
    END IF;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER before_manager_insert
BEFORE INSERT ON Manager
FOR EACH ROW
BEGIN
    DECLARE account_type ENUM('Manager', 'User');
    SELECT AccountType INTO account_type FROM Account WHERE AccountID = NEW.AccountID;
    IF account_type = 'User' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể thêm người quản lý tham chiếu đến tài khoản của người dùng.';
    END IF;
END;
//
DELIMITER ;
-- Tạo trigger để ngăn tạo borrow có trạng thái là đã trả sẵn
DELIMITER //
CREATE TRIGGER before_borrow_insert_status
BEFORE INSERT ON Borrow
FOR EACH ROW
BEGIN
    IF NEW.Status = 'Returned' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Không thể tạo borrow có trạng thái là đã trả sẵn.';
    END IF;
END;
//
DELIMITER ;
-- Tạo trigger để ngăn thêm người dùng có số điện thoại có ký tự không phải số hoặc email không đúng định dạng
DELIMITER //
CREATE TRIGGER before_user_insert_validation
BEFORE INSERT ON User
FOR EACH ROW
BEGIN
    -- Kiểm tra số điện thoại chỉ chứa ký tự số
    IF NEW.Phone REGEXP '[^0-9]' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa ký tự số.';
    END IF;

    -- Kiểm tra định dạng email
    IF NEW.Email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email không đúng định dạng.';
    END IF;
END;
//
DELIMITER ;

-- Tạo trigger để ngăn thêm sách có thông tin giống hệt đã tồn tại ngoại trừ id, ảnh, rating, mô tả, rating count
DELIMITER //
CREATE TRIGGER before_book_insert
BEFORE INSERT ON Book
FOR EACH ROW
BEGIN
    DECLARE count_books INT;
    SELECT COUNT(*) INTO count_books 
    FROM Book 
    WHERE Title = NEW.Title 
    AND Author = NEW.Author 
    AND Category = NEW.Category 
    AND Publisher = NEW.Publisher 
    AND YearPublished = NEW.YearPublished;
    
    IF count_books > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Sách với thông tin tương tự đã tồn tại.';
    END IF;
END;
//
DELIMITER ;
-- Tạo trigger để ngăn cập nhật sách có thông tin giống hệt đã tồn tại ngoại trừ id, ảnh, rating, mô tả, rating count
DELIMITER //
CREATE TRIGGER before_book_update
BEFORE UPDATE ON Book
FOR EACH ROW
BEGIN
    DECLARE count_books INT;
    SELECT COUNT(*) INTO count_books 
    FROM Book 
    WHERE Title = NEW.Title 
    AND Author = NEW.Author 
    AND Category = NEW.Category 
    AND Publisher = NEW.Publisher 
    AND YearPublished = NEW.YearPublished
    AND ID <> NEW.ID;
    
    IF count_books > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Sách với thông tin tương tự đã tồn tại.';
    END IF;
END;
//
DELIMITER ;
-- Tạo trigger để ngăn thêm người quản lý có số điện thoại có ký tự không phải số hoặc email không đúng định dạng
DELIMITER //
CREATE TRIGGER before_manager_insert_validation
BEFORE INSERT ON Manager
FOR EACH ROW
BEGIN
    -- Kiểm tra số điện thoại chỉ chứa ký tự số
    IF NEW.Phone REGEXP '[^0-9]' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa ký tự số.';
    END IF;

    -- Kiểm tra định dạng email
    IF NEW.Email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email không đúng định dạng.';
    END IF;
END;
//

DELIMITER ;

-- Tạo trigger để ngăn mượn sách khi người dùng vẫn còn đang mượn sách đó
DELIMITER //
CREATE TRIGGER before_borrow_insert_check
BEFORE INSERT ON Borrow
FOR EACH ROW
BEGIN
    DECLARE count_borrowed INT;
    SELECT COUNT(*) INTO count_borrowed 
    FROM Borrow 
    WHERE AccountID = NEW.AccountID 
    AND BookID = NEW.BookID 
    AND Status = 'Borrowed';
    
    IF count_borrowed > 0 THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Người dùng vẫn còn đang mượn sách này.';
    END IF;
END;
//
DELIMITER ;
-- Tạo trigger để tự động thêm một người dùng hoặc quản lý khi tạo một tài khoản
DELIMITER //
CREATE TRIGGER after_account_insert
AFTER INSERT ON Account
FOR EACH ROW
BEGIN
    IF NEW.AccountType = 'User' THEN
        INSERT INTO User (AccountID, FullName, Email, Phone)
        VALUES (NEW.AccountID, NEW.Username, CONCAT(NEW.Username, '@example.com'), '0000000000');
    ELSEIF NEW.AccountType = 'Manager' THEN
        INSERT INTO Manager (AccountID, FullName, Email, Phone)
        VALUES (NEW.AccountID, NEW.Username, CONCAT(NEW.Username, '@example.com'), '0000000000');
    END IF;
END;
//
DELIMITER ;

-- Tạo trigger để ngăn cập nhật người dùng có số điện thoại có ký tự không phải số hoặc email không đúng định dạng
DELIMITER //
CREATE TRIGGER before_user_update_validation
BEFORE UPDATE ON User
FOR EACH ROW
BEGIN
    -- Kiểm tra số điện thoại chỉ chứa ký tự số
    IF NEW.Phone REGEXP '[^0-9]' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa ký tự số.';
    END IF;

    -- Kiểm tra định dạng email
    IF NEW.Email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email không đúng định dạng.';
    END IF;
END;
//
DELIMITER ;

-- Tạo trigger để ngăn cập nhật người quản lý có số điện thoại có ký tự không phải số hoặc email không đúng định dạng
DELIMITER //
CREATE TRIGGER before_manager_update_validation
BEFORE UPDATE ON Manager
FOR EACH ROW
BEGIN
    -- Kiểm tra số điện thoại chỉ chứa ký tự số
    IF NEW.Phone REGEXP '[^0-9]' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Số điện thoại chỉ được chứa ký tự số.';
    END IF;

    -- Kiểm tra định dạng email
    IF NEW.Email NOT REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$' THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Email không đúng định dạng.';
    END IF;
END;
//


DELIMITER ;

-- Tạo trigger để xóa người dùng hoặc quản lý thì xóa luôn account đó
DELIMITER //
CREATE TRIGGER after_user_delete
AFTER DELETE ON User
FOR EACH ROW
BEGIN
    DELETE FROM Account WHERE AccountID = OLD.AccountID;
END;
//
DELIMITER ;

DELIMITER //
CREATE TRIGGER after_manager_delete
AFTER DELETE ON Manager
FOR EACH ROW
BEGIN
    DELETE FROM Account WHERE AccountID = OLD.AccountID;
END;
//
DELIMITER ;

-- Thêm dữ liệu ví dụ vào bảng Account
INSERT INTO Account (Username, Password, AccountType)
VALUES 
('2', '2', 'User'),
('user2', 'password2', 'User'),
('1', '1', 'Manager');

-- Chỉnh sửa thông tin người dùng đã được tạo tự động khi thêm tài khoản
UPDATE User
SET FullName = 'Nguyen Van A', Email = 'nguyenvana@gmail.com', Phone = '0909123456'
WHERE AccountID = 1;

UPDATE User
SET FullName = 'Tran Thi B', Email = 'tranthib@gmail.com', Phone = '0912345678'
WHERE AccountID = 2;

UPDATE Manager
SET FullName = 'Le Van C', Email = 'levanc@yahoo.com', Phone = '0909111222'
WHERE AccountID = 3;
-- Thêm dữ liệu ví dụ vào bảng Account
INSERT INTO Account (Username, Password, AccountType)
VALUES 
('user3', 'password3', 'User'),
('user4', 'password4', 'User'),
('user5', 'password5', 'User'),
('user6', 'password6', 'User'),
('user7', 'password7', 'User'),
('user8', 'password8', 'User'),
('user9', 'password9', 'User'),
('user10', 'password10', 'User'),
('user11', 'password11', 'User'),
('user12', 'password12', 'User'),
('user13', 'password13', 'User'),
('user14', 'password14', 'User'),
('user15', 'password15', 'User'),
('user16', 'password16', 'User'),
('user17', 'password17', 'User'),
('manager2', 'password18', 'Manager'),
('manager3', 'password19', 'Manager'),
('manager4', 'password20', 'Manager'),
('manager5', 'password21', 'Manager'),
('manager6', 'password22', 'Manager');

-- Chỉnh sửa thông tin người dùng đã được tạo tự động khi thêm tài khoản
UPDATE User
SET FullName = 'Nguyen Van D', Email = 'nguyenvand@gmail.com', Phone = '0909123457'
WHERE AccountID = 4;

UPDATE User
SET FullName = 'Tran Thi E', Email = 'tranthie@gmail.com', Phone = '0912345679'
WHERE AccountID = 5;

UPDATE User
SET FullName = 'Le Van F', Email = 'levanf@yahoo.com', Phone = '0909111223'
WHERE AccountID = 6;

UPDATE User
SET FullName = 'Nguyen Van G', Email = 'nguyenvang@gmail.com', Phone = '0909123458'
WHERE AccountID = 7;

UPDATE User
SET FullName = 'Tran Thi H', Email = 'tranthih@gmail.com', Phone = '0912345680'
WHERE AccountID = 8;

UPDATE User
SET FullName = 'Le Van I', Email = 'levani@yahoo.com', Phone = '0909111224'
WHERE AccountID = 9;

UPDATE User
SET FullName = 'Nguyen Van J', Email = 'nguyenvanj@gmail.com', Phone = '0909123459'
WHERE AccountID = 10;

UPDATE User
SET FullName = 'Tran Thi K', Email = 'tranthik@gmail.com', Phone = '0912345681'
WHERE AccountID = 11;

UPDATE User
SET FullName = 'Le Van L', Email = 'leval@yahoo.com', Phone = '0909111225'
WHERE AccountID = 12;

UPDATE User
SET FullName = 'Nguyen Van M', Email = 'nguyenvanm@gmail.com', Phone = '0909123460'
WHERE AccountID = 13;

UPDATE User
SET FullName = 'Tran Thi N', Email = 'tranthin@gmail.com', Phone = '0912345682'
WHERE AccountID = 14;

UPDATE User
SET FullName = 'Le Van O', Email = 'levano@yahoo.com', Phone = '0909111226'
WHERE AccountID = 15;

UPDATE User
SET FullName = 'Nguyen Van P', Email = 'nguyenvanp@gmail.com', Phone = '0909123461'
WHERE AccountID = 16;

UPDATE User
SET FullName = 'Tran Thi Q', Email = 'tranthiq@gmail.com', Phone = '0912345683'
WHERE AccountID = 17;

UPDATE User
SET FullName = 'Le Van R', Email = 'levanr@yahoo.com', Phone = '0909111227'
WHERE AccountID = 18;

UPDATE Manager
SET FullName = 'Le Van S', Email = 'levans@yahoo.com', Phone = '0909111228'
WHERE AccountID = 19;

UPDATE Manager
SET FullName = 'Nguyen Van T', Email = 'nguyenvant@gmail.com', Phone = '0909123462'
WHERE AccountID = 20;

UPDATE Manager
SET FullName = 'Tran Thi U', Email = 'tranthiu@gmail.com', Phone = '0912345684'
WHERE AccountID = 21;

UPDATE Manager
SET FullName = 'Le Van V', Email = 'levanv@yahoo.com', Phone = '0909111229'
WHERE AccountID = 22;

UPDATE Manager
SET FullName = 'Nguyen Van W', Email = 'nguyenvanw@gmail.com', Phone = '0909123463'
WHERE AccountID = 23;


-- Thêm dữ liệu ví dụ vào bảng Book
INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies)
VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', 'Novel', 'Charles Scribner\'s Sons', 1925, 10),
('Learning MySQL', 'O\'Reilly Media', 'Programming', 'O\'Reilly Media', 2007, 10),
('Harry Potter and the Philosopher\'s Stone', 'J.K. Rowling', 'Fantasy', 'Bloomsbury', 1997, 10);
-- Thêm dữ liệu ví dụ vào bảng Book
INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies)
VALUES 
('To Kill a Mockingbird', 'Harper Lee', 'Fiction', 'J.B. Lippincott & Co.', 1960, 5),
('1984', 'George Orwell', 'Dystopian', 'Secker & Warburg', 1949, 10),
('The Catcher in the Rye', 'J.D. Salinger', 'Fiction', 'Little, Brown and Company', 1951, 8),
('The Hobbit', 'J.R.R. Tolkien', 'Fantasy', 'George Allen & Unwin', 1937, 6),
('Fahrenheit 451', 'Ray Bradbury', 'Dystopian', 'Ballantine Books', 1953, 4),
('Brave New World', 'Aldous Huxley', 'Dystopian', 'Chatto & Windus', 1932, 6),
('The Grapes of Wrath', 'John Steinbeck', 'Fiction', 'The Viking Press-James Lloyd', 1939, 5);
-- Thêm dữ liệu ví dụ vào bảng Book với năm phát hành sau 1900
INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies)
VALUES 
('The Road', 'Cormac McCarthy', 'Post-apocalyptic', 'Alfred A. Knopf', 2006, 7),
('The Da Vinci Code', 'Dan Brown', 'Mystery', 'Doubleday', 2003, 8),
('The Kite Runner', 'Khaled Hosseini', 'Drama', 'Riverhead Books', 2003, 9),
('Life of Pi', 'Yann Martel', 'Adventure', 'Knopf Canada', 2001, 6),
('The Girl with the Dragon Tattoo', 'Stieg Larsson', 'Mystery', 'Norstedts Förlag', 2005, 5);

-- Thêm dữ liệu ví dụ vào bảng Borrow và ReturnTable
-- Thêm dữ liệu ví dụ vào bảng Borrow
INSERT INTO Borrow (AccountID, BookID, BorrowDate, ExpectedReturnDate)
VALUES 
(1, 1, '2023-01-01', '2023-01-15'),
(2, 2, '2023-01-02', '2023-01-16'),
(3, 3, '2023-01-03', '2023-01-17'),
(4, 4, '2023-01-04', '2023-01-18'),
(5, 5, '2023-01-05', '2023-01-19'),
(6, 6, '2023-01-06', '2023-01-20'),
(7, 7, '2023-01-07', '2023-01-21'),
(8, 8, '2023-01-08', '2023-01-22'),
(9, 9, '2023-01-09', '2023-01-23'),
(10, 10, '2023-01-10', '2023-01-24'),
(11, 11, '2023-01-11', '2023-01-25'),
(12, 12, '2023-01-12', '2023-01-26'),
(13, 13, '2023-01-13', '2023-01-27'),
(1, 2, '2023-01-24', '2023-02-07'),
(2, 3, '2023-01-25', '2023-02-08'),
(3, 4, '2023-01-26', '2023-02-09'),
(4, 5, '2023-01-27', '2023-02-10'),
(5, 6, '2023-01-28', '2023-02-11'),
(6, 7, '2023-01-29', '2023-02-12'),
(7, 8, '2023-01-30', '2023-02-13');

-- Thêm dữ liệu ví dụ vào bảng ReturnTable
INSERT INTO ReturnTable (BorrowID, ReturnDate, DamagePercentage)
VALUES 
(1, '2023-01-15', 0),
(2, '2023-01-16', 5),
(3, '2023-01-17', 0),
(4, '2023-01-18', 10),
(5, '2023-01-19', 0),
(6, '2023-01-20', 15),
(7, '2023-01-21', 0),
(8, '2023-01-22', 20),
(9, '2023-01-23', 0),
(10, '2023-01-24', 25),
(11, '2023-01-25', 0),
(12, '2023-01-26', 30),
(13, '2023-01-27', 0),
(14, '2023-01-28', 35),
(15, '2023-01-29', 0),
(16, '2023-01-30', 40),
(17, '2023-01-31', 0),
(18, '2023-02-01', 45),
(19, '2023-02-02', 0),
(20, '2023-02-03', 50);
SELECT 
    u.AccountID,
    u.FullName,
    u.Email,
    u.Phone,
    a.Username,
    a.AccountType
FROM 
    User u
JOIN 
    Account a ON u.AccountID = a.AccountID;

-- Tạo view cho danh sách tài liệu
CREATE VIEW BookList AS
SELECT 
    ID,
    Title,
    Author,
    Category,
    AvailableCopies
FROM 
    Book;

    -- Tạo view cho danh sách mượn trả tài liệu
    CREATE VIEW BorrowReturnList AS
    SELECT 
        b.BorrowID,
        CONCAT(u.AccountID, '-', u.FullName) AS Member,
        CONCAT(bk.ID, '-', bk.Title) AS Book,
        b.BorrowDate,
        b.ExpectedReturnDate,
        b.Status,
        rt.ReturnDate,
        rt.DamagePercentage,
        CASE 
            WHEN rt.ReturnDate IS NOT NULL AND rt.ReturnDate > b.ExpectedReturnDate THEN DATEDIFF(rt.ReturnDate, b.ExpectedReturnDate) * 1000 + rt.DamagePercentage * 5000
            WHEN rt.ReturnDate IS NOT NULL THEN rt.DamagePercentage * 5000
            ELSE 0
        END AS PenaltyFee
    FROM 
        Borrow b
    JOIN 
        User u ON b.AccountID = u.AccountID
    JOIN 
        Book bk ON b.BookID = bk.ID
    LEFT JOIN 
        ReturnTable rt ON b.BorrowID = rt.BorrowID;
