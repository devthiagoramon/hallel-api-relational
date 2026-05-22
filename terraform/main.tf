locals {
  db_url = "jdbc:postgresql://db.xdkftealcamguolhezss.supabase.co:5432/postgres?sslmode=require"
}

# ─────────────────────────────────────────────────────────────────────────────
# APIs GCP necessárias
# ─────────────────────────────────────────────────────────────────────────────
resource "google_project_service" "run" {
  service            = "run.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "secretmanager" {
  service            = "secretmanager.googleapis.com"
  disable_on_destroy = false
}

resource "google_project_service" "iam" {
  service            = "iam.googleapis.com"
  disable_on_destroy = false
}

# ─────────────────────────────────────────────────────────────────────────────
# Service Account dedicado para o Cloud Run
# ─────────────────────────────────────────────────────────────────────────────
resource "google_service_account" "hallel_api_sa" {
  account_id   = "hallel-api-sa"
  display_name = "Hallel API — Cloud Run Service Account"
  depends_on   = [google_project_service.iam]
}

resource "google_project_iam_member" "hallel_api_sa_secretmanager" {
  project = var.gcp_project_id
  role    = "roles/secretmanager.secretAccessor"
  member  = "serviceAccount:${google_service_account.hallel_api_sa.email}"
}

# ─────────────────────────────────────────────────────────────────────────────
# Secret Manager — arquivos de credencial JSON montados no container
# ─────────────────────────────────────────────────────────────────────────────
resource "google_secret_manager_secret" "crypto_avatar" {
  secret_id  = "crypto-avatar-json"
  depends_on = [google_project_service.secretmanager]
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_version" "crypto_avatar" {
  secret      = google_secret_manager_secret.crypto_avatar.id
  secret_data = var.crypto_avatar_json
}

resource "google_secret_manager_secret" "google_services" {
  secret_id  = "google-services-json"
  depends_on = [google_project_service.secretmanager]
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_version" "google_services" {
  secret      = google_secret_manager_secret.google_services.id
  secret_data = var.google_services_json
}

resource "google_secret_manager_secret" "hallel_messaging_firebase" {
  secret_id  = "hallel-messaging-firebase-json"
  depends_on = [google_project_service.secretmanager]
  replication {
    auto {}
  }
}

resource "google_secret_manager_secret_version" "hallel_messaging_firebase" {
  secret      = google_secret_manager_secret.hallel_messaging_firebase.id
  secret_data = var.hallel_messaging_firebase_json
}

# ─────────────────────────────────────────────────────────────────────────────
# Cloud Run v2 — serviço principal da API
# ─────────────────────────────────────────────────────────────────────────────
resource "google_cloud_run_v2_service" "hallel_api" {
  name                = "hallel-api"
  location            = var.gcp_region
  deletion_protection = false

  template {
    service_account  = google_service_account.hallel_api_sa.email
    timeout          = "3600s"
    session_affinity = true

    scaling {
      min_instance_count = 0
      max_instance_count = 3
    }

    containers {
      image = "docker.io/thiagoramon/hallel-api:latest"

      ports {
        container_port = 8080
      }

      resources {
        limits = {
          cpu    = "1"
          memory = "1Gi"
        }
        cpu_idle          = true
        startup_cpu_boost = true
      }

      startup_probe {
        tcp_socket {
          port = 8080
        }
        initial_delay_seconds = 10
        period_seconds        = 10
        failure_threshold     = 30
        timeout_seconds       = 5
      }

      # ── Banco de dados (Supabase) ──
      env {
        name  = "SPRING_PROFILES_ACTIVE"
        value = "prod"
      }
      env {
        name  = "SPRING_DATASOURCE_URL"
        value = local.db_url
      }
      env {
        name  = "SPRING_DATASOURCE_USERNAME"
        value = "postgres"
      }
      env {
        name  = "SPRING_DATASOURCE_PASSWORD"
        value = var.db_password
      }

      # ── Segredos da aplicação ──
      env {
        name  = "JWT_SECRET"
        value = var.jwt_secret
      }
      env {
        name  = "PEPPER_SECRET"
        value = var.pepper_secret
      }
      env {
        name  = "SPRING_MAIL_USERNAME"
        value = var.mail_username
      }
      env {
        name  = "SPRING_MAIL_PASSWORD"
        value = var.mail_password
      }
      env {
        name  = "MERCADOPAGO_ACCESS_TOKEN"
        value = var.mercadopago_access_token
      }
      env {
        name  = "MERCADOPAGO_NOTIFICATION_URL"
        value = var.mercadopago_notification_url
      }
      env {
        name  = "FIREBASE_FCM_TOKEN"
        value = var.firebase_fcm_token
      }

      # ── Paths dos arquivos de credencial montados via Secret Manager ──
      env {
        name  = "GCP_CREDENTIALS_PATH"
        value = "file:/secrets/crypto-avatar/crypto-avatar.json"
      }
      env {
        name  = "GOOGLE_SERVICES_PATH"
        value = "file:/secrets/google-services/google-services.json"
      }
      env {
        name  = "HALLEL_MESSAGING_FIREBASE_PATH"
        value = "file:/secrets/firebase/hallel-messaging-firebase.json"
      }

      # ── Montagem dos volumes ──
      volume_mounts {
        name       = "crypto-avatar"
        mount_path = "/secrets/crypto-avatar"
      }
      volume_mounts {
        name       = "google-services"
        mount_path = "/secrets/google-services"
      }
      volume_mounts {
        name       = "firebase"
        mount_path = "/secrets/firebase"
      }
    }

    volumes {
      name = "crypto-avatar"
      secret {
        secret       = google_secret_manager_secret.crypto_avatar.secret_id
        default_mode = 0444
        items {
          version = "latest"
          path    = "crypto-avatar.json"
        }
      }
    }
    volumes {
      name = "google-services"
      secret {
        secret       = google_secret_manager_secret.google_services.secret_id
        default_mode = 0444
        items {
          version = "latest"
          path    = "google-services.json"
        }
      }
    }
    volumes {
      name = "firebase"
      secret {
        secret       = google_secret_manager_secret.hallel_messaging_firebase.secret_id
        default_mode = 0444
        items {
          version = "latest"
          path    = "hallel-messaging-firebase.json"
        }
      }
    }
  }

  traffic {
    type    = "TRAFFIC_TARGET_ALLOCATION_TYPE_LATEST"
    percent = 100
  }

  depends_on = [
    google_project_service.run,
    google_project_iam_member.hallel_api_sa_secretmanager,
    google_secret_manager_secret_version.crypto_avatar,
    google_secret_manager_secret_version.google_services,
    google_secret_manager_secret_version.hallel_messaging_firebase,
  ]
}

# Permite invocação pública (API sem autenticação GCP)
resource "google_cloud_run_v2_service_iam_member" "public_access" {
  name     = google_cloud_run_v2_service.hallel_api.name
  location = google_cloud_run_v2_service.hallel_api.location
  role     = "roles/run.invoker"
  member   = "allUsers"
}
