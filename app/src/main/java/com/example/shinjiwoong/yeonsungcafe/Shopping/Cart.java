package com.example.shinjiwoong.yeonsungcafe.Shopping;


public class Cart {

    private String id;
    private String name;
    private String image;
    private String price;
    private String temp;
    private String count;
    private String option;

    public Cart(String id, String name, String image, String price, String temp, String count, String option) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.price = price;
        this.temp = temp;
        this.count = count;
        this.option = option;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
