/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jaddon.math;

/**
 *
 * @author Paul
 */
public class Operation {
    
    private String operation = "";
    private String number = "";
    private boolean number_only = false;
    private int priority = 0;
    private int list_number = 0;
    
    public Operation(String operation, String number) {
        this.operation = operation;
        this.number = number;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isNumberOnly() {
        return number_only;
    }

    public void setNumberOnly(boolean number_only) {
        this.number_only = number_only;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getListNumber() {
        return list_number;
    }

    public void setListNumber(int list_number) {
        this.list_number = list_number;
    }
    
    public double getRealNumber() {
        return Double.parseDouble(number);
    }
    
    @Override
    public String toString() {
        return "list_number = " + list_number + ", Operation: " + operation + ", Number: " + number + ", priority = " + priority + ", number_only = " + number_only;
    }
    
}
