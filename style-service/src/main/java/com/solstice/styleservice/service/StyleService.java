package com.solstice.styleservice.service;

import com.solstice.styleservice.exception.DuplicateEntryException;
import com.solstice.styleservice.exception.StyleNotFoundException;
import com.solstice.styleservice.model.Style;
import com.solstice.styleservice.repository.StyleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A service which consumes the Style Repository
 */
@Component
public class StyleService {

    private static final String INDEX_REGEX = "(?:index: )([^ ]*) ";

    private final StyleRepository styleRepository;

    @Autowired
    public StyleService(StyleRepository styleRepository) {
        this.styleRepository = styleRepository;
    }

    /**
     * Retrieves all styles
     *
     * @return All styles
     */
    public Iterable<Style> findAll() {
        return styleRepository.findAll();
    }

    /**
     * Retrieves a single style by the id of the style.
     *
     * @param id The unique identifier of the style to find
     * @return The style that matches the name provided
     */
    public Style getById(String id) throws StyleNotFoundException {
        Style style = styleRepository.getById(id);

        if (style == null) {
            throw new StyleNotFoundException(String.format("No style found with id of '%s'", id));
        }

        return style;
    }

    /**
     * Retrieves a single style by the name of the style.
     *
     * @param name The unique name of the style to find
     * @return The style that matches the name provided
     */
    public Style getByName(String name) throws StyleNotFoundException {
        Style style = styleRepository.getByName(name);

        if (style == null) {
            throw new StyleNotFoundException(String.format("No style found with name of '%s'", name));
        }

        return style;
    }

    /**
     * Saves the style to storage
     *
     * @param style The style object to save to storage
     */
    public void save(Style style) throws DuplicateEntryException {
        try {
            styleRepository.save(style);
        } catch (DuplicateKeyException ex) {
            Pattern p = Pattern.compile(INDEX_REGEX);
            Matcher m = p.matcher(ex.getMessage());
            String message = ex.getMessage();
            if (m.find()) {
                message = String.format("Duplicate entry for '%s' field", m.group(1));
            }

            throw new DuplicateEntryException(message);
        }
    }

    /**
     * Deletes a style
     */
    public void delete(Style style) {
        styleRepository.delete(style);
    }

    /**
     * Performs a getById and delete
     *
     * @param id The unique identifier
     */
    public void deleteById(String id) {
        styleRepository.delete(getById(id));
    }

    /**
     * Performs a getById and delete
     *
     * @param name The unique name
     */
    public void deleteByName(String name) {
        styleRepository.delete(getByName(name));
    }

}