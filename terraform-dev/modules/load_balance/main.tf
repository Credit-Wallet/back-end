# Application Load Balancer
resource "aws_lb" "load_balancer" {
  name               = "credit-wallet-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = var.load_balance_security_group_ids
  subnets            = var.load_balance_subnet_ids
  enable_deletion_protection = false
  enable_http2               = true
  idle_timeout               = 60
  enable_cross_zone_load_balancing = true
  tags = {
    Name = "credit-wallet-alb"
  }
}

# Load Balancer Listener for HTTP (port 80)
resource "aws_lb_listener" "listener_http" {
  load_balancer_arn = aws_lb.load_balancer.arn
  port              = "80"
  protocol          = "HTTP"
  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.api_gateway_service_target_group.arn
  }
}

resource "aws_lb_target_group" "discovery_service_target_group" {
  name        = "discovery-service-tg"
  port        = 8761
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"

  health_check {
    port               = "traffic-port"
    protocol           = "HTTP"
    path               = "/"
    interval           = 30
    timeout            = 5
    healthy_threshold  = 2
    unhealthy_threshold = 3
  }
}

# Listener Rule for Discovery Service
resource "aws_lb_listener_rule" "discovery_service_rule" {
  listener_arn = aws_lb_listener.listener_http.arn
  priority     = 50

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.discovery_service_target_group.arn
  }
  
  condition {
    path_pattern {
      values = ["/eureka/*"]
    }
  }
}

resource "aws_lb_target_group" "api_gateway_service_target_group" {
  name        = "api-gateway-service-tg"
  port        = 8888
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"
}

# Listener Rule for Discovery Service
resource "aws_lb_listener_rule" "api_gateway_service_rule" {
  listener_arn = aws_lb_listener.listener_http.arn
  priority     = 150

  action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.api_gateway_service_target_group.arn
  }

  condition {
    path_pattern {
      values = ["/*"]
    }
  }
}