package br.hallel.relational.api.app.schedules;

import br.hallel.relational.api.app.email.dto.EmailParticipationDTO;
import br.hallel.relational.api.app.email.service.EmailEventParticipationService;
import br.hallel.relational.api.app.event.model.*;
import br.hallel.relational.api.app.event.model.enum_type.EventStatus;
import br.hallel.relational.api.app.event.model.enum_type.StatusPaymentEventParticipation;
import br.hallel.relational.api.app.event.model.enum_type.TransactionType;
import br.hallel.relational.api.app.event.repository.EventParticipationRepository;
import br.hallel.relational.api.app.event.repository.EventRepository;
import br.hallel.relational.api.app.event.repository.EventTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventParticipationSchedule {

    private static final double TAXA_MP_PIX_PERCENTUAL = 0.99;

    private final EventParticipationRepository eventParticipationRepository;
    private final EventRepository eventRepository;
    private final EmailEventParticipationService emailEventParticipationService;
    private final EventTransactionRepository eventTransactionRepository;

    @Scheduled(cron = "0 30 * * * *", zone = "America/Manaus")
    @Transactional
    public void updateEventStatus() {
        log.info("Iniciando verificação de status dos eventos...");

        LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Manaus"));

        List<Event> activeEvents = eventRepository.findByEventStatusNot(EventStatus.FINALIZADO);
        log.info("Encontrados {} eventos ativos para verificação.", activeEvents.size());

        List<Event> eventsToUpdate = new ArrayList<>();
        List<EventParticipation> participationsToUpdate = new ArrayList<>();

        try {
            for (Event event : activeEvents) {
                LocalDateTime startTime = event.getStartTime();
                LocalDateTime endTime = event.getEndTime();

                if (startTime == null || endTime == null) {
                    log.warn("Evento '{}' (id: {}) ignorado: start_time ou end_time é nulo.", event.getTitle(), event.getId());
                    continue;
                }

                LocalDateTime bufferedEndTime = endTime.plusHours(1);

                if (now.isAfter(bufferedEndTime)) {

                    event.setEventStatus(EventStatus.FINALIZADO);
                    eventsToUpdate.add(event);
                    log.info("Evento '{}' (id: {}) finalizado. Status alterado para FINALIZADO.", event.getTitle(),
                            event.getId());

                    List<EventParticipation> allParticipations = eventParticipationRepository.findAllByEvent_Id(
                            event.getId());
                    for (EventParticipation participation : allParticipations) {
                        if (participation.getStatusPaymentEventParticipation() != StatusPaymentEventParticipation.PAGO) {
                            participation.setStatusPaymentEventParticipation(StatusPaymentEventParticipation.NAO_PAGO);
                        }
                        participation.setHasParticipated(true);
                        participationsToUpdate.add(participation);
                    }

                } else if (now.isAfter(startTime) && event.getEventStatus() == EventStatus.AGENDADO) {

                    event.setEventStatus(EventStatus.OCORRENDO);
                    eventsToUpdate.add(event);
                    log.info("Evento '{}' (id: {}) iniciado. Status alterado para OCORRENDO.", event.getTitle(),
                            event.getId());
                }
            }

            if (!eventsToUpdate.isEmpty()) {
                eventRepository.saveAll(eventsToUpdate);
                log.info("{} eventos tiveram seus status atualizados.", eventsToUpdate.size());
            }
            if (!participationsToUpdate.isEmpty()) {
                eventParticipationRepository.saveAll(participationsToUpdate);
                log.info("{} participações foram atualizadas para refletir o fim dos eventos.",
                        participationsToUpdate.size());
            }

            log.info("Verificação de status de eventos finalizada.");
        } catch (Exception e) {
            log.error("Erro ao tentar atualizar eventos: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "0 0 * * * *", zone = "America/Manaus")
    @Transactional
    public void sendEmailRemindEventParticipation() {
        ZoneId zone = ZoneId.of("America/Manaus");
        LocalDateTime now = LocalDateTime.now(zone);

        // Início e fim do dia atual
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        Date startDate = Date.from(startOfDay.atZone(zone).toInstant());
        Date endDate = Date.from(endOfDay.atZone(zone).toInstant());

        // Busca todos os eventos que ocorrem HOJE
        List<Event> todayEvents = eventRepository.findByDateBetweenOrderByDateAsc(startDate, endDate);

        if (todayEvents.isEmpty()) {
            log.info("Nenhum evento encontrado para hoje ({})", now.toLocalDate());
            return;
        }

        for (Event event : todayEvents) {
            LocalDateTime eventTime = event.getDate().toInstant().atZone(zone).toLocalDateTime();

            Duration untilEvent = Duration.between(now, eventTime);

            // Caso 1: início do dia → mandar lembrete geral
            if (now.getHour() == 6) {
                sendReminderForEvent(event, true);
            }

            if (!untilEvent.isNegative() && untilEvent.toMinutes() <= 60 && untilEvent.toMinutes() >= 0) {
                sendReminderForEvent(event, false);
            }
        }
    }

    private void sendReminderForEvent(Event event, boolean isMorningReminder) {
        List<EventParticipation> participants = eventParticipationRepository.findAllByEvent_Id(event.getId());

        for (EventParticipation participant : participants) {

            boolean isPaid = participant.getStatusPaymentEventParticipation() == StatusPaymentEventParticipation.PAGO;

            EmailParticipationDTO emailDto = new EmailParticipationDTO(
                    participant.getEmail(),
                    participant.getName(),
                    event.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                    event.getTitle()
            );

            emailEventParticipationService.sendEventParticipationReminderEmail(
                    emailDto,
                    event.getId().toString(),
                    isMorningReminder,
                    isPaid
            );

            log.info("Lembrete de evento enviado para {} (status: {})",
                    participant.getEmail(), participant.getStatusPaymentEventParticipation());
        }
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void correctEventParticipationFees() {
        log.info("Iniciando rotina de correção de taxas (registro único - Valor Líquido) para EventParticipation.");


        List<EventParticipation> participationsToVerify =
                eventParticipationRepository.findAllByStatusPaymentEventParticipation(StatusPaymentEventParticipation.PAGO);

        for (EventParticipation participation : participationsToVerify) {

            if (participation.getMercadoPagoPaymentId() == null) {
                continue;
            }

            EventTransaction entradaTransaction = this.eventTransactionRepository.findByMercadoPagoPaymentId(
                    participation.getMercadoPagoPaymentId()
            ).orElse(null);

            if (entradaTransaction == null || entradaTransaction.getTransactionType() != TransactionType.ENTRADA) {
                log.warn("Participation {} PAGA sem EventTransaction de ENTRADA. Pulando.", participation.getId());
                continue;
            }

            Double valorBruto = participation.getAmountPaid();
            Double valorAtualEntrada = entradaTransaction.getValue();

            Double valorBrutoArredondado = round(valorBruto);

            if (round(valorAtualEntrada).equals(valorBrutoArredondado)) {

                log.info("Correção necessária para Participation ID: {}. Valor Bruto: {}",
                        participation.getId(), valorBruto);

                // Usando a taxa de 0.99%
                double fatorDesconto = 1 - (TAXA_MP_PIX_PERCENTUAL / 100);
                Double valorLiquido = round(valorBruto * fatorDesconto);

                entradaTransaction.setValue(valorLiquido);
                entradaTransaction.setDescription(entradaTransaction.getDescription() + " (Ajuste p/ Líquido)");

                eventTransactionRepository.save(entradaTransaction);

                log.info("ATUALIZADA ENTRADA {} com ID MP {}: Valor corrigido de {} para {}",
                        entradaTransaction.getId(),
                        participation.getMercadoPagoPaymentId(),
                        valorAtualEntrada,
                        valorLiquido);

            } else {
                log.info("Participation {} já corrigida ou valor incorreto {}. Pulando.",
                        participation.getEvent().getTitle(), participation.getAmountPaid());
            }
        }
        log.info("Rotina de correção (registro único) finalizada.");
    }

    private Double round(Double value) {
        if (value == null) return 0.0;
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

