package org.kalipo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.kalipo.domain.Tag;
import org.kalipo.service.TagService;
import org.kalipo.service.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

/**
 * REST controller for managing Tag.
 * <p>
 * todo: autosuggest matching tag, tag-cloud, tag modification is mainly done internal
 */
@RestController
@RequestMapping("/app")
public class TagResource {

    private final Logger log = LoggerFactory.getLogger(TagResource.class);

    @Inject
    private TagService tagService;

    /**
     * POST  /rest/tags -> Create a new tag.
     */
    @RequestMapping(value = "/rest/tags",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new tag")
    public Tag create(@NotNull @RequestBody Tag tag) throws KalipoException {
        log.debug("REST request to save Tag : {}", tag);

        Asserts.isNull(tag.getId(), "id");

        return tagService.create(tag);
    }

    /**
     * GET  /rest/tags -> get all the tags.
     */
    @RequestMapping(value = "/rest/tags",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get all the tags")
    public List<Tag> getAll() throws ExecutionException, InterruptedException {
        log.debug("REST request to get all Tags");
        return tagService.getAll().get();
    }

    /**
     * GET  /rest/tags/:id -> get the "id" tag.
     */
    @RequestMapping(value = "/rest/tags/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get the \"id\" tag.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Tag not found")
    })
    public ResponseEntity<Tag> get(@PathVariable String id) throws KalipoException, ExecutionException, InterruptedException {
        log.debug("REST request to get Tag : {}", id);
        Asserts.isNotNull(id, "id");

        return Optional.ofNullable(tagService.get(id).get())
                .map(tag -> new ResponseEntity<>(
                        tag,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/tags/:id -> delete the "id" tag.
     */
    @RequestMapping(value = "/rest/tags/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete the \"id\" tag")
    public void delete(@PathVariable String id) throws KalipoException {
        log.debug("REST request to delete Tag : {}", id);
        Asserts.isNotNull(id, "id");

        tagService.delete(id);
    }
}
