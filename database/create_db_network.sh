#!/bin/bash

# Set MySQL credentials
MYSQL_CONTAINER="back-end-mysql-1"
MYSQL_USER="root"
MYSQL_PASSWORD="Ak)%@b)S1uI8"
MYSQL_HOST="127.0.0.1"

# Temporary SQL file
temp_sql_file="/tmp/setup_db_network.sql"

# Create the SQL script
cat <<'EOF' > $temp_sql_file
CREATE DATABASE IF NOT EXISTS `network`;
USE `network`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `networks`;
CREATE TABLE `networks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `balance` double NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `currency` enum('CNY','EUR','JPY','USD','VND') DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `max_balance` double NOT NULL,
  `max_member` bigint DEFAULT NULL,
  `min_balance` double NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `private_key` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `wallet_address` varchar(255) DEFAULT NULL,
  `wallet_path` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `networks` (`id`, `balance`, `created_at`, `currency`, `description`, `max_balance`, `max_member`, `min_balance`, `name`, `password`, `private_key`, `updated_at`, `uuid`, `wallet_address`, `wallet_path`) VALUES (1, 75000000, '2024-12-20 15:49:12.160483', 'VND', 'không', 5000000, 15, 5000000, 'Group An and Tiển', 'Group An and Tiển', '4e618f77ef597fc68e740a0ffd0da6d63f46331287031fb59e7bc0b1a8ec1b2e', '2024-12-20 15:49:25.217299', 'd6e5e502-ef6f-4934-9171-cdb173b2b258', '0xa3e62c816b5a704f1d387baf232e27bc6f8c799c', 'UTC--2024-12-20T08-49-20.910244698Z--a3e62c816b5a704f1d387baf232e27bc6f8c799c.json');
EOF

# Copy the SQL file to the container
docker cp $temp_sql_file $MYSQL_CONTAINER:/tmp/setup_db_network.sql

# Execute the SQL script inside the container
docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD < /tmp/setup_db_network.sql

# Clean up temporary file
rm $temp_sql_file
docker exec -i $MYSQL_CONTAINER rm /tmp/setup_db_network.sql

echo "Database and data network setup complete!"