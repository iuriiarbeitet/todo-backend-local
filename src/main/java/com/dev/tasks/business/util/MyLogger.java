package com.dev.tasks.business.util;

import lombok.extern.java.Log;

@Log
public class MyLogger {

    public static void debugMethodName(String text) {
        System.out.println();
        System.out.println();
        log.info(text);
    }
}

