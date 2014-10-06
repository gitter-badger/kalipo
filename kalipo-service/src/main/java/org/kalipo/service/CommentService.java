package org.kalipo.service;

import org.kalipo.aop.EnableArgumentValidation;
import org.kalipo.aop.Throttled;
import org.kalipo.domain.Comment;
import org.kalipo.repository.CommentRepository;
import org.kalipo.security.Privileges;
import org.kalipo.security.SecurityUtils;
import org.kalipo.service.util.Asserts;
import org.kalipo.web.rest.KalipoRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.Future;

@Service
@EnableArgumentValidation
public class CommentService {

    private final Logger log = LoggerFactory.getLogger(CommentService.class);

    @Inject
    private CommentRepository commentRepository;

    @Inject
    private ReputationService reputationService;

    @RolesAllowed(Privileges.CREATE_COMMENT)
    @Throttled
    public void create(Comment comment) throws KalipoRequestException {

        // todo id must not exist id
        save(comment);
    }

    @RolesAllowed(Privileges.CREATE_COMMENT)
    @Throttled
    public void update(Comment comment) throws KalipoRequestException {
        save(comment);
    }

    private void save(Comment comment) throws KalipoRequestException {
        comment.setAuthorId(SecurityUtils.getCurrentLogin());
        comment.setStatus(Comment.Status.APPROVED);
        commentRepository.save(comment);
    }

    @Async
    public Future<List<Comment>> findAll() {
        return new AsyncResult<>(commentRepository.findAll());
    }

    @Async
    public Future<Comment> get(String id) throws KalipoRequestException {
        return new AsyncResult<>(commentRepository.findOne(id));
    }

    @Throttled
    public void delete(String id) throws KalipoRequestException {

        Comment comment = commentRepository.findOne(id);
        Asserts.notNull(comment, "id");

        // todo check permissions
        reputationService.punishDeletingComment(comment);

        commentRepository.delete(id);
    }
}
