package cting.com.robin.support.common.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RawRes;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileHelper {
    private static final String TAG = "cting/util/file";

    public static final String DIR_ROBIN_TOOL = "/RobinTool/";
    public static final String EXTERNAL_DIR = Environment.getExternalStorageDirectory() + DIR_ROBIN_TOOL;


    private static final File makeDirIfNotExist(String fileName) {
        File file = new File(fileName);
        File dir = file.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "makeDirIfNotExist: "+e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
        return file;
    }


    public static boolean writeToExternal(@NonNull String fileName, @NonNull String content) {
        OutputStream out = null;
        try {
            File file = makeDirIfNotExist(fileName);
            out = new FileOutputStream(file);
            out.write(content.getBytes());
            return true;
        } catch (IOException e) {
            Log.w(TAG, "exportToFile,exception1: " + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                Log.w(TAG, "exportToFile,exception2: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public static final boolean writeToInternal(Context context, @NonNull String fileName, @NonNull String content) {
        OutputStream out = null;
        try {
            out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            out.write(content.getBytes());
            return true;
        } catch (IOException e) {
            Log.w(TAG, "exportToFile,exception1: " + e.getLocalizedMessage());
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (Exception e) {
                Log.w(TAG, "exportToFile,exception2: " + e.getLocalizedMessage());
                e.printStackTrace();
            }
        }
    }

    public static String readFromInputStream(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (IOException e) {
            Log.e(TAG, "readFromInputStream,exception1:" + e.getLocalizedMessage());
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "readFromInputStream,exception2:" + e.getLocalizedMessage());
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

    public static String readStream(InputStream stream){
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        BufferedOutputStream out = null;
        try {
            int length = 0;
            out = new BufferedOutputStream(byteArray);
            while ((length = stream.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            out.flush();
            return byteArray.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFromRaw(Context context, @RawRes int rawId) {
        InputStream inputStream = context.getResources().openRawResource(rawId);
        return readFromInputStream(inputStream);
    }

    public static String readFromAssets(Context context, @NonNull String rawFileName) {
        try {
            InputStream inputStream = context.getResources().getAssets().open(rawFileName);
            return readFromInputStream(inputStream);
        } catch (IOException e) {
            Log.e(TAG, "readFromRaw,exception:" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String readFromExternal(@NonNull String fileName) {
        try {
            InputStream inputStream = new FileInputStream(fileName);
            return readFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "readFromExternal,exception:" + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static String readFromInternal(Context context, @NonNull String fileName) {
        try {
            InputStream inputStream = context.openFileInput(fileName);
            return readFromInputStream(inputStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "readFromInternal,exception=" + e.getLocalizedMessage());
            e.printStackTrace();
            return null;
        }
    }
}
