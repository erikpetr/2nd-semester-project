package model;


public class Address {

    private int id;
    private String number;
    private String supplement;
    private String street;
    private String city;
    private String zipcode;
    private String region;
    private String country;

    public Address (int id, String number, String supplement, String street, String city, String zipcode, String region, String country)
    {
        this.id = id;
        this.number = number;
        this.supplement = supplement;
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
        this.region = region;
        this.country = country;
    }

    public Address (String number, String supplement, String street, String city, String zipcode, String region, String country)
    {
        this.number = number;
        this.supplement = supplement;
        this.street = street;
        this.city = city;
        this.zipcode = zipcode;
        this.region = region;
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public String getNumber(){
        return number;
    }

    public String getSupplement(){
        return supplement;
    }

    public String getStreet(){
        return street;
    }

    public String getCity(){
        return city;
    }

    public String getZipcode(){
        return zipcode;
    }

    public String getRegion(){
        return region;
    }

    public String getCountry(){
        return country;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setSupplement(String supplement) {
        this.supplement = supplement;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        String value = "";
        if (country != null) {
            value += getCountry();
        }
        if (region != null) {
            value += " " + getRegion();
        }
        if (zipcode != null) {
            if (!value.equals("")) value += "\n";
            value += getZipcode();
        }
        if (city != null) {
            value += " " + getCity();
        }
        if (street != null) {
            value += "\n" + getStreet();
        }
        if (number != null) {
            value += " " + getNumber();
        }
        if (supplement != null) {
            value += " " + getSupplement();
        }

        return value;
    }

}

