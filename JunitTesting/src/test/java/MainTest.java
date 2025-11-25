package test.java;
import main.java.HT;
import java.util.*;
import main.java.Main;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {
    private HT docA;
    private HT docB;
    private HT emptyDoc;
    private List<HT> pages;

    //Set up/Tear down
    @BeforeEach
    void setUp() {
        docA = new HT();
        docA.add("apple");
        docA.add("apple");
        docA.add("banana");

        docB = new HT();
        docB.add("apple");
        docB.add("cherry");
        docB.add("cherry");

        emptyDoc = new HT();

        pages = new ArrayList<>();
        pages.add(docA);
        pages.add(docB);
    }

    @AfterEach
    void tearDown() {
        docA = null;
        docB = null;
        emptyDoc = null;
        pages = null;
    }


    //TF() Tests

    //TF1: Normal TF calculation
    @Test
    void testTF_Normal() {
        Main.TF(docA);
        assertEquals(2.0 / 3.0, docA.getNode("apple").value);
        assertEquals(1.0 / 3.0, docA.getNode("banana").value);
    }

    //TF2: Empty document
    @Test
    void testTF_EmptyDocument() {
        Main.TF(emptyDoc); // Should not crash
        assertEquals(0.0, emptyDoc.size);
    }

    // TF3: Null test
    @Test
    void testTF_Null() {
        assertThrows(NullPointerException.class, () -> Main.TF(null));
    }

    //IDF() Tests

    // IDF1: Standard IDF computation
    @Test
    void testIDF_Normal() {
        Main.TF(docA);
        Main.TF(docB);
        Main.IDF(docA, pages);
        Main.IDF(docB, pages);

        assertEquals(0.0, docA.getNode("apple").value);
        assertEquals(0.0, docB.getNode("apple").value);

        double expectedBanana = (1.0/3.0) * Math.log(2.0);
        assertEquals(expectedBanana, docA.getNode("banana").value);

        double expectedCherry = (2.0/3.0) * Math.log(2.0);
        assertEquals(expectedCherry, docB.getNode("cherry").value);
    }

    // IDF2: doc not empty, but pages is empty.
    @Test
    void testIDF_EmptyPages() {
        Main.TF(docA);
        List<HT> emptyList = new ArrayList<>();

        assertDoesNotThrow(() -> Main.IDF(docA, emptyList));
        assertEquals(2.0/3.0, docA.getNode("apple").value);
        assertEquals(1.0/3.0, docA.getNode("banana").value);
    }

    // IDF3: Empty everything
    @Test
    void testIDF_Empty() {
        HT doc = new HT();
        List<HT> emptyList = new ArrayList<>();

        assertDoesNotThrow(() -> Main.IDF(doc, emptyList));
    }

    // IDF3: Null test
    @Test
    void testIDF_Null() {
        assertThrows(NullPointerException.class, () -> Main.IDF(null, pages));
        assertThrows(NullPointerException.class, () -> Main.IDF(docA, null));
    }


    // Cosine Similarity Tests

    // CS1: Standard similarity
    @Test
    void testCosineSimilarity_NormalDocs() {
        Main.TF(docA);
        Main.TF(docB);
        Main.IDF(docA, pages);
        Main.IDF(docB, pages);

        double result = Main.cosineSimilarity(docA, docB);

        assertTrue(result >= 0 && result <= 1);
    }

    // CS2: doc1 empty, doc2 not empty
    @Test
    void testCosineSimilarity_Doc1Empty_Doc2NotEmpty() {
        Main.TF(emptyDoc);
        Main.TF(docA);

        double result = Main.cosineSimilarity(emptyDoc, docA);
        assertEquals(0.0, result);
    }

    // CS3: Null test
    @Test
    void testCosineSimilarity_Null() {
        assertThrows(NullPointerException.class, () -> Main.cosineSimilarity(docA, null));
        assertThrows(NullPointerException.class, () -> Main.cosineSimilarity(null, docB));
    }

    // CS4: doc2 empty, doc1 not empty
    @Test
    void testCosineSimilarity_Doc2Empty_Doc1NotEmpty() {
        Main.TF(docA);
        Main.TF(emptyDoc);

        double result = Main.cosineSimilarity(docA, emptyDoc);
        assertEquals(0.0, result);
    }

    // CS5: Both empty
    @Test
    void testCosineSimilarity_BothEmpty() {
        Main.TF(emptyDoc);
        HT anotherEmpty = new HT();
        Main.TF(anotherEmpty);

        double result = Main.cosineSimilarity(emptyDoc, anotherEmpty);
        assertEquals(0.0, result);
    }

    // CS6: Two identical documents
    @Test
    void testCosineSimilarity_IdenticalDocs() {
        HT docC = new HT();
        docC.add("apple");
        docC.add("apple");
        docC.add("banana");
        pages.add(docC);

        Main.TF(docA);
        Main.TF(docC);
        Main.IDF(docA, pages);
        Main.IDF(docC, pages);

        double result = Main.cosineSimilarity(docA, docC);

        assertEquals(1.0, result);
    }

    @Test
    void runAllTestsSuiteMarker() {
        assertTrue(true);
    }
}
