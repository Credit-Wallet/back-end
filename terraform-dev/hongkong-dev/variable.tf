variable "region" {
  type = string
  default = "ap-east-1"
}

#parameters for networking module
variable "availability_zones" {
  type = list(string)
  nullable = false
}
variable "cidr_block" {
  type = string
  nullable = false
}
variable "public_subnet_ips" {
  type = list(string)
  nullable = false
  
}
variable "private_subnet_ips" {
  type = list(string)
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
