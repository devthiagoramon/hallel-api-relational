#!/bin/bash
# Script para ser executado no primeiro boot da instância EC2

# Espera a instância estar pronta
sleep 20

# Atualiza os pacotes
sudo yum update -y

# Instala o Docker e o Docker Compose
sudo amazon-linux-extras install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -a -G docker ec2-user
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# Instala o Nginx
sudo amazon-linux-extras install nginx1 -y
sudo systemctl start nginx
sudo systemctl enable nginx

# Instala o Certbot via Snap (método mais confiável)
sudo yum install -y snapd
sudo systemctl enable --now snapd.socket
sudo ln -s /var/lib/snapd/snap /snap
sleep 10 # Dá um tempo para o snapd inicializar
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot