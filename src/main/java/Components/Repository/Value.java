package Components.Repository;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;


public class Value {
    public String val;
    public LocalDateTime expiry;
    public LocalDateTime created;

    public Value(String val, LocalDateTime expiry, LocalDateTime created) {
        this.val = val;
        this.expiry = expiry;
        this.created = created;
    }
}
