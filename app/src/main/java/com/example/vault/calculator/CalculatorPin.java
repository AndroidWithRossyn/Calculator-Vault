package com.example.vault.calculator;

import org.apache.commons.io.FilenameUtils;

public class CalculatorPin {
    private CalculatorPinSetting context;
    private Equation eq = new Equation();
    private EquationSolver equationSolver;

    public CalculatorPin(CalculatorPinSetting calculatorPinSetting) {
        this.context = calculatorPinSetting;
        this.equationSolver = new EquationSolver(calculatorPinSetting);
        update();
    }

    public String getText() {
        return this.eq.getText();
    }

    public void setText(String str) {
        this.eq.setText(str);
        update();
    }

    public void decimal() {
        if (!Character.isDigit(this.eq.getLastChar())) {
            digit('0');
        }
        if (!this.eq.getLast().contains(".")) {
            this.eq.attachToLast(FilenameUtils.EXTENSION_SEPARATOR);
        }
        update();
    }

    public void delete() {
        if (this.eq.getLast().length() <= 1 || (!this.eq.isRawNumber(0) && this.eq.getLast().charAt(0) != '-')) {
            this.eq.removeLast();
        } else {
            this.eq.detachFromLast();
        }
        update();
    }

    public void digit(char c) {
        if (this.eq.isRawNumber(0)) {
            this.eq.attachToLast(c);
        } else {
            if (this.eq.isNumber(0)) {
                this.eq.add("*");
            }
            Equation equation = this.eq;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(c);
            equation.add(sb.toString());
        }
        update();
    }

    public void equal() {
        String str;
        try {
            str = this.equationSolver.formatNumber(this.equationSolver.solve(getText()));
        } catch (Exception unused) {
            str = "Error";
        }
        this.context.displayPrimaryScrollLeft(str);
        this.context.displaySecondary("");
        this.eq = new Equation();
        if (!str.contains("∞") && !str.contains("Infinity") && !str.contains("NaN")) {
            this.eq.add(str);
        }
    }

    public void num(char c) {
        if (this.eq.getLast().endsWith(".")) {
            this.eq.detachFromLast();
        }
        if (!this.eq.isRawNumber(0) || this.eq.getLastChar() != '-') {
            if (this.eq.isNumber(0)) {
                this.eq.add("*");
            }
            Equation equation = this.eq;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(c);
            equation.add(sb.toString());
        } else {
            this.eq.attachToLast(c);
        }
        update();
    }

    public void numOp(char c) {
        if (this.eq.getLast().endsWith(".")) {
            this.eq.detachFromLast();
        }
        if (this.eq.isNumber(0)) {
            Equation equation = this.eq;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(c);
            equation.add(sb.toString());
        }
        update();
    }

    public void numOpNum(char c) {
        update();
    }

    public void opNum(char c) {
        if (this.eq.getLast().endsWith(".")) {
            this.eq.detachFromLast();
        }
        if (this.eq.getLast().equals("-")) {
            this.eq.attachToLast(c);
        } else {
            if (this.eq.isNumber(0)) {
                this.eq.add("*");
            }
            Equation equation = this.eq;
            StringBuilder sb = new StringBuilder();
            sb.append("");
            sb.append(c);
            equation.add(sb.toString());
        }
        update();
    }

    public void parenthesisLeft() {
        if (this.eq.getLast().endsWith(".")) {
            this.eq.detachFromLast();
        }
        if (!this.eq.getLast().equals("-") || !this.eq.isRawNumber(0)) {
            if (this.eq.isNumber(0)) {
                this.eq.add("*");
            }
            this.eq.add("(");
        } else {
            this.eq.attachToLast('(');
        }
        update();
    }

    public void parenthesisRight() {
        if (this.eq.numOf('s') + this.eq.numOf('c') + this.eq.numOf('t') + this.eq.numOf('n') + this.eq.numOf('l') + this.eq.numOf((char) 8730) + this.eq.numOf('(') > this.eq.numOf(')') && this.eq.isNumber(0)) {
            this.eq.add(")");
        }
        update();
    }

    private void update() {
        this.context.displayPrimaryScrollRight(getText());
        try {
            this.context.displaySecondary(this.equationSolver.formatNumber(this.equationSolver.solve(getText())));
        } catch (Exception unused) {
            this.context.displaySecondary("");
        }
    }
}
