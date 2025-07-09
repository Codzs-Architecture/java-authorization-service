-- Migration to add additional test users for JDBC UserDetailsService
-- This provides test users with different roles for testing authentication

-- Add test user with USER role
INSERT IGNORE INTO `users` VALUES ('user1','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);

-- Add test user with ADMIN role
INSERT IGNORE INTO `users` VALUES ('admin2','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);

-- Add test user with multiple roles
INSERT IGNORE INTO `users` VALUES ('manager','{bcrypt}$2a$10$r2lU4bm3aWztQnCVa7T21ek.ssD4vg7PzgXhnnz/SUv38Ph3LuISK',1);

-- Add authorities for user1 (USER role)
INSERT IGNORE INTO `authorities` VALUES ('user1','USER');

-- Add authorities for admin2 (ADMIN role)
INSERT IGNORE INTO `authorities` VALUES ('admin2','ADMIN');

-- Add authorities for manager (multiple roles)
INSERT IGNORE INTO `authorities` VALUES ('manager','USER');
INSERT IGNORE INTO `authorities` VALUES ('manager','MANAGER'); 