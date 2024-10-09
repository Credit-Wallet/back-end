output "alb_arn" {
  value = aws_lb.load_balancer.arn
}

output "alb_dns" {
  value = aws_lb.load_balancer.dns_name
}

output "discovery_service_target_group_arn" {
  value = aws_lb_target_group.discovery_service_target_group.arn
}

output "api_gateway_service_target_group_arn" {
  value = aws_lb_target_group.api_gateway_service_target_group.arn
}