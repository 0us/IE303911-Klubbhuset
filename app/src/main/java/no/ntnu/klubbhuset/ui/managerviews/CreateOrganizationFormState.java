package no.ntnu.klubbhuset.ui.managerviews;

import androidx.annotation.Nullable;

public class CreateOrganizationFormState {
    @Nullable
    private Integer emailError;

    private boolean isDataValid;

    public CreateOrganizationFormState() {
        this.emailError = null;
        this.isDataValid = false;
    }

    public CreateOrganizationFormState(@Nullable Integer emailError) {
        this.emailError = emailError;
        this.isDataValid = false;
    }

    public CreateOrganizationFormState(boolean isDataValid) {
        this.emailError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    public boolean isDataValid() {
        return isDataValid;
    }
}
