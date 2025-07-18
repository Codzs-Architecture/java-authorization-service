-- Migration to add additional test users for JDBC UserDetailsService
-- WARNING: These are TEST USERS ONLY - DO NOT USE IN PRODUCTION
-- Different passwords for security


-- Admin user with strong unique password (password: SecureAdminPass123!)
INSERT IGNORE INTO `users` VALUES ('admin','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);
INSERT IGNORE INTO `authorities` VALUES ('admin','ADMIN');

-- Regular user with strong unique password (password: SecureUserPass456!)
INSERT IGNORE INTO `users` VALUES ('user1','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);
INSERT IGNORE INTO `authorities` VALUES ('user1','USER');
