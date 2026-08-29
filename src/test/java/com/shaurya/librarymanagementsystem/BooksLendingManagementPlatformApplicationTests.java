package com.shaurya.librarymanagementsystem;

import com.shaurya.librarymanagementsystem.controllerTests.AuthorControllerTest;
import com.shaurya.librarymanagementsystem.controllerTests.BookControllerTest;
import com.shaurya.librarymanagementsystem.controllerTests.BorrowRecordControllerTest;
import com.shaurya.librarymanagementsystem.controllerTests.MemberControllerTest;
import com.shaurya.librarymanagementsystem.serviceTests.AuthorServiceTest;
import com.shaurya.librarymanagementsystem.serviceTests.BookServiceTest;
import com.shaurya.librarymanagementsystem.serviceTests.BorrowRecordServiceTest;
import com.shaurya.librarymanagementsystem.serviceTests.MemberServiceTest;
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
