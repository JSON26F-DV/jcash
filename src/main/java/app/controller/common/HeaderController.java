package app.controller.common;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import app.model.User;
import app.services.AuthService;
import javafx.scene.control.Label;

public abstract class HeaderController {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a");

    protected final User user = AuthService.currentUser();

    protected boolean setupHeader(Label welcomeLabel, Label dateTimeLabel) {
        dateTimeLabel.setText(LocalDateTime.now().format(DATE_FORMAT));
        if (user == null) {
            return false;
        }
        welcomeLabel.setText("Hello, " + user.getFirstName() + ". Welcome back.");
        return true;
    }
}
