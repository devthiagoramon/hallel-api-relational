package br.hallel.relational.api.app.payment.dto;

public class PixListRequestDTO {

        private String txid;
        private String status;
        private String inicio;
        private String fim;

        public String getTxid() { return txid; }
        public void setTxid(String txid) { this.txid = txid; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getInicio() { return inicio; }
        public void setInicio(String inicio) { this.inicio = inicio; }
        public String getFim() { return fim; }
        public void setFim(String fim) { this.fim = fim; }

}
