package com.social_media.profileservice.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.Node;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Node("user_profile")
public class UserProfile {


}
