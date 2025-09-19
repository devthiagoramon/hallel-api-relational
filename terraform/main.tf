
variable "domain_name" {
  type        = string
  description = "O domínio raiz, ex: comunidadecatolicahallel.com.br"
  default     = "comunidadecatolicahallel.com.br"
}

# Variável para o subdomínio da API
variable "api_domain_name" {
  type    = string
  default = "api.comunidadecatolicahallel.com.br"
}

data "aws_route53_zone" "primary" {
  name = var.domain_name
}

resource "aws_acm_certificate" "cert" {
  provider                  = aws.us_east_1 # Certificados para ALB/CloudFront devem estar em us-east-1
  domain_name               = var.domain_name
  # ADICIONADO O SUBDOMÍNIO DA API AQUI
  subject_alternative_names = [var.api_domain_name]
  validation_method         = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}


# -- CONFIGURAÇÃO DO BANCO DE DADOS --
# Para segurança, gere uma senha aleatória para o banco de dados
resource "random_password" "db_password" {
  length  = 16
  special = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}


# Criação da instância do banco de dados PostgreSQL no RDS
resource "aws_db_instance" "hallel_db_prod" {
  allocated_storage    = 20
  engine               = "postgres"
  engine_version       = "16.10"
  instance_class       = "db.t3.micro"
  storage_type = "gp2"

  identifier           = "hallel-db-${var.environment}"

  db_name              = "hallel_db"
  username             = "halleladmin"
  password             = random_password.db_password.result

  # -- SEGURANÇA --
  publicly_accessible  = false
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name

  multi_az = true
  backup_retention_period = 7
  skip_final_snapshot = false
  final_snapshot_identifier = "hallel-db-${var.environment}-final-snapshot"

  apply_immediately = false
  auto_minor_version_upgrade = true

  tags = {
    Name = "hallel-db-${var.environment}"
    Environment = var.environment
  }

  # Ignora mudanças na senha se ela for gerenciada fora do Terraform (ex: rotação de senhas)
  lifecycle {
    ignore_changes = [password]
  }
}

# --- FIM DA CONFIGURAÇÃO DO BANCO ----

# --- CONFIGURAÇÃO DO SERVIDOR ---

resource "aws_iam_role" "app_server_role" {
  name = "ec2-role-secrets-manager-${var.environment}"

  assume_role_policy = jsonencode({
    Version   = "2012-10-17",
    Statement = [
      {
        Action    = "sts:AssumeRole",
        Effect    = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_key_pair" "app_ssh_key" {
  key_name   = "hallel-app-key-prod" # Nome que aparecerá no painel da AWS
  public_key = file("~/.ssh/hallel_app_key.pub") # Caminho para sua chave pública
}

resource "aws_iam_instance_profile" "app_profile" {
  name = "ec2-instance-profile-${var.environment}"
  role = aws_iam_role.app_server_role.name
}

data "aws_ami" "amazon_linux_2" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}



# Exemplo de uma instância EC2 para rodar a aplicação
resource "aws_instance" "app_server" {
  # Amazon Machine Image - Amazon Linux 2 (gratuito e comum)
  ami           = data.aws_ami.amazon_linux_2.id # Verifique o AMI ID mais recente para sua região (us-east-2)
  instance_type = "t2.micro"             # Instância do Free Tier da AWS

  # AQUI a mágica acontece: associa a chave SSH criada no passo anterior
  key_name = aws_key_pair.app_ssh_key.key_name

  # Associa a instância à sua rede e security group
  subnet_id                   = aws_subnet.public.id
  vpc_security_group_ids      = [aws_security_group.app_sg.id]

  user_data = file("${path.module}/install-nginx-certbot.sh")

  iam_instance_profile = aws_iam_instance_profile.app_profile.name

  tags = {
    Name = "hallel-app-server-${var.environment}"
  }
}

resource "aws_eip" "app_eip" {
  domain = "vpc"
  tags = {
    Name = "eip-${var.environment}"
  }
}

resource "aws_eip_association" "eip_assoc" {
  instance_id   = aws_instance.app_server.id
  allocation_id = aws_eip.app_eip.id
}

