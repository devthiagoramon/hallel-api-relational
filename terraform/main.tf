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
  provider = aws.us_east_1 # Certificados para ALB/CloudFront devem estar em us-east-1
  domain_name = var.domain_name
  # ADICIONADO O SUBDOMÍNIO DA API AQUI
  subject_alternative_names = [var.api_domain_name]
  validation_method = "DNS"

  lifecycle {
    create_before_destroy = true
  }
}


# -- CONFIGURAÇÃO DO BANCO DE DADOS --
# Para segurança, gere uma senha aleatória para o banco de dados
resource "random_password" "db_password" {
  length           = 16
  special          = true
  override_special = "!#$%&*()-_=+[]{}<>:?"
}

resource "aws_db_instance" "hallel_db_instance" {
  # --- PONTO CHAVE DA MIGRAÇÃO ---
  # Use o ARN ou o nome do snapshot manual que você criou
  # snapshot_identifier  = "arn:aws:rds:us-east-1:774538498711:cluster-snapshot:snapshot-hallel-db-prod-17-11-2025" # <-- COLE O ARN DO SEU SNAPSHOT AQUI

  # Identificador para a nova instância
  identifier = "hallel-db-prod-instance"

  # Configuração de Custo
  instance_class = "db.t4g.micro" # Opção ARM barata (verifique se sua região suporta)
  # ou "db.t3.micro" (Opção Intel)
  multi_az       = false          # Mais barato (sem alta disponibilidade)

  # Armazenamento (Obrigatório para RDS padrão)
  allocated_storage = 20             # Mínimo em GB (ou o tamanho do seu banco antigo, o que for maior)
  storage_type = "gp3"          # Novo padrão, geralmente mais barato e eficiente que gp2
  max_allocated_storage = 100           # Permite crescer até 100GB se precisar

  # Motor (Engine)
  engine = "postgres"
  engine_version = "16.10"


  db_name  = "hallel_db"
  username = "halleladmin"
  password = random_password.db_password.result

  # Usa os recursos de rede que você já definiu em networking.tf
  db_subnet_group_name = aws_db_subnet_group.rds_subnet_group.name
  vpc_security_group_ids = [aws_security_group.db_sg.id]
  publicly_accessible  = false # Mais seguro

  # Configurações de Backup
  backup_retention_period   = 7
  skip_final_snapshot       = false
  final_snapshot_identifier = "hallel-db-prod-final-snapshot-${formatdate("YYYY-MM-DD-hh-mm-ss", timestamp())}"

  tags = {
    Name        = "hallel-db-${var.environment}-instance"
    Environment = var.environment
  }
}

# --- FIM DA CONFIGURAÇÃO DO BANCO ----


# --- CONFIGURAÇÃO DO SERVIDOR ---

resource "aws_iam_role" "app_server_role" {
  name = "ec2-role-secrets-manager-${var.environment}"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_key_pair" "app_ssh_key" {
  key_name = "hallel-app-key-prod" # Nome que aparecerá no painel da AWS
  public_key = file("~/.ssh/hallel_app_key.pub") # Caminho para sua chave pública
}

resource "aws_iam_instance_profile" "app_profile" {
  name = "ec2-instance-profile-${var.environment}"
  role = aws_iam_role.app_server_role.name
}

data "aws_ami" "amazon_linux_2" {
  most_recent = true
  owners = ["amazon"]

  filter {
    name = "name"
    values = ["amzn2-ami-hvm-*-x86_64-gp2"]
  }

  filter {
    name = "virtualization-type"
    values = ["hvm"]
  }
}


# Exemplo de uma instância EC2 para rodar a aplicação
resource "aws_instance" "app_server" {
  # Amazon Machine Image - Amazon Linux 2 (gratuito e comum)
  ami = data.aws_ami.amazon_linux_2.id # Verifique o AMI ID mais recente para sua região (us-east-2)
  instance_type = "t2.micro"             # Instância do Free Tier da AWS

  # AQUI a mágica acontece: associa a chave SSH criada no passo anterior
  key_name = aws_key_pair.app_ssh_key.key_name

  # Associa a instância à sua rede e security group
  subnet_id = aws_subnet.public.id
  vpc_security_group_ids = [aws_security_group.app_sg.id]

  user_data = file("${path.module}/install-docker.sh")

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

