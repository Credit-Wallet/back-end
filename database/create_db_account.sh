#!/bin/bash

# Password user An: 123123@An
# Password user Tiển: 123123@Tien

# Set MySQL credentials
MYSQL_CONTAINER="back-end-mysql-1"
MYSQL_USER="root"
MYSQL_PASSWORD="Ak)%@b)S1uI8"
MYSQL_HOST="127.0.0.1"

# Temporary SQL file
temp_sql_file="/tmp/setup_db.sql"

# Create the SQL script
cat <<'EOF' > $temp_sql_file
CREATE DATABASE IF NOT EXISTS `account`;
USE `account`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for accounts
-- ----------------------------
DROP TABLE IF EXISTS accounts;
CREATE TABLE accounts (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) DEFAULT NULL,
  email VARCHAR(255) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  selected_network_id BIGINT DEFAULT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  url_avatar VARCHAR(255) DEFAULT NULL,
  username VARCHAR(255) DEFAULT NULL,
  is_two_factor BIT(1) NOT NULL,
  secret_key VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_email (email)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for fcm_tokens
-- ----------------------------
DROP TABLE IF EXISTS fcm_tokens;
CREATE TABLE fcm_tokens (
  id BIGINT NOT NULL AUTO_INCREMENT,
  created_at DATETIME(6) DEFAULT NULL,
  fcm_token VARCHAR(255) NOT NULL,
  updated_at DATETIME(6) DEFAULT NULL,
  account_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY UK_fcm_token (fcm_token),
  KEY FK_account_id (account_id),
  CONSTRAINT FK_account_id FOREIGN KEY (account_id) REFERENCES accounts (id)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Table structure for notification_histories
-- ----------------------------
DROP TABLE IF EXISTS notification_histories;
CREATE TABLE notification_histories (
  id BIGINT NOT NULL AUTO_INCREMENT,
  account_id BIGINT DEFAULT NULL,
  body VARCHAR(255) DEFAULT NULL,
  created_at VARCHAR(255) DEFAULT NULL,
  data VARCHAR(255) DEFAULT NULL,
  image VARCHAR(255) DEFAULT NULL,
  is_read BIT(1) NOT NULL,
  title VARCHAR(255) DEFAULT NULL,
  type ENUM('BILL_REQUEST') DEFAULT NULL,
  updated_at VARCHAR(255) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `accounts` (`id`, `created_at`, `email`, `is_two_factor`, `password`, `secret_key`, `selected_network_id`, `updated_at`, `url_avatar`, `username`) VALUES (1, '2024-12-20 15:46:35.338469', 'an@yopmail.com', b'0', '$2a$10$rqJ2GH4XHI56iDDEqbTIeuicK7jvFK3vCcFy7dDOpUZZtQKTGVLu6', NULL, NULL, '2024-12-20 15:46:35.338615', NULL, 'Phan Hoài An');
INSERT INTO `accounts` (`id`, `created_at`, `email`, `is_two_factor`, `password`, `secret_key`, `selected_network_id`, `updated_at`, `url_avatar`, `username`) VALUES (2, '2024-12-20 15:47:31.435666', 'tien@yopmal.com', b'0', '$2a$10$BuXHBz1fbU6/NTc3zK5U.uns56BLMVN8QWc3kV4Ss5/IrCMC1CkxW', NULL, NULL, '2024-12-20 15:47:31.435685', NULL, 'Nguyễn Văn TIển');

INSERT INTO `notification_histories` (`id`, `account_id`, `body`, `created_at`, `data`, `image`, `is_read`, `title`, `type`, `updated_at`) VALUES (1, 1, 'Số tiền cần thanh toán: 50000.0', '2024-12-20 08:50:30.918955', '{billId=1, billRequestId=1, networkId=1}', NULL, b'1', 'Bạn có một yêu cầu thanh toán mới từ Nguyễn Văn TIển', 'BILL_REQUEST', '2024-12-20 08:50:38.690941');
EOF

# Copy the SQL file to the container
docker cp $temp_sql_file $MYSQL_CONTAINER:/tmp/setup_db.sql

# Execute the SQL script inside the container
docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD < /tmp/setup_db.sql

# Clean up temporary file
rm $temp_sql_file
docker exec -i $MYSQL_CONTAINER rm /tmp/setup_db.sql

echo "Database and data account setup complete!"