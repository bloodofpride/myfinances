package com.mponte.minhasfinancas.services.exceptions;

public class ErroAutenticacao extends RuntimeException{
    public ErroAutenticacao(String msg){
        super(msg);
    }
}
