package com.shaurya.booksmanagementplatform.repositories;

import com.shaurya.booksmanagementplatform.model.entity.Book;
import com.shaurya.booksmanagementplatform.model.entity.BorrowRecord;
import com.shaurya.booksmanagementplatform.model.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BorrowRepository extends JpaRepository<BorrowRecord, Long> {

    List<BorrowRecord> findByMember(Member member);

    List<BorrowRecord> findByBook(Book book);

    List<BorrowRecord> findByMemberAndReturnDateIsNull(Member member);

    List<BorrowRecord> findByDueDateBeforeAndReturnDateIsNull(LocalDate today);

    long countByMemberAndReturnDateIsNull(Member member);

    boolean existsByMemberAndBookAndReturnDateIsNull(Member member, Book book);
}
