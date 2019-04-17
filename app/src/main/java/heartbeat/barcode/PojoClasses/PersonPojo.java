package heartbeat.barcode.PojoClasses;

/**
 * Created by Heartbeat on 11-06-2017.
 */

public class PersonPojo {
    private String uid;
    private String name;
    private String house;
    private String street;
    private String lm;
    private String loc;
    private String vtc;
    private String po;
    private String dist;
    private String subdist;
    private String statename;
    private String pincode;
    private String yob;
    private String dob;
    private String fatherName;
    private String gender;


    public PersonPojo(String uid, String name, String house, String street, String lm, String loc, String vtc, String po, String dist, String subdist, String statename, String pincode, String yob, String dob, String fatherName, String gender) {
        this.uid = uid;
        this.name = name;
        this.house = house;
        this.street = street;
        this.lm = lm;
        this.loc = loc;
        this.vtc = vtc;
        this.po = po;
        this.dist = dist;
        this.subdist = subdist;
        this.statename = statename;
        this.pincode = pincode;
        this.yob = yob;
        this.dob = dob;
        this.fatherName = fatherName;
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public String getName() {
        return name;
    }

    public String getHouse() {
        return house;
    }

    public String getStreet() {
        return street;
    }

    public String getLm() {
        return lm;
    }

    public String getLoc() {
        return loc;
    }

    public String getVtc() {
        return vtc;
    }

    public String getPo() {
        return po;
    }

    public String getDist() {
        return dist;
    }

    public String getSubdist() {
        return subdist;
    }

    public String getStatename() {
        return statename;
    }

    public String getPincode() {
        return pincode;
    }

    public String getYob() {
        return yob;
    }

    public String getDob() {
        return dob;
    }

    public String getFatherName() {
        return fatherName;
    }

    public String getGender() {
        return gender;
    }
}
