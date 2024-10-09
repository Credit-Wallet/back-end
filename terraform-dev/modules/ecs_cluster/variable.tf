variable "region" {
  type = string
  default = "ap-east-1"
}

variable "vpc_id" {
  type = string
  description = "The VPC ID to ALB and Target Group"
  nullable = false
}

variable "ecs_subnet_ids" {
  type = list(string)
  description = "The subnet IDs to launch ECS Service"
  nullable = false  
}

variable "ecs_security_group_ids" {
  type = list(string)
  nullable = false
}
variable "alb_arn" {
  type = string
  description = "The ARN of the Application Load Balancer"
  nullable = false
}

# variable "ecr_repo_mysql_url" {
#     type = string
#     description = "The URI of the ECR repository for the MySQL database"
#     nullable = false
# }

variable "ecr_repo_config_server_url" {
    type = string
    description = "The URI of the ECR repository for the Config Server"
    nullable = false
}

variable "ecr_repo_discovery_service_url" {
    type = string
    description = "The URI of the ECR repository for the Discovery Service"
    nullable = false
}

variable "ecr_repo_api_gateway_url" {
  type = string
  description = "The URI of the ECR repository for the API Gateway"
  nullable = false
}

variable "discovery_service_target_group_arn" {
  type = string
  description = "The ARN of the target group for the Discovery Service"
  nullable = false
}

variable "api_gateway_service_target_group_arn" {
  type = string
  description = "The ARN of the target group for the API Gateway"
  nullable = false
}

variable "ecr_repo_wallet_service_url" {
    type = string
    description = "The URI of the ECR repository for the Wallet Service"
    nullable = false
}

variable "ecr_repo_transaction_service_url" {
    type = string
    description = "The URI of the ECR repository for the Transaction Service"
    nullable = false
}

variable "ecr_repo_account_service_url" {
    type = string
    description = "The URI of the ECR repository for the Account Service"
    nullable = false
}

variable "ecr_repo_mysql_url" {
    type = string
    description = "The URI of the ECR repository for the Account Service"
    nullable = false
}

variable "ecr_repo_network_service_url" {
    type = string
    description = "The URI of the ECR repository for the Network Service"
    nullable = false
}