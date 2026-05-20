output "cloud_run_url" {
  description = "URL do serviço Cloud Run (*.run.app)"
  value       = google_cloud_run_v2_service.hallel_api.uri
}

output "api_custom_domain" {
  description = "URL da API com domínio customizado via Cloudflare DNS"
  value       = "https://api.${var.domain_name}"
}

output "cloud_run_service_account" {
  description = "Service Account do Cloud Run"
  value       = google_service_account.hallel_api_sa.email
}
