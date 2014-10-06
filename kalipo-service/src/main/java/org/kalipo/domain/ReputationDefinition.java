package org.kalipo.domain;

import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;

/**
 * A Reputation.
 */

@Document(collection = "T_REPUTATION_DEFINITION")
public class ReputationDefinition {

    @Id
    private String id;

    @CreatedDate
    private DateTime createdDate;

//    @LastModifiedDate
//    private DateTime lastModifiedDate;

    @NotNull(message = "{constraint.notnull.type}")
//    @Field("rtype")
    private Type type;

    @NotNull(message = "{constraint.notnull.reputation}")
    private Integer reputation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(DateTime createdDate) {
        this.createdDate = createdDate;
    }

//    public DateTime getLastModifiedDate() {
//        return lastModifiedDate;
//    }
//
//    public void setLastModifiedDate(DateTime lastModifiedDate) {
//        this.lastModifiedDate = lastModifiedDate;
//    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    /**
     * Created by damoeb on 7/28/14.
     */
    public static enum Type {
        LIKE, LIKED, DISLIKED, WELCOME, ABUSED_REPORT, REPORTED, REPORT, RM_COMMENT, DISLIKE
    }
}
