package calculator.commands;

class Help implements Command {
    @Override
    public boolean run() {
        System.out.println("The program calculates the sum of numbers");
        return false;
    }
}
