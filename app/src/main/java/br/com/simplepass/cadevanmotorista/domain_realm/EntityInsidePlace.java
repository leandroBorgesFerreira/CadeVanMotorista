package br.com.simplepass.cadevanmotorista.domain_realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by leandro on 3/29/16.
 * Ainda não consegui encontrar uma boa arquitetura que substitua essa classe.
 * Aqui temos um problema: School e Student não tëm muitas propriedades em comum, porém elas
 * tem a propriedade de serem pontos de parada na rota, o que faz com que eu os trate como iguais.
 * Mas isso gera problemas como: A escola tem a propriedade school, o que não faz nenhum sentido...
 */
public class EntityInsidePlace extends RealmObject {
    @PrimaryKey
    private Long id;

    private String name;
    private String phone;
    private String countryCode;
    private String type;
    private String address;
    private String school;

    public static final String TYPE_SCHOOL = "school";
    public static final String TYPE_STUDENT = "student";
    public static final String TYPE_PARENT = "parent";

    public EntityInsidePlace() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }
}
