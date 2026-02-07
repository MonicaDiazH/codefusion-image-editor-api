package com.codefusion.image.editor.dto;

import lombok.Data;

@Data
public class MergeRequest {
    private String templateUrl;
    private String infoUrl;

    private int offsetX = 0;
    private int offsetY = 0;
}
