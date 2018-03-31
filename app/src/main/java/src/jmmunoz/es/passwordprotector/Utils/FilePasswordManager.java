package src.jmmunoz.es.passwordprotector.Utils;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by mgj30 on 01/11/2017.
 */

public class FilePasswordManager {
    Context context;
    String appRoute;

    public FilePasswordManager(Context context){
        this.context=context;
    }

    public boolean existFile(String fileName)
    {
        File root = Environment.getExternalStorageDirectory();
        root.exists();
       return new File(root, fileName).exists();
    }

    public String getPath(String file){
        File root = Environment.getExternalStorageDirectory();
        File f = new File(root, file);
        return f.getPath();
    }

    public String getFileContent (String fileName)
    {
        try {
            File root = Environment.getExternalStorageDirectory();
            FileInputStream fis = new FileInputStream(new File(root, fileName) );
            //FileInputStream fis = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }


            return sb.toString();
        } catch (FileNotFoundException e) {
            return "Archivo no encontrado";
        } catch (UnsupportedEncodingException e) {
            return "Codificacion no soportada";
        } catch (IOException e) {
            return "Error al leer el archivo";
        }catch (Exception e){
            return "Error al desencriptar el archivo";
        }
    }

    public void setFileContent(String fileName,String fileCotent)
    {
        FileOutputStream outputStream;
        try {
            File root = Environment.getExternalStorageDirectory();
            File f = new File(root, fileName);
            //f.delete();
            outputStream = new FileOutputStream(f);
            //outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(fileCotent.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
