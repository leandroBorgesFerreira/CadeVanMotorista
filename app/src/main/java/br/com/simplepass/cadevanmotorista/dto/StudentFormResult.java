package br.com.simplepass.cadevanmotorista.dto;

import br.com.simplepass.cadevanmotorista.domain_realm.EntityInsidePlace;

/**
 * PlaceFormResult of the type student
 */
public class StudentFormResult extends PlaceFormResult {
    protected String countryCode;
    protected String phone;
    protected String schoolName;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Override
    public String getType() {
        return EntityInsidePlace.TYPE_STUDENT;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }
}
