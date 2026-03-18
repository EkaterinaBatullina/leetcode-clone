package com.technokratos.problemserviceimpl.specification;

import com.technokratos.problemserviceapi.enums.Difficulty;
import com.technokratos.problemserviceimpl.entity.Problem;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProblemSpecifications {

    public static Specification<Problem> withDifficulties(List<Difficulty> difficulties) {
        return (root, query, builder) -> {
            if (difficulties == null || difficulties.isEmpty()) return null;
            return root.get("difficulty").in(difficulties);
        };
    }

    public static Specification<Problem> withAllTags(List<String> tagNames) {
        return (root, query, builder) -> {
            if (tagNames == null || tagNames.isEmpty()) return null;
            query.distinct(true);
            Subquery<Problem> subquery = query.subquery(Problem.class);
            Root<Problem> subRoot = subquery.from(Problem.class);
            Join<Object, Object> tagsJoin = subRoot.join("tags");
            subquery.select(subRoot.get("id"))
                    .where(tagsJoin.get("name").in(tagNames))
                    .groupBy(subRoot.get("id"))
                    .having(builder.equal(builder.countDistinct(tagsJoin.get("name")), tagNames.size()));

            return builder.in(root.get("id")).value(subquery);
        };
    }
}
