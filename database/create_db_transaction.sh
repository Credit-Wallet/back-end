#!/bin/bash

# Set MySQL credentials
MYSQL_CONTAINER="back-end-mysql-1"
MYSQL_USER="root"
MYSQL_PASSWORD="Ak)%@b)S1uI8"
MYSQL_HOST="127.0.0.1"

# Temporary SQL file
temp_sql_file="/tmp/setup_db_transaction.sql"

# Create the SQL script
cat <<'EOF' > $temp_sql_file
CREATE DATABASE IF NOT EXISTS `transaction`;
USE `transaction`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for bill_requests
-- ----------------------------
DROP TABLE IF EXISTS `bill_requests`;
CREATE TABLE `bill_requests` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint DEFAULT NULL,
  `amount` double NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','PENDING') DEFAULT NULL,
  `bill_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKhp2xvm6xygmn9rxnu2f63j06p` (`bill_id`),
  CONSTRAINT `FKhp2xvm6xygmn9rxnu2f63j06p` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of bill_requests
-- ----------------------------
BEGIN;
INSERT INTO `bill_requests` (`id`, `account_id`, `amount`, `created_at`, `description`, `status`, `bill_id`) VALUES (1, 1, 50000, '2024-12-20 15:50:30.118507', NULL, 'COMPLETED', 1);
INSERT INTO `bill_requests` (`id`, `account_id`, `amount`, `created_at`, `description`, `status`, `bill_id`) VALUES (2, 2, 50000, '2024-12-20 15:50:30.121256', NULL, 'COMPLETED', 1);
COMMIT;

-- ----------------------------
-- Table structure for bills
-- ----------------------------
DROP TABLE IF EXISTS `bills`;
CREATE TABLE `bills` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint DEFAULT NULL,
  `actual_amount` double NOT NULL,
  `amount` double NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `network_id` bigint DEFAULT NULL,
  `status` enum('CANCELLED','COMPLETED','PENDING') DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of bills
-- ----------------------------
BEGIN;
INSERT INTO `bills` (`id`, `account_id`, `actual_amount`, `amount`, `created_at`, `name`, `network_id`, `status`, `updated_at`) VALUES (1, 2, 50000, 100000, '2024-12-20 15:50:30.066418', 'tiền trả sữa', 1, 'COMPLETED', '2024-12-20 15:50:49.660567');
COMMIT;

-- ----------------------------
-- Table structure for flyway_schema_history
-- ----------------------------
DROP TABLE IF EXISTS `flyway_schema_history`;
CREATE TABLE `flyway_schema_history` (
  `installed_rank` int NOT NULL,
  `version` varchar(50) DEFAULT NULL,
  `description` varchar(200) NOT NULL,
  `type` varchar(20) NOT NULL,
  `script` varchar(1000) NOT NULL,
  `checksum` int DEFAULT NULL,
  `installed_by` varchar(100) NOT NULL,
  `installed_on` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `execution_time` int NOT NULL,
  `success` tinyint(1) NOT NULL,
  PRIMARY KEY (`installed_rank`),
  KEY `flyway_schema_history_s_idx` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of flyway_schema_history
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for transactions
-- ----------------------------
DROP TABLE IF EXISTS `transactions`;
CREATE TABLE `transactions` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint DEFAULT NULL,
  `amount` double NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `from_account_id` bigint DEFAULT NULL,
  `hash` varchar(255) DEFAULT NULL,
  `network_id` bigint DEFAULT NULL,
  `to_account_id` bigint DEFAULT NULL,
  `type` bit(1) NOT NULL,
  `bill_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq5xcgnnqi7dyywyihf7uimanw` (`bill_id`),
  CONSTRAINT `FKq5xcgnnqi7dyywyihf7uimanw` FOREIGN KEY (`bill_id`) REFERENCES `bills` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ----------------------------
-- Records of transactions
-- ----------------------------
BEGIN;
INSERT INTO `transactions` (`id`, `account_id`, `amount`, `created_at`, `from_account_id`, `hash`, `network_id`, `to_account_id`, `type`, `bill_id`) VALUES (1, 1, 50000, '2024-12-20 15:50:49.536696', 1, NULL, 1, 2, b'0', 1);
INSERT INTO `transactions` (`id`, `account_id`, `amount`, `created_at`, `from_account_id`, `hash`, `network_id`, `to_account_id`, `type`, `bill_id`) VALUES (2, 2, 50000, '2024-12-20 15:50:49.540095', 1, NULL, 1, 2, b'1', 1);
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
EOF

# Copy the SQL file to the container
docker cp $temp_sql_file $MYSQL_CONTAINER:/tmp/setup_db_transaction.sql

# Execute the SQL script inside the container
docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD < /tmp/setup_db_transaction.sql

# Clean up temporary file
rm $temp_sql_file
docker exec -i $MYSQL_CONTAINER rm /tmp/setup_db_transaction.sql

echo "Database and data transaction setup complete!"