# Create VPC
module "vpc" {
  source  = "terraform-aws-modules/vpc/aws"
  version = "5.0.0"

  name = "Credit Wallet VPC"
  cidr = var.cidr_block

  azs             = var.availability_zones
  public_subnets  = var.public_subnet_ips
  private_subnets = var.private_subnet_ips

  enable_nat_gateway = true
  enable_vpn_gateway = false
  single_nat_gateway = true
  tags = {
    Name = "Credit Wallet VPC"
  }
}