package noor.callListener;

/**
 * Created by asif on 25-Feb-18.
 */

public class Numbers {

    int id;
    String number;
    String name;

    public Numbers() {
    }

    public Numbers( String number, String name) {
        this.number = number;
        this.name = name;
    }

    public Numbers(int id, String number, String name) {
        this.id = id;
        this.number = number;
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
