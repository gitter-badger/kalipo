package org.kalipo.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;
import org.kalipo.domain.Comment;
import org.kalipo.domain.Thread;
import org.kalipo.service.ThreadService;
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
 * REST controller for managing Thread.
 */
@RestController
@RequestMapping("/app/rest")
public class ThreadResource {

    private final Logger log = LoggerFactory.getLogger(ThreadResource.class);

    @Inject
    private ThreadService threadService;

    /**
     * POST  /rest/threads -> Create a new thread.
     */
    @RequestMapping(value = "/threads",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(value = "Create a new thread")
    public Thread create(@NotNull @RequestBody Thread thread) throws KalipoException {
        log.debug("REST request to save Thread : {}", thread);

        return threadService.create(thread);
    }

    /**
     * PUT  /rest/threads -> Update existing thread.
     */
    @RequestMapping(value = "/threads/{id}",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Update existing thread")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Thread not found")
    })
    public Thread update(@PathVariable String id, @NotNull @RequestBody Thread thread) throws KalipoException {
        log.debug("REST request to update Thread : {}", thread);

        Asserts.isNotNull(id, "id");

        thread.setId(id);
        return threadService.update(thread);
    }

    /**
     * GET  /rest/threads -> get all the threads.
     */
    @RequestMapping(value = "/threads",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get all the threads")
    public List<Thread> getAll() throws ExecutionException, InterruptedException {
        log.debug("REST request to get all Threads");

        return threadService.getAll().get();
    }

    /**
     * GET  /rest/threads/:id -> get the "id" thread.
     */
    @RequestMapping(value = "/threads/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get the \"id\" thread.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Thread not found")
    })
    public ResponseEntity<Thread> get(@PathVariable String id) throws KalipoException, ExecutionException, InterruptedException {
        log.debug("REST request to get Thread : {}", id);
        Asserts.isNotNull(id, "id");

        return Optional.ofNullable(threadService.get(id).get())
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
    @ApiOperation(value = "Delete the \"id\" thread")
    public void delete(@PathVariable String id) throws KalipoException {
        log.debug("REST request to delete Thread : {}", id);
        Asserts.isNotNull(id, "id");

        threadService.delete(id);
    }

    /**
     * GET  /rest/threads/:id/comments -> get comments of the "id" thread.
     */
    @RequestMapping(value = "/threads/{id}/comments",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ApiOperation(value = "Get comments of the \"id\" thread.")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid ID supplied"),
            @ApiResponse(code = 404, message = "Thread not found")
    })
    public ResponseEntity<List<Comment>> getComments(@PathVariable String id) throws KalipoException, ExecutionException, InterruptedException {
        log.debug("REST request to get Comments of Thread : {}", id);
        Asserts.isNotNull(id, "id");

        return Optional.ofNullable(threadService.getComments(id).get())
                .map(thread -> new ResponseEntity<>(
                        thread,
                        HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
