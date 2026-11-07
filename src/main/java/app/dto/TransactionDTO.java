package app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Data;
import lombok.ToString;

@Data
public class TransactionDTO {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private long id;

    @NotBlank(message = "Transaction type is required.")
    @Pattern(regexp = "CASH_IN|TRANSFER", message = "Type must be CASH_IN or TRANSFER.")
    private String type;

    private Long senderId;
    private Long receiverId;

    @DecimalMin(value = "0.00", inclusive = false, message = "Amount must be greater than zero.")
    @Digits(integer = 13, fraction = 2, message = "Amount must fit DECIMAL(15,2).")
    private BigDecimal amount;

    @NotBlank(message = "Transaction details are required.")
    @Size(max = 150, message = "Transaction details cannot exceed 150 characters.")
    private String details;

    private String category;

    @Size(max = 100, message = "Message cannot exceed 100 characters.")
    private String message = "";

    private LocalDateTime dateTime;
    private LocalDateTime updatedAt;
    @ToString.Exclude
    private String[] errors = new String[0];

    public void validate() {
        errors = VALIDATOR.validate(this).stream()
                .map(violation -> violation.getMessage())
                .toArray(String[]::new);
    }
}