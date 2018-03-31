package src.jmmunoz.es.passwordprotector.Model;

import com.google.gson.Gson;

/**
 * Created by mgj30 on 01/11/2017.
 */

public class Password {
    private int password_id;
    private String password_url;
    private String password_name;
    private String password_description;
    private String password_user;
    private String password_value;

    public Password(){

    }

    public Password(String password_url,String password_name,String password_description,String password_user,String password_value){
        this.password_url=password_url;
        this.password_name=password_name;
        this.password_description=password_description;
        this.password_user=password_user;
        this.password_value=password_value;

    }

    public int getPassword_id() {
        return password_id;
    }

    public void setPassword_id(int password_id) {
        this.password_id = password_id;
    }

    public String getPassword_name() {
        return password_name;
    }

    public void setPassword_name(String password_name) {
        this.password_name = password_name;
    }

    public String getPassword_description() {
        return password_description;
    }

    public void setPassword_description(String password_description) {
        this.password_description = password_description;
    }

    public String getPassword_url() {
        return password_url;
    }

    public void setPassword_url(String password_url) {
        this.password_url = password_url;
    }

    public String getPassword_user() {
        return password_user;
    }

    public void setPassword_user(String password_user) {
        this.password_user = password_user;
    }

    public String getPassword_value() {
        return password_value;
    }

    public void setPassword_value(String password_value) {
        this.password_value = password_value;
    }

    public String toJson(){
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
