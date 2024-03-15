package org.rascmatt.pushdemo.dto.payload;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ActionDTO {
    private String title;
    private String action;
}
