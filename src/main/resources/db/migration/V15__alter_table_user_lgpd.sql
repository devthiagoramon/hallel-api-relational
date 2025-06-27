ALTER TABLE "user"
    ADD lgpd_consent boolean;

update "user"
set lgpd_consent = false;

ALTER TABLE "user"
    ADD lgpd_consent_date date;