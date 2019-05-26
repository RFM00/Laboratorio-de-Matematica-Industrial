import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {
  public static Random rd = new Random(15);

  public static void shuffler(int ar[]) {
    Random rnd = ThreadLocalRandom.current();
    for (int i = ar.length - 1; i > 0; i--) {
      int index = rnd.nextInt(i + 1);
      int a = ar[index];
      ar[index] = ar[i];
      ar[i] = a;
    }
  }

  public static void swap(int a, int b){
    int x = a;
    a = b;
    b = x;
  }
}