package com.deb.notific.helper;

public class pnumber
{
    String phone;

    public pnumber(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        phone = phone.substring(3,13);
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
