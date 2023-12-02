import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Parser {
    private Scanner reader;
    private String currentCommand;

    private String arg1;
    private int arg2;
    private CommandType commandType;

    public Parser(File file) {
        try {
            reader = new Scanner(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean hasNextLine() {
        boolean hasNextLine = true;
        if(!reader.hasNextLine()) {
            hasNextLine = false;
            reader.close();
        }
        return hasNextLine;
    }

    public void advance() {
        currentCommand = reader.nextLine();
        commandType();
        getArgs();
    }

    private void getArgs() {
        String[] line = currentCommand.split(" ");
        if(line.length == 1) {
            arg1 = line[0];
        } else if(line.length == 3) {
            arg1 = line[1];
            arg2 = Integer.parseInt(line[2]);
        }
    }

    private void commandType() {
        if(currentCommand.contains("pop")) {
            commandType = CommandType.C_POP;
        } else if(currentCommand.contains("push")) {
            commandType = CommandType.C_PUSH;
        } else if(currentCommand.contains("function")) {
            commandType = CommandType.C_FUNCTION;
        } else if(currentCommand.contains("call")) {
            commandType = CommandType.C_CALL;
        } else if(currentCommand.contains("label")) {
            commandType = CommandType.C_LABEL;
        } else if(currentCommand.contains("goto")) {
            commandType = CommandType.C_GOTO;
        } else if(currentCommand.contains("if")) {
            commandType = CommandType.C_IF;
        } else if(currentCommand.contains("return")) {
            commandType = CommandType.C_RETURN;
        } else {
            commandType = CommandType.C_ARITHMETIC;
        }
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getArg1() {
        return arg1;
    }

    public int getArg2() {
        return arg2;
    }

}
