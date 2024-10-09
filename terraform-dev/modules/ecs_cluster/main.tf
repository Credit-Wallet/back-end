# ECS Cluster
resource "aws_ecs_cluster" "credit_wallet_ecs_cluster" {
  name = "credit-wallet-ecs-cluster"
}

resource "aws_service_discovery_private_dns_namespace" "internal_namespace" {
  name        = "internal"
  description = "Private DNS namespace for ECS services"
  vpc         = var.vpc_id
}
#----------------------------------------------------------------------------------------------------------------------
# IAM Role for ECS Task Execution
resource "aws_iam_role" "task_execution_role" {
  name = "config-server-task-execution-role"

  assume_role_policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
POLICY
}

# IAM Policy for ECS Task Execution Role
resource "aws_iam_policy" "task_execution_policy" {
  name        = "config-server-task-execution-policy"
  description = "Policy for ECS task execution role"

  policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:GetDownloadUrlForLayer",
        "ecr:GetRepositoryPolicy",
        "ecr:DescribeRepositories",
        "ecr:ListImages",
        "ecr:DescribeImages",
        "ecr:BatchGetImage",
        "logs:CreateLogStream",
        "logs:PutLogEvents"
      ],
      "Resource": "*"
    },
    {
      "Effect": "Allow",
      "Action": [
        "ssmmessages:CreateControlChannel",
        "ssmmessages:CreateDataChannel",
        "ssmmessages:OpenControlChannel",
        "ssmmessages:OpenDataChannel",
        "ssm:UpdateInstanceInformation"
      ],
      "Resource": "*"
    }
  ]
}
POLICY
}

# IAM Role for ECS Task
resource "aws_iam_role" "task_role" {
  name = "config-server-task-role"

  assume_role_policy = <<POLICY
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Service": "ecs-tasks.amazonaws.com"
      },
      "Action": "sts:AssumeRole"
    }
  ]
}
POLICY
}

# Attach IAM Policy to Task Role (if needed for specific AWS service access)
resource "aws_iam_role_policy_attachment" "task_role_policy_attachment" {
  role       = aws_iam_role.task_role.name
  policy_arn = aws_iam_policy.task_execution_policy.arn
}

