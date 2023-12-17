//This file is generated from prova.vm

//push constant 10
    @10
    D=A
    @SP
    A=M
    M=D
    @SP
    M=M+1
//pop local 0
    @LCL
    D=M
    @0
    D=A+D
    @R13
    M=D
    @SP
    A=M
    A=A-1
    D=M
    @13
    A=M
    M=D
    @SP
    M=M-1