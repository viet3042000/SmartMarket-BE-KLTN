package com.smartmarket.code.request.entityBIC;

public class trvDetailBIC {

    private Long ID;
    private Long TRVID;
    private String FullName;
    private Long Gender;
    private String DateofBirth;
    private String PassportCard;

    public Long getID() {
        return ID;
    }

    public void setID(Long ID) {
        this.ID = ID;
    }

    public Long getTRVID() {
        return TRVID;
    }

    public void setTRVID(Long TRVID) {
        this.TRVID = TRVID;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public Long getGender() {
        return Gender;
    }

    public void setGender(Long gender) {
        Gender = gender;
    }

    public String getDateofBirth() {
        return DateofBirth;
    }

    public void setDateofBirth(String dateofBirth) {
        DateofBirth = dateofBirth;
    }

    public String getPassportCard() {
        return PassportCard;
    }

    public void setPassportCard(String passportCard) {
        PassportCard = passportCard;
    }
}
