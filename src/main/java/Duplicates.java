import com.twmacinta.util.MD5;

import javax.swing.*;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Duplicates
{

    public void duplicatesToTxt(String path,String saveDuplicateFilePath) throws IOException
    {
        File file = new File(saveDuplicateFilePath+"/duplicates.txt");
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        MultMap m = null;
        try
        {
            m = checkFolderForDuplicateFiles(path);
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        String filepaths = m.toString();
        fw.write(filepaths);
        fw.close();
    }

    public MultMap checkFolderForDuplicateFiles(String path) throws IOException, NoSuchAlgorithmException
    {
        MultMap duplicates = new MultMap();
        List<String> filepaths = getFilepaths(path);
        int max=filepaths.size();

        HashMap<String, String> hashmap = new HashMap<String, String>();
        for (String filepath : filepaths)
        {
            String md5 = generateChecksum(filepath); // see linked answer
            if (hashmap.containsKey(md5))
            {
                String original = hashmap.get(md5);
                String duplicate = filepath;

                if (!duplicates.contains(original))
                {
                    duplicates.put(md5, original);
                }
                duplicates.put(md5, duplicate);

                // found a match between original and duplicate
            } else
            {
                hashmap.put(md5, filepath);
            }
        }
        return duplicates;
    }

    public List<String> getFilepaths(String path)
    {
        List<String> result = new ArrayList<String>();
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles)
        {
            if (file.isFile())
            {
                result.add(file.getAbsolutePath());
            } else if (file.isDirectory())
            {
                result.addAll(getFilepaths(file.getAbsolutePath()));
            }
        }
        return result;
    }

    public String generateChecksum(String filename) throws NoSuchAlgorithmException, IOException
    {
        String myChecksum;
        File file = new File(filename);

            myChecksum=MD5.asHex(MD5.getHash(file));



        return myChecksum;
    }
}
