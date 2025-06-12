package Components.Repository;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class Value {
    public String val;
    public LocalDateTime expiry;
    public LocalDateTime created;
    public boolean isDeletedInTransaction;

    public Value(String val, LocalDateTime created, LocalDateTime expiry) {
        this.val = val;
        this.expiry = expiry;
        this.created = created;
        this.isDeletedInTransaction = false;
    }
    public boolean isExpired() {
        return expiry != null && expiry.isBefore(LocalDateTime.now());
    }
}
