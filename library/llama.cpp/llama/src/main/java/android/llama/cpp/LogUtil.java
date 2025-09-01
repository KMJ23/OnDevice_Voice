package android.llama.cpp;


import android.os.Debug;
import android.util.Log;

public class LogUtil
{
    /*
     * ?
     */
    private static final String APP_NAME = "ondevice_rag";

    /*
     * 로그 표시 유무 true : 로그 전체 출력 안함 Level 무시 false : 다 보여줌
     */
//    private static boolean DEBUG_MODE = true;
    private static boolean DEBUG_MODE = true;


    public static int DEBUG_LEVEL_0 = 0;
    public static int DEBUG_LEVEL_1 = 1;
    public static int DEBUG_LEVEL_2 = 2;
    public static int DEBUG_LEVEL_3 = 3;
    /**
     * 0 : 으로 설정하면, 다 나옴
     * 1 : 로 설정하면 1로 설정한거 안나옴 23만 나옴
     * 2 : 로 설정하면 2로 설정한거 안나옴 3만 나옴
     * 3 : 으로 설정하면 아무것도 안나옴  구지 4가 있다면 4로그 나옴
     *
     * ex) 0으로 설정하면 123이 다 보이는거
     * ex) 1로 설정하면 23만 보이는거
     * ex) 2로 설정하면 3만 보이는거ok?
     */
    private static int DEBUG_LEVEL = DEBUG_LEVEL_2;

    /**
     * 1 : 사용자 로그 급 기본은 0
     * 2 : 어플 간 연동 로그 급 ex) 어플간 연동 로그급 이상만 표시됨
     * 3 : 서버 연동하는 중요 로그급 ex) 서버매니져에서 사용되는 로그급 이상만 표시됨
     *
     * @author Owner
     * @date 2012. 9. 12.
     * @param level
     * @param message
     */
    public static void v(int level, String message)
    {
        if (DEBUG_MODE)
        {
            if (level > DEBUG_LEVEL)
            {
                String tag = "";
                // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
                StackTraceElement traceElement = new Throwable().getStackTrace()[1];
                String temp = traceElement.getClassName();
                String logTag = "";
                if (temp != null)
                {
                    int lastDotPos = temp.lastIndexOf(".");
                    if (((lastDotPos + 1) >= 0) && ((lastDotPos + 1) <= temp.length())) {
                        tag = temp.substring(lastDotPos + 1);
                    }
                    // 로그 태그 설정 추가 (2015.12.9 by Kwang)
                    logTag = getLogTag(temp);
                }
                String methodName = traceElement.getMethodName();
                int lineNumber = traceElement.getLineNumber();

                Log.v(logTag, "[" + tag + "] " + methodName + "()" + "[" + lineNumber + "]"
                        + " >> " + message);
            }
        }
    }

    public static void i(int level, String message)
    {
        if (DEBUG_MODE)
        {
            if (level > DEBUG_LEVEL)
            {
                String tag = "";
                // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
                StackTraceElement traceElement = new Throwable().getStackTrace()[1];
                String temp = traceElement.getClassName();
                String logTag = "";
                if (temp != null)
                {
                    int lastDotPos = temp.lastIndexOf(".");
                    if (((lastDotPos + 1) >= 0) && ((lastDotPos + 1) <= temp.length())) {
                        tag = temp.substring(lastDotPos + 1);
                    }
                    // 로그 태그 설정 추가 (2015.12.9 by Kwang)
                    logTag = getLogTag(temp);
                }
                String methodName = traceElement.getMethodName();
                int lineNumber = traceElement.getLineNumber();

                Log.i(logTag, "[" + tag + "] " + methodName + "()" + "[" + lineNumber + "]"
                        + " >> " + message);
            }
        }
    }

    public static void d(int level, String message)
    {
        if (DEBUG_MODE)
        {
            if (level > DEBUG_LEVEL)
            {
                String tag = "";
                // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
                StackTraceElement traceElement = new Throwable().getStackTrace()[1];
                String temp = traceElement.getClassName();
                String logTag = "";
                if (temp != null)
                {
                    int lastDotPos = temp.lastIndexOf(".");
                    if (((lastDotPos + 1) >= 0) && ((lastDotPos + 1) <= temp.length())) {
                        tag = temp.substring(lastDotPos + 1);
                    }
                    // 로그 태그 설정 추가 (2015.12.9 by Kwang)
                    logTag = getLogTag(temp);
                }
                String methodName = traceElement.getMethodName();
                int lineNumber = traceElement.getLineNumber();

                Log.d(logTag, "[" + tag + "] " + methodName + "()" + "[" + lineNumber + "]"
                        + " >> " + message);
            }
        }
    }

