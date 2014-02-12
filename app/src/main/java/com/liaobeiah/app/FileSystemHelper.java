package com.liaobeiah.app;

import android.content.Context;

import java.io.File;
import java.util.UUID;

/**
 * Created by dokinkon on 2/11/14.
 */
public class FileSystemHelper {

    public static boolean deleteEvent(Context context, UUID uuid) {
        File file = context.getExternalFilesDir(uuid.toString());
        return file.delete();
    }

    public static boolean deleteAllEvents(Context context) {

        File appDir = context.getExternalFilesDir(null);
        File[] files = appDir.listFiles();
        for (File file : files) {
            file.delete();
        }
        return true;
    }

    public static File getEventFolder(Context context, UUID uuid) {
        File file = context.getExternalFilesDir(uuid.toString());
        file.mkdir();
        return file;
    }

    public static File getEventPicture(Context context, UUID uuid, int index) {

        String fileName = "PIC-" + index + ".jpg";
        return new File(getEventFolder(context, uuid), fileName);
    }

    public static File getEventThumbnail(Context context, UUID uuid, int index) {

        String fileName = "THUMBNAIL-" + index + ".jpg";
        return new File(getEventFolder(context, uuid), fileName);

    }

    public static File getEventForm(Context context, UUID uuid) {
        String fileName = "form.docx";
        return new File(getEventFolder(context, uuid), fileName);
    }

    /*
    // Checks if external storage is available for read and write
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    // Checks if external storage is available to at least read
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
    */
}
