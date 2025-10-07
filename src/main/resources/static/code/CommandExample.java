// Command Interface
interface OrderCommand {
    void execute();
}

// Receiver
class Chef {
    public void cookMuttonString() {
        System.out.println("Chef is cooking Mutton String.");
    }

    public void cookChicken() {
        System.out.println("Chef is cooking Chicken.");
    }
}

// Concrete Commands
class MuttonStringOrder implements OrderCommand {
    private Chef chef;

    public MuttonStringOrder(Chef chef) {
        this.chef = chef;
    }

    public void execute() {
        chef.cookMuttonString();
    }
}

class ChickenOrder implements OrderCommand {
    private Chef chef;

    public ChickenOrder(Chef chef) {
        this.chef = chef;
    }

    public void execute() {
        chef.cookChicken();
    }
}

// Invoker
class Servant {
    private OrderCommand order;

    public void setOrder(OrderCommand order) {
        this.order = order;
    }

    public void executeOrder() {
        order.execute();
    }
}

// Client
public class Main {
    public static void main(String[] args) {
        Chef chef = new Chef();
        OrderCommand muttonString = new MuttonStringOrder(chef);
        OrderCommand chicken = new ChickenOrder(chef);

        Servant servant = new Servant();

        // Test with an order of one mutton string and one chicken
        servant.setOrder(muttonString);
        servant.executeOrder();

        servant.setOrder(chicken);
        servant.executeOrder();
    }
}
