package com.shaurya.booksmanagementplatform;

import com.shaurya.booksmanagementplatform.controllerTests.AuthorControllerTest;
import com.shaurya.booksmanagementplatform.controllerTests.BookControllerTest;
import com.shaurya.booksmanagementplatform.controllerTests.BorrowRecordControllerTest;
import com.shaurya.booksmanagementplatform.controllerTests.MemberControllerTest;
import com.shaurya.booksmanagementplatform.serviceTests.AuthorServiceTest;
import com.shaurya.booksmanagementplatform.serviceTests.BookServiceTest;
import com.shaurya.booksmanagementplatform.serviceTests.BorrowRecordServiceTest;
import com.shaurya.booksmanagementplatform.serviceTests.MemberServiceTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AuthorServiceTest.class,
        BookServiceTest.class,
        BorrowRecordServiceTest.class,
        MemberServiceTest.class,
        MemberControllerTest.class,
        BookControllerTest.class,
        BorrowRecordControllerTest.class,
        AuthorControllerTest.class
})
public class BooksLendingManagementPlatformApplicationTests {
    // Run this class as a JUnit test suite to execute all specified service layer unit tests.
}
