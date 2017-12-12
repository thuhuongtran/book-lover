package com.vimensa.chat.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileProcess {
    //Save the uploaded file to this folder
    public static String UPLOADED_FOLDER = getDirectory()+"/upload/file/";
    public static final String DOWNLOAD_SOURCE = getDirectory()+"/upload/file/";

    // return file name
    public String processFile(MultipartFile file){
        try {

            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = (Path) Paths.get(UPLOADED_FOLDER + removeSpaces(file.getOriginalFilename()));
            Files.write(path, bytes);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return removeSpaces(file.getOriginalFilename());

    }

    // get project direcotry
    private static String getDirectory(){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        return s;
    }

    // get file from a folder link
    public File getFile(String fileName){
       // ClassLoader loader = Thread.currentThread().getContextClassLoader();
//        File file = new File(loader.getResource(DOWNLOAD_SOURCE+fileName).getFile());
        File file = new File(DOWNLOAD_SOURCE + fileName);
        return file;
    }
    // replace all spaces from a string to _
    private String removeSpaces(String str){
        return str.replaceAll("\\s", "_");
    }

}
