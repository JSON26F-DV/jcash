package app.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
public class UserDTO {
    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    private long id;

    @NotBlank(message = "Mobile number is required.")
    @Pattern(regexp = "\\d{11}", message = "Mobile number must contain exactly 11 digits.")
    private String mobileNumber;

    @NotBlank(message = "PIN is required.")
    @Pattern(regexp = "\\d{6}", message = "PIN must be exactly 6 digits.")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private String pin;

    @NotBlank(message = "First name is required.")
    @Size(max = 100, message = "First name cannot exceed 100 characters.")
    private String firstName;

    @Size(max = 200, message = "Last name cannot exceed 200 characters.")
    private String lastName = "";

    @NotBlank(message = "Email is required.")
    @Email(message = "Email address is invalid.")
    @Size(max = 200, message = "Email cannot exceed 200 characters.")
    private String email = "";

    @DecimalMin(value = "0.00", message = "Balance cannot be negative.")
    @Digits(integer = 13, fraction = 2, message = "Balance must fit DECIMAL(15,2).")
    private BigDecimal balance = BigDecimal.ZERO;

    private LocalDateTime createdAt;
    @ToString.Exclude
    private String[] errors = new String[0];

    public void validate() {
        errors = VALIDATOR.validate(this).stream()
                .map(violation -> violation.getMessage())
                .toArray(String[]::new);
    }
}