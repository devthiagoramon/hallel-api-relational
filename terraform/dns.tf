resource "aws_route53_record" "api" {
  zone_id = data.aws_route53_zone.primary.zone_id
  name    = var.api_domain_name # "api.comunidadecatolicahallel.com.br"
  type    = "A"
  ttl     = 300
  records = [aws_eip.app_eip.public_ip]
}