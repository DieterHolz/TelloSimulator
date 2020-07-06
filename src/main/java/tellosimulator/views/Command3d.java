package tellosimulator.views;

import java.util.List;

public class Command3d {
    String instruction;
    List<String> parameters;
    int priority;

    public Command3d(String instruction, List<String> parameters, int priority){
        this.instruction = instruction;
        this.parameters = parameters;
        this.priority = priority;
    }


    public String toString() {
        return "instruction: " + instruction + ", params: " + parameters + ", priority: " + priority;
    }


    //Getter and setter

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }
}
