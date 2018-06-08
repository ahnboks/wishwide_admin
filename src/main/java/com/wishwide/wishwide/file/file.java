package com.wishwide.wishwide.file;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;

@Component
public class file {
    @Autowired
    static ServletContext servletContext;

    @Autowired
    public static void main(String[] args) {

        System.out.println(servletContext.getRealPath("/"));

    }

    @PostConstruct
    private void inint(){
        servletContext = servletContext;
    }

}
