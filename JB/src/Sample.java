import java.awt.*;
import java.io.Serializable;

/**
 * Created by User on 03.05.2017.
 */
public class Sample implements Serializable {
    private Object[] arr;

/*    public Sample(Object[] arr)
    {
        this.arr = arr;
    }*/

    public Object[] getArr() { return arr; }

    public Object getArr(int a) { return arr[a]; }

    public void setArr(Object[] arr) { this.arr = arr; }

    public void setArr(Object arr_el, int a) { this.arr[a] = arr_el; }
}
