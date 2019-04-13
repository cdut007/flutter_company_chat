package com.ultralinked.voip.api;

import java.io.File;

/**
 * Created by Administrator on 2015/11/20.
 */
public class FileNameUtils {

    public static final char EXTENSION_SEPARATOR = '.';

    public static final String EXTENSION_SEPARATOR_STR = (new Character(EXTENSION_SEPARATOR)).toString();


    private static final char UNIX_SEPARATOR = '/';


    private static final char WINDOWS_SEPARATOR = '\\';


    private static final char SYSTEM_SEPARATOR = File.separatorChar;

    /**
     * get file name from the absolute file path
     * @param filename
     * @return the file name
     */

    public static String getName(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfLastSeparator(filename);
        return filename.substring(index + 1);
    }


    protected static int indexOfExtension(String filename) {
        if (filename == null) {
            return -1;
        }
        int extensionPos = filename.lastIndexOf(EXTENSION_SEPARATOR);
        int lastSeparator = indexOfLastSeparator(filename);
        return (lastSeparator > extensionPos ? -1 : extensionPos);
    }

    /**
     * get the extension of the filename
     * @param filename
     * @return extension of the filename
     */
    public static String getExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int index = indexOfExtension(filename);
        if (index == -1) {
            return "";
        } else {
            return filename.substring(index + 1);
        }
    }

    protected static int indexOfLastSeparator(String filename) {
        if (filename == null) {
            return -1;
        }
        int lastUnixPos = filename.lastIndexOf(UNIX_SEPARATOR);
        int lastWindowsPos = filename.lastIndexOf(WINDOWS_SEPARATOR);
        return Math.max(lastUnixPos, lastWindowsPos);
    }

}
