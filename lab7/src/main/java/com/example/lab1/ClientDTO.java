package com.example.lab1;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@Builder
public class ClientDTO implements Serializable {

    private dtoType dtoType;
    private dtoObject dtoObject;
    private dtoReply serverMessage;
    private String toName;
    private ArrayList<Bee> beeList;
    private ArrayList<String> clients;

    public enum dtoType {
        CLIENT_REQUEST,
        CLIENT_REPLY,
        SERVER_REQUEST,
        SERVER_REPLY,
        SERVER_ERROR;
    }

    public enum dtoObject {
        CLIENT_LIST,
        OBJECTS;
    }

    public enum dtoReply {
        SUCCESS,
        ERROR;
    }
}
