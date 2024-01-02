import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import static java.lang.System.exit;
import static java.lang.System.in;

//Giusto

public class CodeWriter {

    private String path;
    private BufferedWriter bufferedWriter;
    private String fileName;
    private ArrayList<String> code;
    private int labelCounter = 0;
    private FileWriter fileWriter;

    public CodeWriter(String previousPath, String fileName) {
        path = addAsm(previousPath);
        this.fileName = fileName;
        code = new ArrayList<>();

        // Initializing BufferedWriter
        try {
            fileWriter = new FileWriter(path);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write("//This file is generated from " + fileName);
            bufferedWriter.newLine();
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void writePushPop(CommandType type, String segment, int index) {
        code.clear();
        if(type == CommandType.C_PUSH) {
            code.add("//push " + segment + " " + index);
            switch(segment) {
                case "local":
                case "argument":
                case "this":
                case "that":
                    calculateStandardOffset(segment, index);
                    code.add("");
                    code.add("");
                    break;
                case "static":
                    code.add("@" + removeVm(fileName) + "." + index);
                    code.add("D=M");
                    break;
                case "temp":
                    int i = 5 + index;
                    code.add("@"+i);
                    code.add("D=A");
                    code.add("@"+index);
                    code.add("A=A+D");
                    code.add("D=M");
                    break;
                case "constant":
                    code.add("@"+index);
                    code.add("D=A");
                    break;
                case "pointer":
                    if(index == 0) {
                        code.add("@THIS");
                    } else if (index == 1) {
                        code.add("@THAT");
                    }
                    code.add("D=A");
                    code.add("A=D");
                    code.add("D=M");
                    break;
            }

            code.add("@SP");
            code.add("A=M");
            code.add("M=D");
            code.add("@SP");
            code.add("M=M+1");
        } else if(type == CommandType.C_POP) {
            code.add("//pop " + segment + " " + index);
            switch(segment){
                case "local":
                case "argument":
                case "this":
                case "that":
                    calculateStandardOffset(segment, index);
                    break;
                case "static":
                    writeStaticPop(index);
                    writeToFile();
                    return;
                case "temp":
                    int i = 5 + index;
                    code.add("@"+i);
                    code.add("D=A");
                    break;
                case "constant":
                    System.out.println("ERROR, CANNOT POP CONSTANT!");
                    break;
                case "pointer":
                    if(index == 0) {
                        code.add("@THIS");
                    } else if (index == 1) {
                        code.add("@THAT");
                    }
                    code.add("D=A");
                    break;
            }
            code.add("@R13");
            code.add("M=D");
            code.add("@SP");
            code.add("A=M");
            code.add("A=A-1");
            code.add("D=M");
            code.add("@13");
            code.add("A=M");
            code.add("M=D");
            code.add("@SP");
            code.add("M=M-1");
        }
        writeToFile();
    }

    private void writeStaticPop(int index){
        code.add("@SP");
        code.add("AM=M-1");
        code.add("D=M");
        code.add("@" + removeVm(fileName) + "." + index);
        code.add("M=D");
    }

    public void writeArithmetic(String arg1) {
        code.clear();
        code.add("//" + arg1);
        code.add("@SP");
        code.add("A=M");
        code.add("A=A-1");

        //neg or not
        if (arg1.equals("not")) {
            code.add("M=!M");
            //DEBUG
            code.add("space");
            writeToFile();
            return;
        } else if (arg1.equals("neg")) {
            code.add("M=-M");
            //DEBUG
            code.add("space");
            writeToFile();
            return;
        }

        code.add("D=M");
        code.add("A=A-1");

        if(arg1.equals("add") || arg1.equals("sub") || arg1.equals("and") || arg1.equals("or")) {
            writeLogicAndAlgebraic(arg1);
        } else if(arg1.equals("eq") || arg1.equals("gt") || arg1.equals("lt")) {
            writeCondition(arg1);
        }

        writeToFile();
    }

    private void writeLogicAndAlgebraic(String arg1) {
        //actual operation
        switch(arg1) {
            case "add" -> code.add("M=D+M");
            case "sub" -> code.add("M=M-D");
            case "and" -> code.add("M=D&M");
            case "or" -> code.add("M=M|D");
        }

        //Decrease stack pointer
        code.add("@SP");
        code.add("M=M-1");

        //DEBUG
        code.add("space");
    }

    private void writeCondition(String arg1) {
        code.add("D=D-M");

        //unique label needed
        code.add("@label." + labelCounter);

        //actual operation
        switch(arg1) {
            case "eq" -> code.add("D;JEQ");
            case "gt" -> code.add("D;JLT");
            case "lt" -> code.add("D;JGT");
        }

        code.add("D=0");
        code.add("@end." + labelCounter);
        code.add("0;JMP");
        code.add("(label." + labelCounter + ")");
        code.add("D=-1");
        code.add("(end." + labelCounter + ")");
        code.add("@SP");
        code.add("M=M-1");
        code.add("A=M");
        code.add("A=A-1");
        code.add("M=D");

        //DEBUG
        code.add("space");

        labelCounter++;
    }

    private void calculateStandardOffset(String segment, int index) {
        switch(segment) {
            case "local":
                code.add("@LCL");
                break;
            case "argument":
                code.add("@ARG");
                break;
            case "this":
                code.add("@THIS");
                break;
            case "that":
                code.add("@THAT");
                break;
        }

        code.add("D=M");
        code.add("@"+index);
        code.add("D=A+D");
    }

    private void writeStandardPop(String segment, int index) {
        switch(segment) {
            case "local":
                code.add("@LCL");
            case "argument":
                code.add("@ARG");
            case "this":
                code.add("@THIS");
            case "that":
                code.add("THAT");
        }
    }

    private String removeVm(String path) {
        return path.replace(".vm", "");
    }

    private String addAsm(String path) {
        return path.replace(".vm", ".asm");
    }

    private void writeToFile() {
        try {
            fileWriter = new FileWriter(path, true);
            bufferedWriter = new BufferedWriter(fileWriter);
            for(String c : code) {
                bufferedWriter.newLine();
                if(!(c.contains("(") || c.contains("//")))
                    bufferedWriter.append("    ");
                if(c.equals("space"))
                    continue;
                bufferedWriter.append(c);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
