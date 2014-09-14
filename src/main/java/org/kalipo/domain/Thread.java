package org.kalipo.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;
import org.joda.time.DateTime;
import org.kalipo.domain.util.CustomLocalDateSerializer;
import org.joda.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * A Thread.
 */

@Document(collection = "T_THREAD")
public class Thread implements Serializable {

    @Id
    private String id;

    @Size(min = 1, max = 512)
    private String uri;

    @Size(min = 0, max = 128)
    private String title;

//    @NotNull
    @Field("created_date")
    private DateTime createdDate = DateTime.now();

    @Field("last_modified_date")
    private DateTime lastModifiedDate = DateTime.now();

    @Field("comment_count")
    private Integer commentCount = 0;

    @NotNull
    @Field("author_id")
    private String authorId;

    /**
     * Sum of all comment likes plus thread likes
     */
    private Integer likes = 0;

    /**
     * Sum of all comment dislikes plus thread dislikes
     */
    private Integer dislikes = 0;

    /**
     * Disable comments
     */
    @Field("read_only")
    private Boolean readOnly = false; // todo implement readonly

    private Status status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

    public DateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(DateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

    public Boolean getReadOnly() {
        return readOnly;
    }

    public void setReadOnly(Boolean readOnly) {
        this.readOnly = readOnly;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Created by damoeb on 7/28/14.
     */
    public static enum Status {
        OPEN, CLOSED
    }
}