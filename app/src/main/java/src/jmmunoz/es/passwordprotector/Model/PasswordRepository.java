package src.jmmunoz.es.passwordprotector.Model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mgj30 on 01/11/2017.
 */

public class PasswordRepository
{
    private List<Password> passwordList;
    private String repository_password;
    private String repository_user;

    public PasswordRepository(){

        this.repository_password="";
        this.passwordList=new ArrayList<Password>();
    }

    public List<Password> getPasswordList() {
        return passwordList;
    }

    public void addPasswordToRepository(String password_url,String password_name,String password_description,String password_user,String password_value)
    throws Exception{

        for(Password i:getPasswordList()){
            if(i.getPassword_name().equalsIgnoreCase(password_name)){
                throw new Exception("La contraseña ya existe");
            }
        }

        this.passwordList.add(new Password(
                password_url,
                password_name,
                password_description,
                password_user,
                password_value,
                Password.TYPE_ITEM));

    }
    public void setPasswordList(List<Password> passwordList) {
        this.passwordList = passwordList;
    }

    public String getRepositoryCode() {
        return repository_password;
    }

    public void setRepositoryCode(String repositoryCode) {
        this.repository_password = repositoryCode;
    }

    public int getLastId(){
        return getPasswordList().get(getPasswordList().size()-1).getPassword_id();
    }

    public int getNewId(){
        return getPasswordList().get(getPasswordList().size()-1).getPassword_id()+1;
    }

    public void updatePassword(Password p){
        List<Password> lista = getPasswordList();
        for(Password i:lista){
            if(i.getPassword_name().equalsIgnoreCase(p.getPassword_name())){
                lista.remove(i);
            }
        }
        lista.add(p);
        setPasswordList(lista);
    }

    public Password getPasswordById(int id){
        List<Password> lista = getPasswordList();
        Password item_response=null;
        for(Password i:lista){
            if(i.getPassword_id()==id){
                item_response=i;
            }
        }
        return item_response;
    }

    public Password getPasswordByName(String nombre){
        List<Password> lista = getPasswordList();
        Password item_response=null;
        for(Password i:lista){
            if(i.getPassword_name().equalsIgnoreCase(nombre)){
                item_response=i;
            }
        }
        return item_response;
    }

    public String getRepository_user() {
        return repository_user;
    }

    public void setRepository_user(String repository_user) {
        this.repository_user = repository_user;
    }

    public void deleteItem(int item){
        List<Password> lista = getPasswordList();
        for(Password i:lista){
            if(i.getPassword_id()==item){
                lista.remove(i);
                break;
            }
        }
        setPasswordList(lista);
    }

    public String toJson(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return jsonString;
    }


}
