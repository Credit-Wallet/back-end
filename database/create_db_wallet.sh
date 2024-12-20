#!/bin/bash

# Set MySQL credentials
MYSQL_CONTAINER="back-end-mysql-1"
MYSQL_USER="root"
MYSQL_PASSWORD="Ak)%@b)S1uI8"
MYSQL_HOST="127.0.0.1"

# Temporary SQL file
temp_sql_file="/tmp/setup_db_wallet.sql"

# Create the SQL script
cat <<'EOF' > $temp_sql_file
CREATE DATABASE IF NOT EXISTS `wallet`;
USE `wallet`;
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `wallets`;
CREATE TABLE `wallets` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `account_id` bigint DEFAULT NULL,
  `balance` double NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `debt` double NOT NULL,
  `network_id` bigint DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `private_key` varchar(255) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `wallet_address` varchar(255) DEFAULT NULL,
  `wallet_path` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

SET FOREIGN_KEY_CHECKS = 1;

-- ----------------------------
-- Records of accounts
-- ----------------------------
INSERT INTO `wallets` (`id`, `account_id`, `balance`, `created_at`, `debt`, `network_id`, `password`, `private_key`, `updated_at`, `wallet_address`, `wallet_path`) VALUES (1, 2, 50000, '2024-12-20 15:49:17.663884', 0, 1, 'tien@yopmal.com', 'ecff604debf12db125ba8bf99b6261cb9f401e62b7b9aebb4d7b78a4de5e24f8', '2024-12-20 15:50:49.598161', '0x49c7301dd964c3754325dd6e3057435519d153e7', 'UTC--2024-12-20T08-49-15.479641567Z--49c7301dd964c3754325dd6e3057435519d153e7.json');
INSERT INTO `wallets` (`id`, `account_id`, `balance`, `created_at`, `debt`, `network_id`, `password`, `private_key`, `updated_at`, `wallet_address`, `wallet_path`) VALUES (2, 1, -50000, '2024-12-20 15:50:12.655467', 50000, 1, 'an@yopmail.com', 'af62d654a482d8aca4776fef2f2b5d07cbf5e9cd8fea4f572d3123a526d549ba', '2024-12-20 15:50:49.640599', '0x756535d85bc3401bfdb1a895dbe3b51017477503', 'UTC--2024-12-20T08-50-10.426496686Z--756535d85bc3401bfdb1a895dbe3b51017477503.json');
EOF

# Copy the SQL file to the container
docker cp $temp_sql_file $MYSQL_CONTAINER:/tmp/setup_db_wallet.sql

# Execute the SQL script inside the container
docker exec -i $MYSQL_CONTAINER mysql -u$MYSQL_USER -p$MYSQL_PASSWORD < /tmp/setup_db_wallet.sql

# Clean up temporary file
rm $temp_sql_file
docker exec -i $MYSQL_CONTAINER rm /tmp/setup_db_wallet.sql

echo "Database and data wallet setup complete!"