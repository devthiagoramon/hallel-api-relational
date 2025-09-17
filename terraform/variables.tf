variable "aws_region" {
  description = "A região da AWS para criar os recursos."
  type        = string
  default     = "us-east-1"
}

variable "vpc_cidr" {
  description = "O bloco CIDR para a VPC."
  type        = string
  default     = "10.0.0.0/16"
}

variable "environment" {
  description = "O nome do ambiente (ex: prod, staging)."
  type        = string
  default     = "prod"
}