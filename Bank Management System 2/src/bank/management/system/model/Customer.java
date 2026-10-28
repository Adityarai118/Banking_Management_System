package bank.management.system.model;

/**
 * Model representing a bank customer.
 */
public class Customer {

    private int customerId;
    private String formNo;
    private String name;
    private String fatherName;
    private String dob;
    private String gender;
    private String email;
    private String maritalStatus;
    private String address;
    private String city;
    private String pincode;
    private String state;
    private String religion;
    private String category;
    private String income;
    private String education;
    private String occupation;
    private String pan;
    private String aadhar;
    private String seniorCitizen;
    private String existingAccount;
    private String createdAt;

    public Customer() {}

    public Customer(String formNo, String name, String fatherName, String dob, String gender,
                    String email, String maritalStatus, String address, String city,
                    String pincode, String state) {
        this.formNo = formNo;
        this.name = name;
        this.fatherName = fatherName;
        this.dob = dob;
        this.gender = gender;
        this.email = email;
        this.maritalStatus = maritalStatus;
        this.address = address;
        this.city = city;
        this.pincode = pincode;
        this.state = state;
    }

    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }

    public String getFormNo() { return formNo; }
    public void setFormNo(String formNo) { this.formNo = formNo; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFatherName() { return fatherName; }
    public void setFatherName(String fatherName) { this.fatherName = fatherName; }

    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPincode() { return pincode; }
    public void setPincode(String pincode) { this.pincode = pincode; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getIncome() { return income; }
    public void setIncome(String income) { this.income = income; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }

    public String getAadhar() { return aadhar; }
    public void setAadhar(String aadhar) { this.aadhar = aadhar; }

    public String getSeniorCitizen() { return seniorCitizen; }
    public void setSeniorCitizen(String seniorCitizen) { this.seniorCitizen = seniorCitizen; }

    public String getExistingAccount() { return existingAccount; }
    public void setExistingAccount(String existingAccount) { this.existingAccount = existingAccount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
