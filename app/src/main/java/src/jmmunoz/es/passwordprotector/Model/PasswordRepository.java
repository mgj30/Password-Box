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

    public List<Password> getListOrGroup(int item_group){
        if(item_group==0)
            return passwordList;
        else
            return getPasswordById(item_group).getLista_password();
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
        if(getPasswordList().size()>0) {
            return getPasswordList().get(getPasswordList().size()-1).getPassword_id();
        }else{
            return 1;
        }
    }

    public int getNewId(){

        if(getPasswordList().size()>0) {
            return getPasswordList().get(getPasswordList().size() - 1).getPassword_id() + 1;
        }else{
            return 1;
        }
    }

    public int getNewIdFromGroup(int id_group){
        Password grupo = getPasswordById(id_group);
        if(grupo.getLista_password().size()>0) {
            return grupo.getLista_password().get(grupo.getLista_password().size() - 1).getPassword_id() + 1;
        }else{
            return 1;
        }
    }

    public void updatePassword(Password p){
        List<Password> lista = getPasswordList();
        for(Password i:lista){
            if(i.getPassword_id()==p.getPassword_id()){
                lista.remove(i);
                break;
            }
        }
        lista.add(p);
        setPasswordList(lista);
    }

    public void updatePasswordInGroup(int id_group, Password p){
        Password group = getPasswordById(id_group);
        List<Password> lista = group.getLista_password();
        for(Password i:group.getLista_password()){
            if(i.getPassword_id()==p.getPassword_id()){
                lista.remove(i);
                break;
            }
        }
        lista.add(p);
        group.setLista_password(lista);
        updatePassword(group);
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

    public Password getPasswordInGroupById(int id_group, int id_password){
        Password grupo= getPasswordById(id_group);
        List<Password> lista = grupo.getLista_password();
        Password item_response=null;
        for(Password i:lista){
            if(i.getPassword_id()==id_password){
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

    public void deleteItemInGroup(int id_group,int item){
        Password grupo= getPasswordById(id_group);
        List<Password> lista = grupo.getLista_password();
        for(Password i:lista){
            if(i.getPassword_id()==item){
                lista.remove(i);
                break;
            }
        }
        grupo.setLista_password(lista);
        updatePassword(grupo);
    }

    public String toJson(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(this);
        return jsonString;
    }


}
