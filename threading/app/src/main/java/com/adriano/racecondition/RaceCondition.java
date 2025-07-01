
public class RaceCondition {
    private static volatile boolean done; //without volatile the variable get cached or stored in register without cross the memory barrier
    //that means others thread can't see the modification

    public static void main(String[] args) throws Exception {
        new Thread(
                new Runnable() {
                    public void run() {
                        int i = 0;
                        while (!done) {
                            i++;
                        }
                        System.out.println("Done!");
                    }
                }).start();

        System.out.println("OS: " + System.getProperty("os.name"));
        Thread.sleep(2000);
        done = true;
        System.out.println("flag done set to true");
    }
}
