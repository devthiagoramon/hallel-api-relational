package br.hallel.relational.api.app.payment.dto;

import java.util.List;

public class PixWebhookPayloadDTO {
    private List<PixWebHookPaymentDTO> pix;

    public List<PixWebHookPaymentDTO> getPix() {
        return pix;
    }

    public void setPix(List<PixWebHookPaymentDTO> pix) {
        this.pix = pix;
    }
}
