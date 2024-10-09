terraform {
  required_version = ">= 1.5.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = ">= 5.0.0"
    }
  }
}

provider "aws" {
  region = var.region
}

#Create a complete VPC using module networking
module "networking" {
  source              = "../modules/networking"
  region              = var.region
  availability_zones  = var.availability_zones
  cidr_block          = var.cidr_block
  public_subnet_ips   = var.public_subnet_ips
  private_subnet_ips  = var.private_subnet_ips
}

module "security" {
  source = "../modules/security"
  region = var.region
  vpc_id = module.networking.vpc_id
}

module "load_balance" {
  source                 = "../modules/load_balance"
  region                 = var.region
  vpc_id                 = module.networking.vpc_id
  load_balance_subnet_ids = module.networking.public_subnet_ids
  load_balance_security_group_ids = [
    module.security.public_security_group_id
  ]
}

module "ecs_cluster"{
  source = "../modules/ecs_cluster"
  region = var.region
  vpc_id = module.networking.vpc_id
  ecs_subnet_ids = module.networking.private_subnet_ids
  ecs_security_group_ids = [
    module.security.private_security_group_id
  ]
  alb_arn = module.load_balance.alb_arn
  ecr_repo_config_server_url       = var.ecr_repo_config_server_url
  ecr_repo_discovery_service_url   = var.ecr_repo_discovery_service_url
  ecr_repo_api_gateway_url         = var.ecr_repo_api_gateway_url
  discovery_service_target_group_arn = module.load_balance.discovery_service_target_group_arn
  api_gateway_service_target_group_arn = module.load_balance.api_gateway_service_target_group_arn
  ecr_repo_account_service_url = var.ecr_repo_account_service_url
  ecr_repo_mysql_url = var.ecr_repo_mysql_url
  ecr_repo_network_service_url = var.ecr_repo_network_service_url
  ecr_repo_transaction_service_url = var.ecr_repo_transaction_service_url
  ecr_repo_wallet_service_url = var.ecr_repo_wallet_service_url
}
