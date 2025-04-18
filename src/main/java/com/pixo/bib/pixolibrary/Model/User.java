package com.pixo.bib.pixolibrary.Model;

public class User {
    private int id;
    private String email;
    private String password;
    private String name;
    private String firstname;

    public User(int id, String email, String password, String name, String firstname) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.firstname = firstname;
    }

    public User(String email, String password, String name, String firstname) {
        this(-1, email, password, name, firstname);
    }

    //getters and setters
    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getFirstname() { return firstname; }
    public void setEmail(String email) {this.email = email;}
    public void setId(int id) {this.id = id;}
    public void setPassword(String password){this.password = password;}
    public void setName(String name) {this.name = name;}
    public void setFirstname(String firstname) {this.firstname = firstname;}
}
