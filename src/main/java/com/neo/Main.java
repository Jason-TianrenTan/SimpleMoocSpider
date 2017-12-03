package com.neo;

import com.neo.IO.Spider;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	// write your code here
        try {
            new Spider().activate();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
