# CNAME api.comunidadecatolicahallel.com.br → Cloud Run URL
# proxied = false: Cloud Run gerencia TLS diretamente (evita conflito de certificados)
resource "cloudflare_record" "api" {
  zone_id = var.cloudflare_zone_id
  name    = "api"
  content = trimprefix(google_cloud_run_v2_service.hallel_api.uri, "https://")
  type    = "CNAME"
  proxied = false
  ttl     = 300

  depends_on = [google_cloud_run_v2_service.hallel_api]
}
