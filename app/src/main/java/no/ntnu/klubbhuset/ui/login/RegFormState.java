package no.ntnu.klubbhuset.ui.login;

import androidx.annotation.Nullable;

public class RegFormState {

    @Nullable
    private Integer firstNameError;

    @Nullable
    private Integer lastNameError;

    @Nullable
    private Integer emailError;

    @Nullable
    private Integer passwordError;

    @Nullable
    private Integer phoneNumberError;



    private boolean isFirstNameValid;
    private boolean isLastNameValid;
    private boolean isEmailValid;
    private boolean isPasswordValid;
    private boolean isPhoneNumberValid;

    public RegFormState() {
        isFirstNameValid = true;
        isLastNameValid = true;
        isEmailValid = true;
        isPasswordValid = true;
        isPhoneNumberValid = true;
    }

    public boolean isFirstNameValid() {
        return isFirstNameValid;
    }

    public boolean isLastNameValid() {
        return isLastNameValid;
    }

    public boolean isEmailValid() {
        return isEmailValid;
    }

    public boolean isPasswordValid() {
        return isPasswordValid;
    }

    public boolean isPhoneNumberValid() {
        return isPhoneNumberValid;
    }

    public void setFirstNameError(@Nullable Integer firstNameError) {
        this.firstNameError = firstNameError;
        this.isFirstNameValid = false;
    }

    public void setLastNameError(@Nullable Integer lastNameError) {
        this.lastNameError = lastNameError;
        this.isLastNameValid = false;
    }

    public void setEmailError(@Nullable Integer emailError) {
        this.emailError = emailError;
        this.isEmailValid = false;
    }

    public void setPasswordError(@Nullable Integer passwordError) {
        this.passwordError = passwordError;
        this.isPasswordValid = false;
    }

    public void setPhoneNumberError(@Nullable Integer phoneNumberError) {
        this.phoneNumberError = phoneNumberError;
        this.isPhoneNumberValid = false;
    }

    @Nullable
    public Integer getFirstNameError() {
        return firstNameError;
    }

    @Nullable
    public Integer getLastNameError() {
        return lastNameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getPhoneNumberError() {
        return phoneNumberError;
    }

    public boolean isDataValid() {
        return isFirstNameValid && isLastNameValid && isEmailValid
                && isPasswordValid && isPhoneNumberValid;
    }
}
