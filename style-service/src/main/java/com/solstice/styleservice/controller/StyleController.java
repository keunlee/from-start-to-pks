package com.solstice.styleservice.controller;

import com.solstice.styleservice.exception.StyleNotFoundException;
import com.solstice.styleservice.model.Style;
import com.solstice.styleservice.service.StyleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/styles")
@Api(value = "/styles", description = "Styles for front end assets")
public class StyleController {

    private final StyleService styleService;

    @Autowired
    public StyleController(StyleService styleService) {
        this.styleService = styleService;
    }

    @ApiOperation(value = "Retrieves all Styles", response = Iterable.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully retrieved list of styles")
    })
    @RequestMapping(method = RequestMethod.GET)
    public Iterable<Style> styles() {
        return styleService.findAll();
    }

    @ApiOperation(value = "Retrieves a single style by id", response = Style.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully found the desired style"),
            @ApiResponse(code = 404, message = "Could not find the desired style")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Style getStyleById(@PathVariable(value = "id") String id) throws StyleNotFoundException {
        return styleService.getById(id);
    }

    @ApiOperation(value = "Creates a style", response = Style.class)
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Successfully created the style"),
            @ApiResponse(code = 400, message = "The style could not be created due to constraints")
    })
    @RequestMapping(method = RequestMethod.POST)
    public Style createStyle(@RequestBody Style style) {
        styleService.save(style);

        return style;
    }

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully updated the desired style"),
            @ApiResponse(code = 404, message = "Could not find the desired style"),
            @ApiResponse(code = 400, message = "The style could not be updated due to constraints")
    })
    @ApiOperation(value = "Updates a single style by id", response = Style.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Style updateStyleById(@PathVariable(value = "id") String id, @RequestBody Style style) {
        Style currentStyle = styleService.getById(id);

        currentStyle.setName(style.getName());
        currentStyle.setThemeColor(style.getThemeColor());
        currentStyle.setServiceName(style.getServiceName());
        currentStyle.setLogo(style.getLogo());

        styleService.save(currentStyle);

        return currentStyle;
    }

    @ApiOperation(value = "Deletes a single style by id")
    @ApiResponses(value = {
            @ApiResponse(code = 204, message = "Successfully deleted the desired style"),
            @ApiResponse(code = 404, message = "Could not find the desired style")
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteStyleByName(@PathVariable(value = "id") String id) {
        Style style = styleService.getById(id);

        styleService.delete(style);
    }

}