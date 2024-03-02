package com.example.api.repository;

import com.example.api.model.entity.Customer;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, JpaSpecificationExecutor<Customer> {
    Optional<Customer> findByLogin(String login);

    boolean existsByLogin(String login);

    default Page<Customer> searchByDateOfBirthAndPhoneNumberAndFullNameAndEmail(Optional<LocalDate> dateOfBirth, Optional<String> phoneNumber,
                                                                                Optional<String> fullName, Optional<String> email, Pageable pageable) {
        return findAll((root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            dateOfBirth.ifPresent(dob -> {
                if (StringUtils.hasText(dob.toString())) {
                    predicates.add(criteriaBuilder.greaterThan(root.get("dateOfBirth"), dob));
                }
            });

            phoneNumber.ifPresent(pn -> {
                if (StringUtils.hasText(pn)) {
                    predicates.add(criteriaBuilder.equal(root.join("phoneNumberList").get("value"), pn));
                }
            });

            fullName.ifPresent(fn -> {
                if (StringUtils.hasText(fn)) {
                    predicates.add(criteriaBuilder.like(root.get("fullName"), fn + "%"));
                }
           });

            email.ifPresent(e -> {
                if (StringUtils.hasText(e)) {
                    predicates.add(criteriaBuilder.equal(root.join("emailList").get("value"), e));
                }
            });

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }
}
