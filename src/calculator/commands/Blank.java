package calculator.commands;

import calculator.exceptions.BadExpression;

class Blank implements Command {
    @Override
    public boolean run() {
//        return false;
        throw new BadExpression();
    }
}
