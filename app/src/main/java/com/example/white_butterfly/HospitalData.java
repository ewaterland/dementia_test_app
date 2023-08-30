package com.example.white_butterfly;

public class HospitalData {
    private String hospital_name;
    private String hospital_sub;
    private String hospital_adr;

    public HospitalData(String hospital_name, String hospital_sub, String hospital_adr){
        this.hospital_name = hospital_name;
        this.hospital_sub = hospital_sub;
        this.hospital_adr = hospital_adr;
    }

    public String getHospital_name()
    {
        return this.hospital_name;
    }

    public String getHospital_sub()
    {
        return this.hospital_sub;
    }

    public String getHospital_adr()
    {
        return this.hospital_adr;
    }
}