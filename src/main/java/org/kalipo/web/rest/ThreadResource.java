package org.kalipo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.kalipo.config.Constants;
import org.kalipo.domain.Thread;
import org.kalipo.repository.ThreadRepository;
import org.kalipo.web.rest.dto.ThreadDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Thread.
 */
@RestController
@RequestMapping("/app/rest")
public class ThreadResource {

    private final Logger log = LoggerFactory.getLogger(ThreadResource.class);

    @Inject
    private ThreadRepository threadRepository;

    /**
     * POST  /rest/threads -> Create a new thread.
     */
    @RequestMapping(value = "/threads",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new thread")
    public void create(@Valid @RequestBody ThreadDTO threadDTO) {
        log.debug("REST request to save Thread : {}", threadDTO);

        Thread thread = ThreadDTO.convert(threadDTO);
        thread.setAuthorId("d"); // todo SecurityUtils.getCurrentLogin() is null during tests
        thread.setStatus(Thread.Status.OPEN);

        threadRepository.save(thread);
    }

    /**
     * PUT  /rest/comments -> Update existing comment.
     */
    @RequestMapping(value = "/threads/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Update existing thread")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Comment not found")
    })
    public void update(@PathVariable String id, @Valid @RequestBody ThreadDTO threadDTO) throws KalipoRequestException {
        log.debug("REST request to update Thread : {}", threadDTO);

        if (StringUtils.isBlank(id)) {
            throw new IllegalParameterException();
        }

        Thread thread = ThreadDTO.convert(threadDTO);
        thread.setId(id);
        thread.setAuthorId("d"); // todo SecurityUtils.getCurrentLogin() is null during tests
//        thread.setStatus(Thread.Status.OPEN);
        threadRepository.save(thread);
    }

    /**
     * GET  /rest/threads -> get all the threads.
     */
    @RequestMapping(value = "/threads",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get all the threads")
    public List<Thread> getAll() {
        log.debug("REST request to get all Threads");
        return threadRepository.findAll();
    }

    /**
     * GET  /rest/threads/:id -> get the "id" thread.
     */
    @RequestMapping(value = "/threads/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get the \"id\" comment.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Comment not found")
    })
    public ResponseEntity<Thread> get(@PathVariable String id) throws KalipoRequestException {
        log.debug("REST request to get Thread : {}", id);
        if (StringUtils.isBlank(id)) {
            throw new IllegalParameterException();
        }

        return Optional.ofNullable(threadRepository.findOne(id))
            .map(thread -> new ResponseEntity<>(
                thread,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /rest/threads/:id -> delete the "id" thread.
     */
    @RequestMapping(value = "/threads/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "Delete the \"id\" comment")
    public void delete(@PathVariable String id) throws KalipoRequestException {
        log.debug("REST request to delete Thread : {}", id);
        if (StringUtils.isBlank(id)) {
            throw new IllegalParameterException();
        }

        threadRepository.delete(id);
    }
}