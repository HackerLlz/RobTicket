package com.duriamuk.robartifact.common.validate;


import org.thymeleaf.util.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidateResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String EMPTY_MESSAGE = "";
    private static final String DEFAULT_MSG_SEPERATOR = ",";
    private List<String> messages = new ArrayList<>();

    public void addErrorMessage(String msg) {
        this.messages.add(msg);
    }

    public boolean hasError() {
        return messages.size() > 0;
    }

    public String getErrorMessages() {
        if (!hasError()) {
            return EMPTY_MESSAGE;
        }
        return StringUtils.join(messages, DEFAULT_MSG_SEPERATOR);
    }

    public List<String> getValideMessageList() {
        return this.messages;
    }

}
