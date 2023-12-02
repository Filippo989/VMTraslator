import java.io.*;
import java.util.Scanner;

public class VMTranslator {

    public static void main(String[] args) {
        System.out.println("Insert the file path");
        Scanner scanner = new Scanner(System.in);

        File file = new File("/home/filippo/Progetti/Java/NandToTetris/VMTraslator/src/prova.vm");
        Parser parser = new Parser(file);
        CodeWriter codeWriter = new CodeWriter("/home/filippo/Progetti/Java/NandToTetris/VMTraslator/src/prova.vm", file.getName());

        while(parser.hasNextLine()) {
            parser.advance();
            switch(parser.getCommandType()) {
                case C_ARITHMETIC:
                    System.out.println("ciao" + parser.getArg1());
                    codeWriter.writeArithmetic(parser.getArg1());
                    break;
                case C_PUSH:
                case C_POP:
                    codeWriter.writePushPop(parser.getCommandType(), parser.getArg1(), parser.getArg2());
                    break;
                case C_LABEL:
                    System.out.println("label");
                    break;
                case C_GOTO:
                    System.out.println("goto");
                    break;
                case C_IF:
                    System.out.println("if");
                    break;
                case C_FUNCTION:
                    System.out.println("function");
                    break;
                case C_RETURN:
                    System.out.println("return");
                    break;
                case C_CALL:
                    System.out.println("call");
                    break;
            }
        }

    }

}