    public static void w(int level, String message)
    {
        if (DEBUG_MODE)
        {
            if (level > DEBUG_LEVEL)
            {
                String tag = "";
                // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
                StackTraceElement traceElement = new Throwable().getStackTrace()[1];
                String temp = traceElement.getClassName();
                String logTag = "";
                if (temp != null)
                {
                    int lastDotPos = temp.lastIndexOf(".");
                    if (((lastDotPos + 1) >= 0) && ((lastDotPos + 1) <= temp.length())) {
                        tag = temp.substring(lastDotPos + 1);
                    }
                    // 로그 태그 설정 추가 (2015.12.9 by Kwang)
                    logTag = getLogTag(temp);
                }
                String methodName = traceElement.getMethodName();
                int lineNumber = traceElement.getLineNumber();

                Log.w(logTag, "[" + tag + "] " + methodName + "()" + "[" + lineNumber + "]"
                        + " >> " + message);
            }
        }
    }

    public static void e(int level, String message)
    {
        if (DEBUG_MODE)
        {
            if (level > DEBUG_LEVEL)
            {
                String tag = "";
                // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
                StackTraceElement traceElement = new Throwable().getStackTrace()[1];
                String temp = traceElement.getClassName();
                String logTag = "";
                if (temp != null)
                {
                    int lastDotPos = temp.lastIndexOf(".");
                    if (((lastDotPos + 1) >= 0) && ((lastDotPos + 1) <= temp.length())) {
                        tag = temp.substring(lastDotPos + 1);
                    }
                    // 로그 태그 설정 추가 (2015.12.9 by Kwang)
                    logTag = getLogTag(temp);
                }
                String methodName = traceElement.getMethodName();
                int lineNumber = traceElement.getLineNumber();

                Log.e(logTag, "[" + tag + "] " + methodName + "()" + "[" + lineNumber + "]"
                        + " >> " + message);
            }
        }
    }

    public static void e(int level, String tag, String message){
        if (DEBUG_MODE) {
            if (level > DEBUG_LEVEL) {
                // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
                StackTraceElement traceElement = new Throwable().getStackTrace()[1];
                String temp = traceElement.getClassName();
                String logTag = "";
                if (temp != null)
                {
                    logTag = getLogTag(temp);
                }
                Log.e(logTag, "[ERR][" + tag + "] "+ message);
            }
        }
    }

    public static void debugNativeHeap()
    {
        if (DEBUG_MODE)
        {
            String tag = "";
            // LogUtil의 성능 저하를 막기 위해 getStackTrace 호출을 한번만 하도록 변수 추가 (2015.08.27 by ko)
            StackTraceElement traceElement = new Throwable().getStackTrace()[1];
            String temp = traceElement.getClassName();
            if (temp != null)
            {
                int lastDotPos = temp.lastIndexOf(".");
                if (((lastDotPos + 1) >= 0) && ((lastDotPos + 1) <= temp.length())) {
                    tag = temp.substring(lastDotPos + 1);
                }
            }
            String methodName = traceElement.getMethodName();
            int lineNumber = traceElement.getLineNumber();

            Log.i(APP_NAME, "[" + tag + "] " + methodName + "()" + "[" + lineNumber + "]"
                    + " >> " + "NativeHeapSize=" + Debug.getNativeHeapSize()
                    + " NativeHeapFreeSize=" + Debug.getNativeHeapFreeSize()
                    + " NativeHeapAllocatedSize()=" + Debug.getNativeHeapAllocatedSize());
        }
    }
    /**
     * App에 따라 로그 태그를 변경하는 메소드 추가 (2015.12.9 by Kwang)
     * @param packageName
     * @return
     */
    private static String getLogTag(String packageName)
    {
//    	String tag = "";
        StringBuffer tagSb = new StringBuffer(32);

        if((packageName).contains("com.lguplus.android.tv") || (packageName).contains("com.android.tv")) {
            tagSb.append("LIVE-PINEONE-TVAPP");
        } else {
            // 대그룹 설정 (2014.10.22 by ko)
            if (packageName.contains("livewidget") || packageName.contains("sns") || packageName.contains("multiview"))
//    		tag = "LIVE-PINEONE-";
                tagSb.append("LIVE-PINEONE-");
            else if (packageName.contains("category") || packageName.contains("vod") || packageName.contains("menu"))
//    		tag = "VOD-PINEONE-";
                tagSb.append("VOD-PINEONE-");
            else
//    		tag = "ETC-PINEONE-";
                tagSb.append("ETC-PINEONE-");

//    	// 회사명 설정 (2014.10.22 by ko)
//    	tag += "PINEONE-";

            // 어플 명 설정 (2014.10.23 by ko)
            /*String[] arr = packageName.split("\\.");

            if (arr != null && arr.length > 4) {
//    		tag += arr[4].toUpperCase();
                tagSb.append(arr[4].toUpperCase());
            }*/
            tagSb.append("LAUNCHER");
        }

//    	return tag;
        return tagSb.toString();
    }
}

