package me.jiho.springdatajpa.post.dto;

import lombok.Getter;
import me.jiho.springdatajpa.common.BaseDto;
import org.hibernate.metamodel.model.domain.IdentifiableDomainType;

/**
 * @author jiho
 * @since 2021/01/27
 */
@Getter
public class PostUpdateRequestDto extends BaseDto {

    private final String text;

    public PostUpdateRequestDto(Long id, String text) {
        super(id);
        this.text = text;
    }
}
