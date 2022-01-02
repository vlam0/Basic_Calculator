package com.company;
import java.io.*;
import java.util.*;

public class Calculator
{
    int start_up = 0, newDisplay = 0, AfterEquals = 0, numOfDigits = 0;
    int ignore = 0, Neg = 0;
    String display = "", equation = "";

    public String getDisplayString()
    {
        if (start_up == 0) {
            display = "0";
            start_up = 1;
        }
        //System.out.printf("%s\n", equation);
        return display;
    }
    // Add numbers or (.) to display
    public void numbers(String s)
    {
        //After operator has been pressed
        if (newDisplay == 1)
            display = "";
        if (Neg == 1)
            display = "-";
        //  ignores for multiple (.) for a value on display
        if (s == ".") {
            for (int i = 0; i < display.length(); i++) {
                if (display.charAt(i) == '.') {
                    ignore = 1;
                    break;
                }
            }
        }
        if (ignore == 0) {
            if (Neg == 1)
                Neg = 2;
            equation += s;
            display += s;
            newDisplay = 0;
        }
    }
    //Checks for duplicate operators in a row
    public void duplicateOperator(String s)
    {
        if (equation.length() > 2) {
            if (s == "+" || s == "-" || s == "*" || s == "/" || s == "=") {
                int x = equation.length() - 2;
                if (equation.charAt(x) == '+' || equation.charAt(x) == '-' ||
                        equation.charAt(x) == '*' || equation.charAt(x) == '/')
                    display = "Error";
            }
        }
    }
    // Assigns arraylist for operators and numbers
    public void equationAssignment(ArrayList<Double> operand, ArrayList<Character> op)
    {
        String num = "", temp = "";
        int x = 0, negative = 0, dupDecimal = 0;

        if (equation.charAt(0) == '-') {
            negative = 1;
        }
        for (int i = 0; i < equation.length(); i++) {
            if (equation.charAt(i) != '+' && equation.charAt(i) != '-' &&
                    equation.charAt(i) != '*' && equation.charAt(i) != '/') {
                num += equation.charAt(i);
                if (i == equation.length() - 1) {
                    for (int k = 0; k < num.length(); k++) {
                        if (num.charAt(k) == '.') {
                            dupDecimal++;
                        }
                        if (dupDecimal != 2)
                            temp += num.charAt(k);
                        else
                            break;
                    }
                    if (dupDecimal == 2)
                        num = temp;
                    operand.add(x, Double.parseDouble(num));
                }
            }
            else {
                if (i != 0) {
                    if (negative == 1) {
                        operand.add(x, Double.parseDouble(num) * -1.0);
                        negative = 0;
                    }
                    else {
                        operand.add(x, Double.parseDouble(num));
                    }
                    op.add(x, equation.charAt(i));
                    num = "";
                    x++;
                }
            }
        }
    }
    // Reevaluates each operator and sets new equation running through order of operation
    public void tempStringAssignment(ArrayList<Double> operand, ArrayList<Character> op, String temp, int i)
    {
        for (int k = i + 1; k < numOfDigits; k++) {
            temp += op.get(k);
            temp += Double.toString(operand.get(k+1));
        }
        display = temp;
        equation = temp;
        numOfDigits--;
        temp = "";
        equationAssignment(operand, op);
    }
    // Handles which operator goes first
    public void orderOfOperations(ArrayList<Double> operand, ArrayList<Character> op, String temp, double result)
    {
        int num = numOfDigits;
        temp = "";
        if (Neg == 2) {
            operand.add(0, operand.get(0) * -1.0);
            operand.remove(1);
        }

        for (int j = 0; j < equation.length(); j++) {
            for (int i = 0; i < num; i++) {
                if (op.get(i) == '*') {
                    result = operand.get(i) * operand.get(i+1);
                    temp += String.valueOf(result);
                    tempStringAssignment(operand, op, temp, i);
                    num--;
                    break;
                }
                else if (op.get(i) == '/') {
                    if (operand.get(i+1) == 0) {
                        display = "NaN";
                        break;
                    }
                    result = operand.get(i) / operand.get(i+1);
                    temp += String.valueOf(result);
                    tempStringAssignment(operand, op, temp, i);
                    num--;
                    break;
                }
                else
                    temp += String.valueOf(operand.get(i)) + op.get(i);
            }
        }
        for (int j = 0; j < equation.length(); j++) {
            for (int i = 0; i < num; i++) {
                if (op.get(i) == '+') {
                    result = operand.get(i) + operand.get(i+1);
                    temp = String.valueOf(result);
                    tempStringAssignment(operand, op, temp, i);
                    num--;
                    break;
                }
                else if (op.get(i) == '-') {
                    result = operand.get(i) - operand.get(i+1);
                    temp = String.valueOf(result);
                    tempStringAssignment(operand, op, temp, i);
                    num--;
                    break;
                }
            }
        }
    }
    // Calculate equation and set arraylist
    public void compute()
    {
        ArrayList<Double> operand = new ArrayList<>(numOfDigits);
        ArrayList<Character> op = new ArrayList<>(numOfDigits-1);
        double result = 0;
        int decimalError = 0;
        String temp = "";

        equationAssignment(operand, op);
        orderOfOperations(operand, op, temp, result);
        if (display != "NaN") {
            for (int k = 0; k < display.length(); k++) {
                if (display.charAt(k) == '.')
                    decimalError++;
                if (decimalError != 2)
                    temp += display.charAt(k);
                else
                    break;
            }
            if (decimalError == 2)
                display = temp;
            result = Math.round(Double.parseDouble(display) * 100.0) / 100.0;
            display = Double.toString(result);
            // Removes .0 i.e. 9.0 = 9 for display
            if (display.charAt(display.length() - 2) == '.' && display.charAt(display.length() - 1) == '0') {
                temp = "";
                for (int i = 0; i < display.length() - 2; i++) {
                    temp += display.charAt(i);
                }
                display = temp;
            }
            equation = display;
        }
        AfterEquals = 1;
    }

    public void acceptInput(String s)
    {
        // Sets equation with 0 in the beginning if operator starts
        if (start_up == 1) {
            if (s == "+" || s == "-" || s == "/" || s == "*") {
                display = "0";
                equation += "0";
            }
            else
                display = "";
        }
        if (start_up == 1 && s == "-")
            Neg = 1;
        // Hanldes what happens when after the = operator and chooses to continue
        if (AfterEquals == 1) {
            if (s != "+" && s != "-" && s != "*" && s != "/" && s != "=") {
                equation = "";
                display = "";
                numOfDigits = 0;
            }
            else {
                equation = display;
            }
            AfterEquals = 0;
        }

        switch (s)
        {
            case "+" :
            case "-" :
            case "*" :
            case "/" :
                if (display == "NaN" || display == "Error")
                    break;
                numOfDigits++;
                equation += s;
                newDisplay = 1;
                break;
            case "=" :
                if (display == "NaN" || display == "Error")
                    break;
                if (start_up == 1) {
                    display = "0";
                    AfterEquals = 1;
                    break;
                }
                compute();
                break;
            case "C" :
                display = "0 (cleared)";
                equation = "";
                start_up = 1;
                AfterEquals = 0;
                numOfDigits = 0;
                break;
            default : //numbers or (.)
                if (display == "NaN" || display == "Error")
                    break;
                numbers(s);
                if (ignore == 1)
                    ignore = 0;
                break;
        }
        if (display != "NaN")
            duplicateOperator(s); // Regulates and checks if operators back to back
        start_up = 2;
    }

}
