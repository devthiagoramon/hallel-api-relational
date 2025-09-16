output "db_endpoint" {
  description = "A URL de conexão do RDS."
  value       = aws_db_instance.hallel_db_prod.endpoint
}

output "db_username" {
  description = "O nome de usuário do banco de dados."
  value       = aws_db_instance.hallel_db_prod.username
}

output "db_password_secret_arn" {
  description = "O ARN do segredo da senha do banco de dados no AWS Secrets Manager."
  value       = aws_ssm_parameter.db_password_parameter.value
  sensitive = true
}

output "app_security_group_id" {
  description = "O ID do Security Group da aplicação, para ser usado no deploy do EC2/Beanstalk."
  value = aws_security_group.app_sg.id
}