package com.cts.adverseevent.api;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApiMessage {

    private String message;
    private LocalDateTime timestamp;

    public ApiMessage(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}