B1: build image push ECR
Example: 
+ aws ecr get-login-password --region ap-east-1 | docker login --username AWS --password-stdin 637423209325.dkr.ecr.ap-east-1.amazonaws.com
+ docker build -t config-server .
+ docker tag config-server:latest 637423209325.dkr.ecr.ap-east-1.amazonaws.com/config-server:latest
+ docker push 637423209325.dkr.ecr.ap-east-1.amazonaws.com/config-service:latest