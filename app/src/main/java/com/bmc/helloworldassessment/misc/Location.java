package com.bmc.helloworldassessment.misc;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Location implements Parcelable, Serializable{

    private String name;
    private String address;
    private String address2;
    private String city;
    private String state;
    private int zipCode;
    private String phone;
    private String fax;
    private double latitude;
    private double longitude;
    private String imageUrl;

    private Location() {
        // Use the builder
    }

    private Location(Parcel in) {
        readFromParcel(in);
    }

    /**
     * Get the office name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the first line of this office's address
     * @return address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Get the second line of this office's address
     * @return address2
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Get the city this office is located in
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Get the state this office is located in
     * @return state
     */
    public String getState() {
        return state;
    }

    /**
     * Get the postal zip code for this office
     * @return zipCode
     */
    public int getZipCode() {
        return zipCode;
    }

    /**
     * Get the phone number for this office
     * @return phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Get the fax number for this office
     * @return fax
     */
    public String getFax() {
        return fax;
    }

    /**
     * Get the latitude for this office's location
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Get the longitude for this office's location
     * @return longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Get the url where this office's image is located at
     * @return imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public String toString() {
        return "Location: " + name;
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeString(address);
        out.writeString(address2);
        out.writeString(city);
        out.writeString(state);
        out.writeInt(zipCode);
        out.writeString(phone);
        out.writeString(fax);
        out.writeDouble(latitude);
        out.writeDouble(longitude);
        out.writeString(imageUrl);
    }

    private void readFromParcel(Parcel in) {
        name = in.readString();
        address = in.readString();
        address2 = in.readString();
        city = in.readString();
        state = in.readString();
        zipCode = in.readInt();
        phone = in.readString();
        fax = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        imageUrl = in.readString();
    }

    public static class Builder {
        private String name;
        private String address;
        private String address2;
        private String city;
        private String state;
        private int zipCode;
        private String phone;
        private String fax;
        private double latitude;
        private double longitude;
        private String imageUrl;

        public Builder setName(String officeName) {
            name = officeName;
            return this;
        }

        public Builder setAddress(String officeAddress) {
            address = officeAddress;
            return this;
        }

        public Builder setAddress2(String officeAddress2) {
            address2 = officeAddress2;
            return this;
        }

        public Builder setCity(String officeCity) {
            city = officeCity;
            return this;
        }

        public Builder setState(String officeState) {
            state = officeState;
            return this;
        }

        public Builder setZipCode(int officeZip) {
            zipCode = officeZip;
            return this;
        }

        public Builder setPhoneNumber(String officePhone) {
            phone = officePhone;
            return this;
        }

        public Builder setFaxNumber(String officeFax) {
            fax = officeFax;
            return this;
        }

        public Builder setLatitude(double officeLatitude) {
            latitude = officeLatitude;
            return this;
        }

        public Builder setLongitude(double officeLongitude) {
            longitude = officeLongitude;
            return this;
        }

        public Builder setImage(String officeImage) {
            imageUrl = officeImage;
            return this;
        }

        public Location build() {
            Location location = new Location();
            location.name = name;
            location.address = address;
            location.address2 = address2;
            location.city = city;
            location.state = state;
            location.zipCode = zipCode;
            location.phone = phone;
            location.fax = fax;
            location.latitude = latitude;
            location.longitude = longitude;
            location.imageUrl = imageUrl;
            return location;
        }
    }
}
