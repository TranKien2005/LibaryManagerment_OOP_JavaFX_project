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
    Image LONGBLOB DEFAULT NULL -- Lưu trữ hình ảnh dưới dạng BLOB
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
('user1', 'password1', 'User'),
('user2', 'password2', 'User'),
('manager1', 'password3', 'Manager');

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
-- Thêm 100 người dùng vào bảng Account
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
('user18', 'password18', 'User'),
('user19', 'password19', 'User'),
('user20', 'password20', 'User'),
('user21', 'password21', 'User'),
('user22', 'password22', 'User'),
('user23', 'password23', 'User'),
('user24', 'password24', 'User'),
('user25', 'password25', 'User'),
('user26', 'password26', 'User'),
('user27', 'password27', 'User'),
('user28', 'password28', 'User'),
('user29', 'password29', 'User'),
('user30', 'password30', 'User'),
('user31', 'password31', 'User'),
('user32', 'password32', 'User'),
('user33', 'password33', 'User'),
('user34', 'password34', 'User'),
('user35', 'password35', 'User'),
('user36', 'password36', 'User'),
('user37', 'password37', 'User'),
('user38', 'password38', 'User'),
('user39', 'password39', 'User'),
('user40', 'password40', 'User'),
('user41', 'password41', 'User'),
('user42', 'password42', 'User'),
('user43', 'password43', 'User'),
('user44', 'password44', 'User'),
('user45', 'password45', 'User'),
('user46', 'password46', 'User'),
('user47', 'password47', 'User'),
('user48', 'password48', 'User'),
('user49', 'password49', 'User'),
('user50', 'password50', 'User'),
('user51', 'password51', 'User'),
('user52', 'password52', 'User'),
('user53', 'password53', 'User'),
('user54', 'password54', 'User'),
('user55', 'password55', 'User'),
('user56', 'password56', 'User'),
('user57', 'password57', 'User'),
('user58', 'password58', 'User'),
('user59', 'password59', 'User'),
('user60', 'password60', 'User'),
('user61', 'password61', 'User'),
('user62', 'password62', 'User'),
('user63', 'password63', 'User'),
('user64', 'password64', 'User'),
('user65', 'password65', 'User'),
('user66', 'password66', 'User'),
('user67', 'password67', 'User'),
('user68', 'password68', 'User'),
('user69', 'password69', 'User'),
('user70', 'password70', 'User'),
('user71', 'password71', 'User'),
('user72', 'password72', 'User'),
('user73', 'password73', 'User'),
('user74', 'password74', 'User'),
('user75', 'password75', 'User'),
('user76', 'password76', 'User'),
('user77', 'password77', 'User'),
('user78', 'password78', 'User'),
('user79', 'password79', 'User'),
('user80', 'password80', 'User'),
('user81', 'password81', 'User'),
('user82', 'password82', 'User'),
('user83', 'password83', 'User'),
('user84', 'password84', 'User'),
('user85', 'password85', 'User'),
('user86', 'password86', 'User'),
('user87', 'password87', 'User'),
('user88', 'password88', 'User'),
('user89', 'password89', 'User'),
('user90', 'password90', 'User'),
('user91', 'password91', 'User'),
('user92', 'password92', 'User'),
('user93', 'password93', 'User'),
('user94', 'password94', 'User'),
('user95', 'password95', 'User'),
('user96', 'password96', 'User'),
('user97', 'password97', 'User'),
('user98', 'password98', 'User'),
('user99', 'password99', 'User'),
('user100', 'password100', 'User'),
('user101', 'password101', 'User'),
('user102', 'password102', 'User');
-- Thêm dữ liệu ví dụ vào bảng Book
INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies)
VALUES 
('The Great Gatsby', 'F. Scott Fitzgerald', 'Novel', 'Charles Scribner\'s Sons', 1925, 5),
('Learning MySQL', 'O\'Reilly Media', 'Programming', 'O\'Reilly Media', 2007, 2),
('Harry Potter and the Philosopher\'s Stone', 'J.K. Rowling', 'Fantasy', 'Bloomsbury', 1997, 10);
-- Thêm 100 quyển sách vào bảng Book
INSERT INTO Book (Title, Author, Category, Publisher, YearPublished, AvailableCopies)
VALUES 
('Book 1', 'Author 1', 'Category 1', 'Publisher 1', 2000, 5),
('Book 2', 'Author 2', 'Category 2', 'Publisher 2', 2001, 5),
('Book 3', 'Author 3', 'Category 3', 'Publisher 3', 2002, 5),
('Book 4', 'Author 4', 'Category 4', 'Publisher 4', 2003, 5),
('Book 5', 'Author 5', 'Category 5', 'Publisher 5', 2004, 5),
('Book 6', 'Author 6', 'Category 6', 'Publisher 6', 2005, 5),
('Book 7', 'Author 7', 'Category 7', 'Publisher 7', 2006, 5),
('Book 8', 'Author 8', 'Category 8', 'Publisher 8', 2007, 5),
('Book 9', 'Author 9', 'Category 9', 'Publisher 9', 2008, 5),
('Book 10', 'Author 10', 'Category 10', 'Publisher 10', 2009, 5),
('Book 11', 'Author 11', 'Category 11', 'Publisher 11', 2010, 5),
('Book 12', 'Author 12', 'Category 12', 'Publisher 12', 2011, 5),
('Book 13', 'Author 13', 'Category 13', 'Publisher 13', 2012, 5),
('Book 14', 'Author 14', 'Category 14', 'Publisher 14', 2013, 5),
('Book 15', 'Author 15', 'Category 15', 'Publisher 15', 2014, 5),
('Book 16', 'Author 16', 'Category 16', 'Publisher 16', 2015, 5),
('Book 17', 'Author 17', 'Category 17', 'Publisher 17', 2016, 5),
('Book 18', 'Author 18', 'Category 18', 'Publisher 18', 2017, 5),
('Book 19', 'Author 19', 'Category 19', 'Publisher 19', 2018, 5),
('Book 20', 'Author 20', 'Category 20', 'Publisher 20', 2019, 5),
('Book 21', 'Author 21', 'Category 21', 'Publisher 21', 2020, 5),
('Book 22', 'Author 22', 'Category 22', 'Publisher 22', 2021, 5),
('Book 23', 'Author 23', 'Category 23', 'Publisher 23', 2022, 5),
('Book 24', 'Author 24', 'Category 24', 'Publisher 24', 2023, 5),
('Book 25', 'Author 25', 'Category 25', 'Publisher 25', 2024, 5),
('Book 26', 'Author 26', 'Category 26', 'Publisher 26', 2025, 5),
('Book 27', 'Author 27', 'Category 27', 'Publisher 27', 2026, 5),
('Book 28', 'Author 28', 'Category 28', 'Publisher 28', 2027, 5),
('Book 29', 'Author 29', 'Category 29', 'Publisher 29', 2028, 5),
('Book 30', 'Author 30', 'Category 30', 'Publisher 30', 2029, 5),
('Book 31', 'Author 31', 'Category 31', 'Publisher 31', 2030, 5),
('Book 32', 'Author 32', 'Category 32', 'Publisher 32', 2031, 5),
('Book 33', 'Author 33', 'Category 33', 'Publisher 33', 2032, 5),
('Book 34', 'Author 34', 'Category 34', 'Publisher 34', 2033, 5),
('Book 35', 'Author 35', 'Category 35', 'Publisher 35', 2034, 5),
('Book 36', 'Author 36', 'Category 36', 'Publisher 36', 2035, 5),
('Book 37', 'Author 37', 'Category 37', 'Publisher 37', 2036, 5),
('Book 38', 'Author 38', 'Category 38', 'Publisher 38', 2037, 5),
('Book 39', 'Author 39', 'Category 39', 'Publisher 39', 2038, 5),
('Book 40', 'Author 40', 'Category 40', 'Publisher 40', 2039, 5),
('Book 41', 'Author 41', 'Category 41', 'Publisher 41', 2040, 5),
('Book 42', 'Author 42', 'Category 42', 'Publisher 42', 2041, 5),
('Book 43', 'Author 43', 'Category 43', 'Publisher 43', 2042, 5),
('Book 44', 'Author 44', 'Category 44', 'Publisher 44', 2043, 5),
('Book 45', 'Author 45', 'Category 45', 'Publisher 45', 2044, 5),
('Book 46', 'Author 46', 'Category 46', 'Publisher 46', 2045, 5),
('Book 47', 'Author 47', 'Category 47', 'Publisher 47', 2046, 5),
('Book 48', 'Author 48', 'Category 48', 'Publisher 48', 2047, 5),
('Book 49', 'Author 49', 'Category 49', 'Publisher 49', 2048, 5),
('Book 50', 'Author 50', 'Category 50', 'Publisher 50', 2049, 5),
('Book 51', 'Author 51', 'Category 51', 'Publisher 51', 2050, 5),
('Book 52', 'Author 52', 'Category 52', 'Publisher 52', 2051, 5),
('Book 53', 'Author 53', 'Category 53', 'Publisher 53', 2052, 5),
('Book 54', 'Author 54', 'Category 54', 'Publisher 54', 2053, 5),
('Book 55', 'Author 55', 'Category 55', 'Publisher 55', 2054, 5),
('Book 56', 'Author 56', 'Category 56', 'Publisher 56', 2055, 5),
('Book 57', 'Author 57', 'Category 57', 'Publisher 57', 2056, 5),
('Book 58', 'Author 58', 'Category 58', 'Publisher 58', 2057, 5),
('Book 59', 'Author 59', 'Category 59', 'Publisher 59', 2058, 5),
('Book 60', 'Author 60', 'Category 60', 'Publisher 60', 2059, 5),
('Book 61', 'Author 61', 'Category 61', 'Publisher 61', 2060, 5),
('Book 62', 'Author 62', 'Category 62', 'Publisher 62', 2061, 5),
('Book 63', 'Author 63', 'Category 63', 'Publisher 63', 2062, 5),
('Book 64', 'Author 64', 'Category 64', 'Publisher 64', 2063, 5),
('Book 65', 'Author 65', 'Category 65', 'Publisher 65', 2064, 5),
('Book 66', 'Author 66', 'Category 66', 'Publisher 66', 2065, 5),
('Book 67', 'Author 67', 'Category 67', 'Publisher 67', 2066, 5),
('Book 68', 'Author 68', 'Category 68', 'Publisher 68', 2067, 5),
('Book 69', 'Author 69', 'Category 69', 'Publisher 69', 2068, 5),
('Book 70', 'Author 70', 'Category 70', 'Publisher 70', 2069, 5),
('Book 71', 'Author 71', 'Category 71', 'Publisher 71', 2070, 5),
('Book 72', 'Author 72', 'Category 72', 'Publisher 72', 2071, 5),
('Book 73', 'Author 73', 'Category 73', 'Publisher 73', 2072, 5),
('Book 74', 'Author 74', 'Category 74', 'Publisher 74', 2073, 5),
('Book 75', 'Author 75', 'Category 75', 'Publisher 75', 2074, 5),
('Book 76', 'Author 76', 'Category 76', 'Publisher 76', 2075, 5),
('Book 77', 'Author 77', 'Category 77', 'Publisher 77', 2076, 5),
('Book 78', 'Author 78', 'Category 78', 'Publisher 78', 2077, 5),
('Book 79', 'Author 79', 'Category 79', 'Publisher 79', 2078, 5),
('Book 80', 'Author 80', 'Category 80', 'Publisher 80', 2079, 5),
('Book 81', 'Author 81', 'Category 81', 'Publisher 81', 2080, 5),
('Book 82', 'Author 82', 'Category 82', 'Publisher 82', 2081, 5),
('Book 83', 'Author 83', 'Category 83', 'Publisher 83', 2082, 5),
('Book 84', 'Author 84', 'Category 84', 'Publisher 84', 2083, 5),
('Book 85', 'Author 85', 'Category 85', 'Publisher 85', 2084, 5),
('Book 86', 'Author 86', 'Category 86', 'Publisher 86', 2085, 5),
('Book 87', 'Author 87', 'Category 87', 'Publisher 87', 2086, 5),
('Book 88', 'Author 88', 'Category 88', 'Publisher 88', 2087, 5),
('Book 89', 'Author 89', 'Category 89', 'Publisher 89', 2088, 5),
('Book 90', 'Author 90', 'Category 90', 'Publisher 90', 2089, 5),
('Book 91', 'Author 91', 'Category 91', 'Publisher 91', 2090, 5),
('Book 92', 'Author 92', 'Category 92', 'Publisher 92', 2091, 5),
('Book 93', 'Author 93', 'Category 93', 'Publisher 93', 2092, 5),
('Book 94', 'Author 94', 'Category 94', 'Publisher 94', 2093, 5),
('Book 95', 'Author 95', 'Category 95', 'Publisher 95', 2094, 5),
('Book 96', 'Author 96', 'Category 96', 'Publisher 96', 2095, 5),
('Book 97', 'Author 97', 'Category 97', 'Publisher 97', 2096, 5),
('Book 98', 'Author 98', 'Category 98', 'Publisher 98', 2097, 5),
('Book 99', 'Author 99', 'Category 99', 'Publisher 99', 2098, 5),
('Book 100', 'Author 100', 'Category 100', 'Publisher 100', 2099, 5);

-- Thêm dữ liệu ví dụ vào bảng Borrow
INSERT INTO Borrow (AccountID, BookID, BorrowDate, ExpectedReturnDate)
VALUES 
(1, 1, '2023-06-01', '2023-06-15'),
(2, 2, '2023-07-05', '2023-07-20');

-- Thêm dữ liệu ví dụ vào bảng Borrow với trạng thái
INSERT INTO ReturnTable (BorrowID, ReturnDate, DamagePercentage)
VALUES 
(1, '2023-06-16', 0),
(2, '2023-07-21', 5);

-- Tạo view cho danh sách người dùng
CREATE VIEW UserList AS
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

-- Tạo view cho danh sách mượn tài liệu
CREATE VIEW BorrowList AS
SELECT 
    b.BorrowID,
    b.AccountID,
    a.Username,
    b.BookID,
    d.Title,
    b.BorrowDate,
    b.ExpectedReturnDate,
    b.Status
FROM 
    Borrow b
JOIN 
    Account a ON b.AccountID = a.AccountID
JOIN 
    Book d ON b.BookID = d.ID;
