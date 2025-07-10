-- Migration to add additional test users for JDBC UserDetailsService
-- This provides test users with different roles for testing authentication


INSERT IGNORE INTO `users` VALUES ('admin','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);
INSERT IGNORE INTO `authorities` VALUES ('admin','ADMIN');

INSERT IGNORE INTO `users` VALUES ('user1','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);
INSERT IGNORE INTO `authorities` VALUES ('user1','USER');
