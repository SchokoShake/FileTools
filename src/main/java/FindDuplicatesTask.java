import com.twmacinta.util.MD5;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

public class FindDuplicatesTask extends SwingWorker
{
    String path;
    String resultPath;

    FindDuplicatesTask(String path, String resultPath)
    {
        this.path = path;
        this.resultPath = resultPath;
    }

    @Override
    protected Object doInBackground() throws Exception
    {
        duplicatesToTxt(path, resultPath);
        return null;
    }

    public void duplicatesToTxt(String path, String saveDuplicateFilePath) throws IOException
    {
        System.out.println("on");
        File file = new File(saveDuplicateFilePath + "/duplicates.txt");
        file.createNewFile();
        FileWriter fw = new FileWriter(file);
        MultMap m = null;

        System.out.println("Start checking...");
        try
        {
            m = checkFolderForDuplicateFiles(path);
        } catch (ExecutionException e)
        {
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        String filepaths = m.toString();
        fw.write(filepaths);
        fw.close();
    }

    public MultMap checkFolderForDuplicateFiles(String path) throws ExecutionException, InterruptedException
    {
        MultMap duplicates = new MultMap();

        System.out.println("Loading Files...");

        List<String> filepaths = getFilepaths(path);

        System.out.println("Files loaded!");

        //used for Progress
        int max = filepaths.size();
        int current = 0;
        int progress = 100 * current / max;
        setProgress(progress);

        List<PathChecksumPair> pairList=parallelChecksumGeneration(filepaths,32);

        HashMap<String, String> hashmap = new HashMap<String, String>();
        for (PathChecksumPair pair : pairList)
        {
            System.out.println(pair);
            if (hashmap.containsKey(pair.checksum))
            {
                String original = hashmap.get(pair.checksum);
                String duplicate = pair.path;

                if (!duplicates.contains(original))
                {
                    duplicates.put(pair.checksum, original);
                }
                duplicates.put(pair.checksum, duplicate);

                // found a match between original and duplicate
            } else
            {
                hashmap.put(pair.checksum, pair.path);
            }
            current += 1;
            progress = 100 * current / max;
            setProgress(progress);
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
    public static <T> List<List<T>> split(List<T> list, int numberOfParts) {
        List<List<T>> numberOfPartss = new ArrayList<>(numberOfParts);
        int size = list.size();
        int sizePernumberOfParts = (int) Math.ceil(((double) size) / numberOfParts);
        int leftElements = size;
        int i = 0;
        while (i < size && numberOfParts != 0) {
            numberOfPartss.add(list.subList(i, i + sizePernumberOfParts));
            i = i + sizePernumberOfParts;
            leftElements = leftElements - sizePernumberOfParts;
            sizePernumberOfParts = (int) Math.ceil(((double) leftElements) / --numberOfParts);
        }
        return numberOfPartss;
    }
    public List<PathChecksumPair> parallelChecksumGeneration(List<String> filepaths,int threadCount) throws ExecutionException, InterruptedException
    {
        List<List<String>> splitFilepaths= split(filepaths,threadCount);


        ExecutorService service = Executors.newFixedThreadPool(threadCount);
        List<Future<List<PathChecksumPair>>> futureList=new ArrayList<>();
        System.out.println(splitFilepaths);
        System.out.println(splitFilepaths.size());
        for (List<String> chunk : splitFilepaths)
        {
            Future<List<PathChecksumPair>> temp=service.submit(new ChecksumGeneratorRunnable(chunk));
            futureList.add(temp);
        }

        List<PathChecksumPair> result=new ArrayList<>();

        for (Future<List<PathChecksumPair>> checksums:futureList)
        {
            List<PathChecksumPair> pathChecksumPairs=checksums.get();
            for (PathChecksumPair pathChecksumPair:pathChecksumPairs)
            {
                result.add(pathChecksumPair);
            }
        }
        return result;
    }
    public static String generateChecksum(String filename) throws IOException
    {
        String myChecksum;
        myChecksum = MD5.asHex(MD5.getHash(new File(filename)));
        return myChecksum;
    }
}
class ChecksumGeneratorRunnable implements Callable<List<PathChecksumPair>>
{
    List<String> filepaths;
    ChecksumGeneratorRunnable(List<String> filepaths){
        this.filepaths=filepaths;
    }

    @Override
    public List<PathChecksumPair> call()
    {
        String md5;
        List<PathChecksumPair> Checksums=new ArrayList<>();
        for (String filepath:filepaths)
        {
            PathChecksumPair temp=null;
            try
            {
                md5 = FindDuplicatesTask.generateChecksum(filepath);
                temp=new PathChecksumPair(filepath,md5);
            } catch (IOException e)
            {
                e.printStackTrace();
            }
            Checksums.add(temp);
        }
        return Checksums;
    }
}
class PathChecksumPair
{
    String path;
    String checksum;
    PathChecksumPair(String path, String checksum){
        this.path=path;
        this.checksum=checksum;
    }
}
