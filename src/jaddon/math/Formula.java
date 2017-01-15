/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

import jaddon.controller.StaticStandard;
import jaddon.exceptions.FormulaException;
import jaddon.utils.JUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author Paul Hagedorn
 */
public class Formula {
    
    public static final char[] LETTERS_LOW = "abcdefghijklmnopqrstuvwxyzäöü".toCharArray();
    public static final char[] LETTERS_HIGH = "ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÜ".toCharArray();
    public static final char[] NUMBERS = "0123456789,.".toCharArray();
    public static final char[] OPERATORS = "+-*/%^()=".toCharArray();
    public static final char[] ALLOWED_CHARACTERS = (new String(LETTERS_LOW) + new String(LETTERS_HIGH) + new String(NUMBERS) + new String(OPERATORS)).toCharArray();
    
    private final HashMap<String, Double> values = new HashMap<>();
    private final ArrayList<Operation> list = new ArrayList<>();
    private String formula = "";
    
    public Formula() {
        
    }
    
    public Formula(String formula) {
        try {
            setFormula(formula);
        } catch (FormulaException ex) {
            ex.printStackTrace();
        }
    }
    
    public double reload() {
        final ArrayList<Operation> list_temp = new ArrayList<>();
        for(Operation operation : list) {
            list_temp.add(operation);
        }
        list_temp.sort(new Comparator() {
            
            @Override
            public int compare(Object o1, Object o2) {
                Operation op1 = (Operation) o1;
                Operation op2 = (Operation) o2;
                if(op1.getPriority() == op2.getPriority() && op1.getListNumber() == op2.getListNumber()) {
                    return 0;
                } else if(op1.getPriority() < op2.getPriority()) {
                    return op1.getListNumber() - op2.getListNumber();
                } else if(op1.getPriority() > op2.getPriority()) {
                    return - op1.getListNumber() + op2.getListNumber();
                }
                return 0;
            }
            
        });
        final HashMap<Integer, ArrayList<Operation>> list_prior = new HashMap<>();
        for(Operation operation : list_temp) {
            StaticStandard.log(operation);
            int i = 0;
            if(!list_prior.containsKey(operation.getPriority())) {
                list_prior.put(operation.getPriority(), new ArrayList<>());
            } else {
                if(list_prior.get(operation.getPriority()).get(list_prior.get(operation.getPriority()).size() - 1).getListNumber() < operation.getListNumber() - 1) {
                    while(list_prior.containsKey(operation.getPriority() + i)) {
                        i++;
                    }
                    list_prior.put(operation.getPriority() + i, new ArrayList<>());
                }
            }
            list_prior.get(operation.getPriority() + i).add(operation);
        }
        ArrayList<Double> temp_data = new ArrayList<>();
        for(int i : list_prior.keySet()) {
            temp_data.add(solve(list_prior.get(i)));
            StaticStandard.log("prior: " + i + ", value = " + temp_data.get(temp_data.size() - 1));
        }
        return -1.0;
    }
    
    public void initValues() {
        values.clear();
        list.clear();
        boolean variable = false;
        boolean number = false;
        boolean operator = false;
        int priority = 0;
        Operation operation = null;
        for(int i = 0; i < formula.length(); i++) {
            char c = formula.charAt(i);
            if(("" + c).equals("(")) {
                priority++;
            }
            if(!variable && !number && !operator) {
                if(JUtils.arrayCharContains(c, NUMBERS)) {
                    operation = new Operation("", "" + c);
                    operation.setPriority(priority);
                    operation.setNumberOnly(true);
                    number = true;
                } else if(JUtils.arrayCharContains(c, LETTERS_LOW) || JUtils.arrayCharContains(c, LETTERS_HIGH)) {
                    operation = new Operation("" + c, "");
                    operation.setPriority(priority);
                    variable = true;
                } else if(JUtils.arrayCharContains(c, OPERATORS)) {
                    operator = true;
                    operation = new Operation("" + c, "");
                    operation.setPriority(priority);
                    operation.setListNumber(list.size());
                    list.add(operation);
                    operation = null;
                    operator = false;
                }
            } else if(variable) {
                if(JUtils.arrayCharContains(c, OPERATORS) || ("" + c).equals(" ")) {
                    operation.setListNumber(list.size());
                    list.add(operation);
                    operation = null;
                    variable = false;
                    if(JUtils.arrayCharContains(c, NUMBERS)) {
                        operation = new Operation("", "" + c);
                        operation.setPriority(priority);
                        operation.setNumberOnly(true);
                        number = true;
                    } else if(JUtils.arrayCharContains(c, OPERATORS)) {
                        operator = true;
                        operation = new Operation("" + c, "");
                        operation.setPriority(priority);
                        operation.setListNumber(list.size());
                        list.add(operation);
                        operation = null;
                        operator = false;
                    }
                } else {
                    operation.setOperation(operation.getOperation() + c);
                }
            } else if(number) {
                if(!JUtils.arrayCharContains(c, NUMBERS)) {
                    operation.setListNumber(list.size());
                    list.add(operation);
                    operation = null;
                    number = false;
                    if(JUtils.arrayCharContains(c, LETTERS_LOW) || JUtils.arrayCharContains(c, LETTERS_HIGH)) {
                        operation = new Operation("" + c, "");
                        operation.setPriority(priority);
                        variable = true;
                    } else if(JUtils.arrayCharContains(c, OPERATORS)) {
                        operator = true;
                        operation = new Operation("" + c, "");
                        operation.setPriority(priority);
                        operation.setListNumber(list.size());
                        list.add(operation);
                        operation = null;
                        operator = false;
                    }
                } else {
                    operation.setNumber(operation.getNumber() + c);
                }
            }
            if(("" + c).equals(")")) {
                priority--;
            }
        }
        if(operation != null) {
            operation.setListNumber(list.size());
            list.add(operation);
            operation = null;
        }
    }
    
    private Double solve(ArrayList<Operation> ops) {
        double end = 0;
        double temp = 0;
        String operator = "";
        boolean last_operator = false;
        for(Operation op : ops) {
            if(op.getOperation().equals("(") || op.getOperation().equals(")")) {
                continue;
            }
            if(op.isNumberOnly()) {
                if(last_operator) {
                    switch(operator) {
                        case "+":
                            temp*= op.getRealNumber();
                            break;
                        case "-":
                            temp -= op.getRealNumber();
                            break;
                        case "*":
                            temp *= op.getRealNumber();
                            break;
                        case "/":
                            temp /= op.getRealNumber();
                            break;
                        case "%":
                            temp %= op.getRealNumber();
                            break;
                        case "^":
                            temp = Math.pow(temp, op.getRealNumber());
                            break;
                    }
                } else {
                    temp = op.getRealNumber();
                }
                last_operator = false;
            } else {
                operator = op.getOperation();
                last_operator = true;
            }
        }
        end = temp;
        return end;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) throws FormulaException {
        final int brackets_open = JUtils.countInString(formula, "(");
        final int brackets_closed = JUtils.countInString(formula, ")");
        final int equals = JUtils.countInString(formula, "=");
        if(brackets_open > brackets_closed) {
            throw new FormulaException("Brackets not closed");
        } else if(brackets_open < brackets_closed) {
            throw new FormulaException("Brackets not opened");
        } else if(equals > 1) {
            //TODO das kann weg, falls man multiple abfragen machen will wie a = b = c
            throw new FormulaException("More than one equal is not allowed");
        }
        this.formula = formula;
        initValues();
    }

    public HashMap<String, Double> getValues() {
        return values;
    }

    public ArrayList<Operation> getList() {
        return list;
    }
    
}
