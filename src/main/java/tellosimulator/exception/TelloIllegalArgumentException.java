package tellosimulator.exception;

public class TelloIllegalArgumentException extends IllegalArgumentException {
    public TelloIllegalArgumentException(String command, String paramName, String inputValues, String validValues) { super("Illegal Argument. Command: "+command+", param name: "+paramName+", input value: "+inputValues+", valid value: "+validValues); }
}
