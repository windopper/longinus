package Exceptions;

public class UndefinedFunctionError extends Exception {
    public UndefinedFunctionError() {
        super("부적절한 기능입니다");
    }
}
