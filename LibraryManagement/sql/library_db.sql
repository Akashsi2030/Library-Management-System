-- ========================================================
-- Online Library Management System - Database Setup Script
-- Designed for MySQL Workbench 8.0 CE & MySQL Server 8.0+
-- ========================================================

CREATE DATABASE IF NOT EXISTS library_db;
USE library_db;

-- 1. Users Table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Books Table
CREATE TABLE IF NOT EXISTS books (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150) NOT NULL,
    isbn VARCHAR(50) DEFAULT NULL,
    category VARCHAR(100) DEFAULT 'General',
    quantity INT NOT NULL DEFAULT 1,
    available INT NOT NULL DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 3. Issues / Borrow Records Table
CREATE TABLE IF NOT EXISTS issues (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    issue_date DATE NOT NULL,
    return_date DATE DEFAULT NULL,
    status VARCHAR(20) DEFAULT 'ISSUED',
    FOREIGN KEY (book_id) REFERENCES books(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Compatible View for borrow_records
CREATE OR REPLACE VIEW borrow_records AS 
SELECT 
    id, 
    user_id, 
    book_id, 
    issue_date, 
    return_date, 
    status 
FROM issues;

-- ========================================================
-- Seed Initial Data
-- ========================================================

-- Default Admin User (Password: admin123 -> SHA-256 hash)
INSERT INTO users (name, email, password, role)
VALUES ('Admin', 'admin@library.com', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Default Regular User (Password: user123 -> SHA-256 hash)
INSERT INTO users (name, email, password, role)
VALUES ('John Doe', 'user@library.com', 'a665a45920422f9d417e4867efdc4fb8a04a1f3fff1fa07e998e86f7f7a27ae3', 'USER')
ON DUPLICATE KEY UPDATE name=VALUES(name);

-- Sample Books
INSERT INTO books (title, author, isbn, category, quantity, available)
VALUES 
('Head First Java', 'Kathy Sierra & Bert Bates', '978-0596009205', 'Programming', 5, 5),
('Effective Java (3rd Edition)', 'Joshua Bloch', '978-0134685991', 'Programming', 3, 3),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Software Engineering', 4, 4),
('Database System Concepts', 'Abraham Silberschatz', '978-0073523323', 'Database', 3, 3),
('Design Patterns', 'Erich Gamma, Richard Helm, Ralph Johnson, John Vlissides', '978-0201633610', 'Architecture', 2, 2)
ON DUPLICATE KEY UPDATE title=VALUES(title);

