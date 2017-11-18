import java.util.Observable;
import java.util.Observer;

public class ObserverA extends Observable implements Observer {

    @Override
    public void update(Observable o, Object arg) {
        ObserverB observerB = (ObserverB)o;
        System.out.println("observerB changed, the new value of observerB.data is: " + observerB.data);
        this.setChanged();
        this.notifyObservers();
    }

    public static class ObserverB extends Observable implements Observer {
        int data = 0;

        @Override
        public void update(Observable o, Object arg) {
            System.out.println("ObserverB found ObserverA changed");
        }

        public void setData(int data){
            this.data = data;
            this.setChanged();
            this.notifyObservers();
        }
    }

    public static void main(String[] args) {
        ObserverA a = new ObserverA();
        ObserverB b = new ObserverB();
        a.addObserver(b);
        b.addObserver(a);
        b.setData(10);

    }
}
