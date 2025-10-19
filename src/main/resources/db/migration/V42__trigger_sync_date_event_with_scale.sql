create or replace function fn_sycn_date_event_scale()
    returns trigger as
$$
begin
    IF NEW.date is DISTINCT FROM OLD.date THEN
        UPDATE event_scale
        SET date = NEW.date::DATE
        WHERE event_id = NEW.id;
    end if;
    return NEW;
end;
$$ language plpgsql;

create or replace trigger sync_date_event_with_scale
    BEFORE INSERT OR UPDATE
    ON events
    FOR EACH ROW
EXECUTE FUNCTION fn_sycn_date_event_scale();