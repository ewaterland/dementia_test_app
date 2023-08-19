package com.example.white_butterfly;

import androidx.lifecycle.ViewModel;

public class InfoModel extends ViewModel {

    // register 페이지에서 입력된 정보
    private String inputEmail;
    private String inputPW;
    private String inputPW2;

    // register2 페이지에서 입력된 정보
    private String inputName;
    private Number inputGender;
    private String inputYear;
    private String inputMonth;
    private String inputDay;


    public String getInputEmail() {
        return inputEmail;
    }
    public void setInputEmail(String inputEmail) {
        this.inputEmail = inputEmail;
    }

    public String getInputPW() {
        return inputPW;
    }
    public void setInputPW(String inputPW) {
        this.inputPW = inputPW;
    }

    public String getInputPW2() {
        return inputPW2;
    }
    public void setInputPW2(String inputPW2) {
        this.inputPW2 = inputPW2;
    }

    public String getInputName() {
        return inputName;
    }
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }

    public Number getInputGender() {
        return inputGender;
    }
    public void setInputGender(Number inputGender) {
        this.inputGender = inputGender;
    }

    public String getInputYear() { return inputYear; }
    public void setInputYear(String inputYear) { this.inputYear = inputYear; }

    public String getInputMonth() { return inputMonth; }
    public void setInputMonth(String inputMonth) { this.inputMonth = inputMonth; }

    public String getInputDay() { return inputDay; }
    public void setInputDay(String inputDay) { this.inputDay = inputDay; }


}
