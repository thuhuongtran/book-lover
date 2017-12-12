import com.vimensa.chat.service.FileProcess;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainTest {
    public static void main(String[] args) throws FileNotFoundException {
        // test get file stream from server
        /*
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        File file = new File(loader.getResource("upload/file/harrypotter.txt").getFile());
        */
        /*
        FileProcess fileProcess = new FileProcess();
        File file = fileProcess.getFile("hobbit.docx");
//        File file = new File(getDirectory()+"/upload/file/hobbit.docx");
        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        System.out.println("success");
*/
        /*
        String test_remove_space = "haha haha haha haha uhu jin haah edu ";
        test_remove_space=test_remove_space.replaceAll("\\s", "");
        System.out.println(test_remove_space);
        */
        // test processing roomLink
        String roomLink = "65_989_57885_1_455";
        String[] userIDLi = roomLink.split("_");
        for(int i=0;i<userIDLi.length;i++){
            System.out.println(userIDLi[i]);
        }

    }
    // get project direcotry
    private static String getDirectory(){
        Path currentRelativePath = Paths.get("");
        String s = currentRelativePath.toAbsolutePath().toString();
        return s;
    }
}
