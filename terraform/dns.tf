# CNAME proxied pelo Cloudflare — o Worker intercepta e reescreve o Host header
resource "cloudflare_record" "api" {
  zone_id = var.cloudflare_zone_id
  name    = "api"
  content = trimprefix(google_cloud_run_v2_service.hallel_api.uri, "https://")
  type    = "CNAME"
  proxied = true
  ttl     = 1

  depends_on = [google_cloud_run_v2_service.hallel_api]
}

# Worker que reescreve o Host header para o Cloud Run aceitar o domínio customizado
resource "cloudflare_workers_script" "api_proxy" {
  account_id = var.cloudflare_account_id
  name       = "hallel-api-proxy"

  content = <<-EOT
    addEventListener("fetch", function(event) {
      event.respondWith(handleRequest(event.request));
    });

    async function handleRequest(request) {
      var cloudRunHost = "${trimprefix(google_cloud_run_v2_service.hallel_api.uri, "https://")}";
      var url = new URL(request.url);
      url.hostname = cloudRunHost;

      var headers = new Headers(request.headers);
      headers.set("Host", cloudRunHost);

      return fetch(url.toString(), {
        method: request.method,
        headers: headers,
        body: ["GET", "HEAD"].includes(request.method) ? null : request.body,
        redirect: "follow",
      });
    }
  EOT

  depends_on = [google_cloud_run_v2_service.hallel_api]
}

# Rota: todo tráfego de api.comunidadecatolicahallel.com.br passa pelo Worker
resource "cloudflare_workers_route" "api_proxy" {
  zone_id     = var.cloudflare_zone_id
  pattern     = "api.${var.domain_name}/*"
  script_name = cloudflare_workers_script.api_proxy.name
}
