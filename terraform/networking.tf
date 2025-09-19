
resource "aws_vpc" "main" {
  cidr_block = var.vpc_cidr
  tags = {
    Name = "vpc-${var.environment}"
  }
}

resource "aws_subnet" "public" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
  availability_zone = "${var.aws_region}a"
  map_public_ip_on_launch = true
  tags = {
    Name = "subnet-public-${var.environment}"
  }
}

resource "aws_subnet" "private" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.2.0/24"
  availability_zone = "${var.aws_region}a"
  tags = {
    Name = "subnet-private-${var.environment}"
  }
}

resource "aws_subnet" "private_2" {
  vpc_id = aws_vpc.main.id
  cidr_block = "10.0.3.0/24"
  availability_zone = "${var.aws_region}b"
  tags = {
    Name = "subnet-private-${var.environment}"
  }
}

resource "aws_db_subnet_group" "rds_subnet_group" {
  name = "dbsubnetgroup-${var.environment}"
  subnet_ids = [aws_subnet.private.id, aws_subnet.private_2.id]
  tags = {
    Name = "dbsubnetgroup-${var.environment}"
  }
}

resource "aws_security_group" "app_sg" {
  name = "app-sg-${var.environment}"
  vpc_id = aws_vpc.main.id

  # Permite tráfego HTTP para validação e redirecionamento
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite tráfego HTTPS (o principal)
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  # Permite acesso SSH APENAS do seu IP
  ingress {
    description = "Permite acesso SSH para administracao"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["191.122.187.39/32"] # IMPORTANTE: Troque pelo seu IP (veja em meuip.com.br)
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "db_sg" {
  name        = "db-sg-${var.environment}"
  vpc_id      = aws_vpc.main.id

  # REGRA DE ENTRADA (INGRESS):
  # Permite tráfego na porta 5432 (PostgreSQL) SOMENTE se a origem for o
  # Security Group da aplicação ('app_sg').
  ingress {
    from_port       = 5432
    to_port         = 5432
    protocol        = "tcp"
    security_groups = [aws_security_group.app_sg.id] # MÁGICA DA SEGURANÇA!
  }

  # REGRA DE SAÍDA (EGRESS):
  # Permite que o DB responda a qualquer requisição que ele receber.
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "igw-${var.environment}"
  }
}

# 2. Crie uma Tabela de Rotas para a sub-rede pública
resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id

  # 3. Crie uma rota padrão que aponta todo o tráfego da internet (0.0.0.0/0)
  # para o Internet Gateway que acabamos de criar.
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "public-rt-${var.environment}"
  }
}

# 4. Associe a Tabela de Rotas à sua sub-rede pública
resource "aws_route_table_association" "public_assoc" {
  subnet_id      = aws_subnet.public.id
  route_table_id = aws_route_table.public_rt.id
}