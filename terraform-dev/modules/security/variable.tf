variable "region" {
  type = string
  default = "ap-east-1"
}
variable "vpc_id" {
  type = string
  description = "The VPC ID"
  nullable = false
  
}