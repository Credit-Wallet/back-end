resource "aws_security_group" "public_security_group" {
  name        = "public_security_group"
  description = "Public security group for internet facing services"
  vpc_id      = var.vpc_id

  # HTTP access from the internet
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # HTTPS access from the internet
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Optional: SSH access (you might want to limit this to your IP)
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # Consider restricting this to specific IPs
  }

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}


resource "aws_security_group" "private_security_group" {
  name        = "private_security_group"
  description = "Private security group for internal services"
  vpc_id      = var.vpc_id

  # Allow HTTP traffic from public services (e.g., API Gateway)
  ingress {
    from_port       = 80
    to_port         = 80
    protocol        = "tcp"
    security_groups = [aws_security_group.public_security_group.id]
  }

  # Allow API Gateway traffic on port 8888
  ingress {
    from_port       = 8888
    to_port         = 8888
    protocol        = "tcp"
    security_groups = [aws_security_group.public_security_group.id]
  }

  # Allow traffic to Config Server on port 8000
  ingress {
    from_port       = 8000
    to_port         = 8000
    protocol        = "tcp"
    security_groups = [aws_security_group.public_security_group.id]
  }

  # Allow Eureka Discovery Service traffic on port 8761
  ingress {
    from_port       = 8761
    to_port         = 8761
    protocol        = "tcp"
    security_groups = [aws_security_group.public_security_group.id]
  }

  ingress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Allow all outbound traffic
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}