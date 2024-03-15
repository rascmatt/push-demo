package org.rascmatt.pushdemo.dto.payload;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
@Builder
public class NotificationDTO {
    private String title;
    private String body;
    private String icon;
    private List<Integer> vibrate;
    private HashMap<String, Serializable> data;
    private List<ActionDTO> actions;
}
