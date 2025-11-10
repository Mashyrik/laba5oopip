package com.example.laba5;

import java.io.Serializable;

public interface AdminCommand extends Serializable {
    boolean execute();
    void undo();
}