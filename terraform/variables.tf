# ── GCP ──────────────────────────────────────────────────────────────────────
variable "gcp_project_id" {
  description = "ID do projeto GCP"
  type        = string
  default     = "hallel-prod"
}

variable "gcp_region" {
  description = "Região GCP para o Cloud Run"
  type        = string
  default     = "southamerica-east1"
}

variable "gcp_credentials_file" {
  description = "Caminho para o arquivo JSON de credenciais do Service Account GCP (terraform-sa)"
  type        = string
}

# ── Cloudflare ────────────────────────────────────────────────────────────────
variable "cloudflare_api_token" {
  description = "Token da API do Cloudflare (Zone:DNS:Edit)"
  type        = string
  sensitive   = true
}

variable "cloudflare_zone_id" {
  description = "Zone ID do domínio no Cloudflare"
  type        = string
  default     = "ec11479f117044648c9bd09863102e34"
}

variable "domain_name" {
  description = "Domínio principal"
  type        = string
  default     = "comunidadecatolicahallel.com.br"
}

# ── Segredos da aplicação ─────────────────────────────────────────────────────
variable "db_password" {
  description = "Senha do banco de dados Supabase (postgres user)"
  type        = string
  sensitive   = true
}

variable "jwt_secret" {
  description = "Segredo para assinatura JWT"
  type        = string
  sensitive   = true
}

variable "pepper_secret" {
  description = "Pepper para hash de senhas"
  type        = string
  sensitive   = true
}

variable "mail_username" {
  description = "Endereço de e-mail para envio (Gmail)"
  type        = string
}

variable "mail_password" {
  description = "App Password do Gmail para envio de e-mails"
  type        = string
  sensitive   = true
}

variable "mercadopago_access_token" {
  description = "Access Token do Mercado Pago"
  type        = string
  sensitive   = true
}

variable "mercadopago_notification_url" {
  description = "URL do webhook do Mercado Pago"
  type        = string
}

variable "firebase_fcm_token" {
  description = "Token da API do Firebase Cloud Messaging"
  type        = string
  sensitive   = true
}

# ── Arquivos de credencial JSON ───────────────────────────────────────────────
variable "crypto_avatar_json" {
  description = "Conteúdo do crypto-avatar.json (service account GCS)"
  type        = string
  sensitive   = true
}

variable "google_services_json" {
  description = "Conteúdo do google-services.json (Firebase config)"
  type        = string
  sensitive   = true
}

variable "hallel_messaging_firebase_json" {
  description = "Conteúdo do hallel-messaging-firebase.json (Firebase FCM)"
  type        = string
  sensitive   = true
}
