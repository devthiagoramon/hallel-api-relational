output "db_endpoint" {
  description = "A URL de conexão da instância RDS."
  value       = aws_db_instance.hallel_db_instance.endpoint
}

output "db_username" {
  description = "O nome de usuário do banco de dados."
  value       = aws_db_instance.hallel_db_instance.username
}
output "app_security_group_id" {
  description = "O ID do Security Group da aplicação, para ser usado no deploy do EC2/Beanstalk."
  value       = aws_security_group.app_sg.id
}

output "db_password" {
  description = "A senha gerada para o banco de dados RDS."
  value       = random_password.db_password.result
  sensitive   = true # Impede que a senha apareça nos logs do Terraform
}

output "app_sg_id" {
  description = "O ID do Security Group da aplicação."
  value       = aws_security_group.app_sg.id
}