# Attach IAM Policy to IAM Role
resource "aws_iam_role_policy_attachment" "task_execution_policy_attachment" {
  role       = aws_iam_role.task_execution_role.name
  policy_arn = aws_iam_policy.task_execution_policy.arn
}
#----------------------------------------------------------------------------------------------------------------------
# Config Server
resource "aws_ecs_task_definition" "config_server_task_definition" {
  family                   = "config-server-task-definition"
  task_role_arn            = aws_iam_role.task_role.arn
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "config-server",
      "image": "${var.ecr_repo_config_server_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8000,
          "hostPort": 8000,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "SPRING_PROFILES_ACTIVE",
          "value": "native"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_SERVER_NATIVE_SEARCH_LOCATIONS",
          "value": "file:/app/configurations/"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.config_server_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "config-server"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "config_server_log_group" {
  name = "ecs/config-server"
  retention_in_days = 7
}

resource "aws_service_discovery_service" "config_server_service" {
  name        = "config-server"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_ecs_service" "config-server" {
  name            = "config-server"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.config_server_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.config_server_service.arn
  }
  
  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  enable_execute_command = true
}
#----------------------------------------------------------------------------------------------------------------------
# Discovery Service
resource "aws_ecs_task_definition" "discovery_service_task_definition" {
  family                   = "discovery-service-task-definition"
  task_role_arn            = aws_iam_role.task_role.arn
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "discovery-service",
      "image": "${var.ecr_repo_discovery_service_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8761,
          "hostPort": 8761,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "EUREKA_INSTANCE_HOSTNAME",
          "value": "discovery-service.internal"
        },
        {
          "name": "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
          "value": "http://discovery-service.internal:8761/eureka/"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_URI",
          "value": "http://config-server.internal:8000"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.discovery_service_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "discovery-service"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "discovery_service_log_group" {
  name = "ecs/discovery-service"
  retention_in_days = 7
}

resource "aws_service_discovery_service" "discovery_service_service" {
  name        = "discovery-service"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_ecs_service" "discovery_service" {
  name            = "discovery-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.discovery_service_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  
  # Service Discovery Configuration
  service_registries {
      registry_arn = aws_service_discovery_service.discovery_service_service.arn
  }

  load_balancer {
    target_group_arn = var.discovery_service_target_group_arn
    container_name   = "discovery-service"
    container_port   = 8761
  }

  enable_execute_command = true
}
#----------------------------------------------------------------------------------------------------------------------
# API Gateway
resource "aws_ecs_task_definition" "api_gateway_task_definition" {
  family                   = "api-gateway-task-definition"
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "api-gateway",
      "image": "${var.ecr_repo_api_gateway_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8888,
          "hostPort": 8888,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "EUREKA_INSTANCE_HOSTNAME",
          "value": "api-gateway-service.internal"
        },
        {
          "name": "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
          "value": "http://discovery-service.internal:8761/eureka/"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_URI",
          "value": "http://config-server.internal:8000"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.api_gateway_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "api-gateway-service"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "api_gateway_log_group" {
  name = "ecs/api-gateway-service"
  retention_in_days = 7
}

resource "aws_service_discovery_service" "api_gateway_service" {
  name        = "api-gateway-service"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_ecs_service" "api_gateway" {
  name            = "api-gateway-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.api_gateway_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  
  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.api_gateway_service.arn
  }

  load_balancer {
    target_group_arn = var.api_gateway_service_target_group_arn
    container_name   = "api-gateway"
    container_port   = 8888
  }
}
#----------------------------------------------------------------------------------------------------------------------
#Mysql
resource "aws_ecs_task_definition" "mysql_task_definition" {
  family                   = "mysql-task-definition"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  task_role_arn            = aws_iam_role.task_role.arn

  container_definitions = <<DEFINITION
  [
    {
      "name": "mysql",
      "image": "${var.ecr_repo_mysql_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 3306,
          "hostPort": 3306,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "MYSQL_ROOT_PASSWORD",
          "value": "123456"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.mysql_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "mysql"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "mysql_log_group" {
  name              = "ecs/mysql"
  retention_in_days = 7
}

resource "aws_service_discovery_service" "mysql_service" {
  name        = "mysql"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

resource "aws_ecs_service" "mysql_service" {
  name            = "mysql-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.mysql_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.mysql_service.arn
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200
  enable_execute_command = true
}
#----------------------------------------------------------------------------------------------------------------------
#Account-service
resource "aws_ecs_task_definition" "account_service_task_definition" {
  family                   = "account-service-task-definition"
  task_role_arn            = aws_iam_role.task_role.arn
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "account-service",
      "image": "${var.ecr_repo_account_service_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8001,
          "hostPort": 8001,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "EUREKA_INSTANCE_HOSTNAME",
          "value": "api-gateway-service.internal"
        },
        {
          "name": "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
          "value": "http://discovery-service.internal:8761/eureka/"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_URI",
          "value": "http://config-server.internal:8000"
        },
        {
          "name": "DATABASE_URL",
          "value": "jdbc:mysql://mysql.internal:3306/account"
        },
        {
          "name": "DATABASE_USERNAME",
          "value": "root"
        },
        {
          "name": "DATABASE_PASSWORD",
          "value": "123456"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.account_service_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "account-service"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "account_service_log_group" {
  name              = "ecs/account-service"
  retention_in_days = 7
}

# Service Discovery for Account Service
resource "aws_service_discovery_service" "account_service" {
  name        = "account-service"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

# ECS Service for Account Service
resource "aws_ecs_service" "account_service" {
  name            = "account-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.account_service_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200

  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.account_service.arn
  }

  enable_execute_command = true
}
#----------------------------------------------------------------------------------------------------------------------
#Network-service
resource "aws_ecs_task_definition" "network_service_task_definition" {
  family                   = "network-service-task-definition"
  task_role_arn            = aws_iam_role.task_role.arn
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "network-service",
      "image": "${var.ecr_repo_network_service_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8003,
          "hostPort": 8003,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "EUREKA_INSTANCE_HOSTNAME",
          "value": "network-service.internal"
        },
        {
          "name": "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
          "value": "http://discovery-service.internal:8761/eureka/"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_URI",
          "value": "http://config-server.internal:8000"
        },
        {
          "name": "DATABASE_URL",
          "value": "jdbc:mysql://mysql.internal:3306/network"
        },
        {
          "name": "DATABASE_USERNAME",
          "value": "root"
        },
        {
          "name": "DATABASE_PASSWORD",
          "value": "123456"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.network_service_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "network-service"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "network_service_log_group" {
  name              = "ecs/network-service"
  retention_in_days = 7
}

# Service Discovery for Network Service
resource "aws_service_discovery_service" "network_service" {
  name        = "network-service"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

# ECS Service for Network Service
resource "aws_ecs_service" "network_service" {
  name            = "network-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.network_service_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200

  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.network_service.arn
  }

  enable_execute_command = true
}
#----------------------------------------------------------------------------------------------------------------------
#Wallet-service
resource "aws_ecs_task_definition" "wallet_service_task_definition" {
  family                   = "wallet-service-task-definition"
  task_role_arn            = aws_iam_role.task_role.arn
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "wallet-service",
      "image": "${var.ecr_repo_wallet_service_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8002,
          "hostPort": 8002,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "EUREKA_INSTANCE_HOSTNAME",
          "value": "wallet-service.internal"
        },
        {
          "name": "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
          "value": "http://discovery-service.internal:8761/eureka/"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_URI",
          "value": "http://config-server.internal:8000"
        },
        {
          "name": "DATABASE_URL",
          "value": "jdbc:mysql://mysql.internal:3306/wallet"
        },
        {
          "name": "DATABASE_USERNAME",
          "value": "root"
        },
        {
          "name": "DATABASE_PASSWORD",
          "value": "123456"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.wallet_service_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "wallet-service"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "wallet_service_log_group" {
  name              = "ecs/wallet-service"
  retention_in_days = 7
}

# Service Discovery for Network Service
resource "aws_service_discovery_service" "wallet_service" {
  name        = "wallet-service"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

# ECS Service for Network Service
resource "aws_ecs_service" "wallet_service" {
  name            = "wallet-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.wallet_service_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200

  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.wallet_service.arn
  }

  enable_execute_command = true
}
#----------------------------------------------------------------------------------------------------------------------
#Transaction-service
resource "aws_ecs_task_definition" "transaction_service_task_definition" {
  family                   = "transaction-service-task-definition"
  task_role_arn            = aws_iam_role.task_role.arn
  execution_role_arn       = aws_iam_role.task_execution_role.arn
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = "512"
  memory                   = "1024"

  container_definitions = <<DEFINITION
  [
    {
      "name": "transaction-service",
      "image": "${var.ecr_repo_transaction_service_url}",
      "cpu": 512,
      "memory": 1024,
      "portMappings": [
        {
          "containerPort": 8004,
          "hostPort": 8004,
          "protocol": "tcp"
        }
      ],
      "environment": [
        {
          "name": "EUREKA_INSTANCE_HOSTNAME",
          "value": "transaction-service.internal"
        },
        {
          "name": "EUREKA_CLIENT_SERVICEURL_DEFAULTZONE",
          "value": "http://discovery-service.internal:8761/eureka/"
        },
        {
          "name": "SPRING_CLOUD_CONFIG_URI",
          "value": "http://config-server.internal:8000"
        },
        {
          "name": "DATABASE_URL",
          "value": "jdbc:mysql://mysql.internal:3306/wallet"
        },
        {
          "name": "DATABASE_USERNAME",
          "value": "root"
        },
        {
          "name": "DATABASE_PASSWORD",
          "value": "123456"
        }
      ],
      "logConfiguration": {
        "logDriver": "awslogs",
        "options": {
          "awslogs-group": "${aws_cloudwatch_log_group.transaction_service_log_group.name}",
          "awslogs-region": "${var.region}",
          "awslogs-stream-prefix": "transaction-service"
        }
      }
    }
  ]
  DEFINITION
}

resource "aws_cloudwatch_log_group" "transaction_service_log_group" {
  name              = "ecs/transaction-service"
  retention_in_days = 7
}

# Service Discovery for Transaction Service
resource "aws_service_discovery_service" "transaction_service" {
  name        = "transaction-service"
  namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id

  dns_config {
    dns_records {
      type = "A"
      ttl  = 60
    }
    namespace_id = aws_service_discovery_private_dns_namespace.internal_namespace.id
  }

  health_check_custom_config {
    failure_threshold = 1
  }
}

# ECS Service for Transaction Service
resource "aws_ecs_service" "transaction_service" {
  name            = "transaction-service"
  cluster         = aws_ecs_cluster.credit_wallet_ecs_cluster.id
  task_definition = aws_ecs_task_definition.transaction_service_task_definition.arn
  desired_count   = 1
  launch_type     = "FARGATE"

  network_configuration {
    subnets         = var.ecs_subnet_ids
    security_groups = var.ecs_security_group_ids
    assign_public_ip = false
  }

  deployment_minimum_healthy_percent = 50
  deployment_maximum_percent         = 200

  # Service Discovery Configuration
  service_registries {
    registry_arn = aws_service_discovery_service.transaction_service.arn
  }

  enable_execute_command = true
}