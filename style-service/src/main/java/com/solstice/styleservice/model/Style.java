package com.solstice.styleservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Style {

    @ApiModelProperty(value = "An auto-generated identifier")
    @Id
    private String id;

    @ApiModelProperty(value = "A unique name for the style to identify with", required = true)
    @JsonProperty(required = true)
    @NonNull
    @Indexed(unique = true)
    private String name;

    @ApiModelProperty(value = "The color to be used for the asset theme")
    private String themeColor;

    @ApiModelProperty(value = "The type of the service that will be using this style")
    private String serviceName;

    @ApiModelProperty(value = "The image content to be used as the logo")
    private String logo;

}
