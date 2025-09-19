terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    random = {
      source  = "hashicorp/random"
      version = "~> 3.5"
    }
  }
}

# Provedor principal para a sua região
provider "aws" {
  region = var.aws_region
}

# Provedor ALIAS para a região us-east-1, especificamente para o certificado ACM
provider "aws" {
  alias  = "us_east_1"
  region = "us-east-1"
}