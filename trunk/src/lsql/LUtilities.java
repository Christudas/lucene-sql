/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package lsql;

import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;

/**
 *
 * Random useful functions that are not part of the actual queries
 */
public class LUtilities
{

    //this function takes all the $variables and replaces them with
    //an environment variable
    public static String insertEnvVariables(String command)
    {
        String[] array = command.trim().split(" ");
        for (int i=0; i<array.length; i++)
        {
            if (array[i].length() > 0)
            if (array[i].charAt(0) == '$')
            {
                String env = System.getenv(array[i].substring(1, array[i].length()).trim());
                if (env != null)
                array[i] = env;
            }
        }

        //put the array back together
        String newCommand = "";
        for (int i=0; i<array.length; i++)
        {
            newCommand = newCommand + array[i] + " ";
        }

        return newCommand;
    }


    //Checks for a lock on the Lucene Index
    public static void checkLuceneLock(String index) throws IOException
    {
        File f = new File(index + "\\write.lock");
        if (f.exists())
        {
            System.out.println();
            System.out.println("A write lock exists on the Lucene index.  Would you like to remove the lock?");
            System.out.print("Type yes to delete write.lock: ");
            BufferedReader br =  new BufferedReader(new InputStreamReader(System.in));
            String input = br.readLine();
            if (input.equalsIgnoreCase("yes"))
            {
                f.delete();
                System.out.println("Lock deleted.");
            }
             br.close();
        }

    }

    //adds a specified string until it reaches a certain length
    //doesn't java have a function that alrady does this?
    //TODO: handle exceptions out of bounds
	public static String strPad(String string, int length, String filler) {
		//not efficient, but works for now
		StringBuffer sb = new StringBuffer(string);
		for (int i = sb.length(); i < (length - 1); i++) {
			sb.append(filler);
		}

		return sb.toString();
	}

    //takes an array and truncates it to a smaller size
    //used for the display in horizontal mode
    public static String[] truncateArray(String[] array, int length)
    {
        String[] newArray = new String[length];
        for (int i=0; i<length; i++)
        {
            newArray[i] = array[i];
        }

        return newArray;
    }

    //find the longest string in the array
	public static int findLongestStringLength(String[] fields) throws IOException {

        //the longest string in the array
		int longest = 0;

        for (int i = 0; i < fields.length; i++) {
            String f = fields[i];
            if (f.length() > longest) {
                longest = f.length();
            }
        }

		return longest;
	}

    //helps us to get the file size
    private static final long  MEGABYTE = 1024L * 1024L;

    //does not recurse
    //TODO: Why is this not working?!?!
    public static long getFolderSize(File folder)
    {
        File[] files = folder.listFiles();

        long total = 0;

        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName() + "   " + files[i].length());
            total = total + files[i].length();
        }

        return total;
    }

    //convert bytes to megs
    public static long bytesToMeg(long bytes) {
        return bytes / MEGABYTE ;
    }

    //converts a collection to array
    public static String[] collectionToStringArray(Collection collection)
    {

        Object[] fields_object = collection.toArray();
        String[] fields_string = new String[fields_object.length];

        //convert an object[] to a string[]
        for (int i=0;i<fields_object.length; i++)
        {
            fields_string[i] = fields_object[i].toString();
        }

        return fields_string;
    }


    //Time Functions
    private static final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private static String getTimeStamp() {
		return sdf.format(new Date());
	}

    //logging functions
    public static void log() {
		System.out.println();
	}

    public static void log(String msg) {
		log(msg, true);
	}

    public static void log(String msg, boolean time) {
        if (time)
		System.out.println(getTimeStamp() + " " + msg);
        else
        System.out.println(msg);
	}
}
