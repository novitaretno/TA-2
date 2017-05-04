package com.example.novita.cobata2;

import java.io.File;

/**
 * Created by Administrator on 4/26/2017.
 */

public class FileHelper {
    private String name; //menampung nama folder di db hp


    public FileHelper(){}
    public FileHelper(String name){
        this.name=name;
    }

    public static boolean isFileAnImage(File file)
    {
        if(file.toString().endsWith(".jpg")||file.toString().endsWith(".gif")||file.toString().endsWith(".jpeg")||file.toString().endsWith(".png"))
            return true;
        else
            return false;
    }

    public File[] getListOfFiles(){
        File directory = new File(name);
        if(directory.exists()){
            return directory.listFiles();
        }
        else {
            return new File[]{};
        }
    }





}
