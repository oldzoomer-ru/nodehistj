package ru.gavrilovegor519.nodehistj_historic_nodelists.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import ru.gavrilovegor519.dto.NodelistEntryDto;

import java.util.Map;

@Getter
@Setter
@Document
public class NodelistEntity {

    @Id
    private ObjectId id;

    @Field("nodelist_year")
    @Indexed(unique = true)
    private Integer nodelistYear;

    @Field("nodelist_name")
    @Indexed(unique = true)
    private String nodelistName;

    @Field("nodelist")
    private Map<Integer, NodelistEntryDto> nodelist;

}