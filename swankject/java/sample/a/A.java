package sample.a;

public class A {

    public static void willThrow(String msg) {
        willThrowSub(msg);
    }

    private static void willThrowSub(String msg) {
        throw new RuntimeException("msg");
    }

    public static void countSlow(int value) {
        count(value,5);
    }

    private static String doStuff(Object o) {
        return "hello " + o;
    }

    public static void countFast(int value) {
        String s = doStuff(new java.util.Date());
        count(value,0);
    }

    private static void count(int value, int delay) {
        for (int i=0;i<value;i++) {
            try {Thread.sleep(delay);} catch (Exception e) {}
        }
    }

    public static void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        }
        catch (Exception e) {
        }
    }

    public static void a(String msg) {
        sleepMillis(1000);
        System.out.println("        This is A!, msg="+msg);
    }
}
